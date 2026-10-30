
package com.example.samuraitravel_2.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.samuraitravel_2.form.ReservationRegisterForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripeService {

	@Value("${stripe.api-key}")
	private String stripeApiKey;

	private final ReservationService reservationService;

	public StripeService(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	public String createStripeSession(String houseName,
			ReservationRegisterForm reservationRegisterForm,
			HttpServletRequest httpServletRequest) {

		Stripe.apiKey = stripeApiKey;

		String requestUrl = new String(httpServletRequest.getRequestURL());

		SessionCreateParams params = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
				.addLineItem(SessionCreateParams.LineItem.builder()
						.setPriceData(SessionCreateParams.LineItem.PriceData.builder()
								.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
										.setName(houseName)
										.build())
								.setUnitAmount((long) reservationRegisterForm.getAmount())
								.setCurrency("jpy")
								.build())
						.setQuantity(1L)
						.build())
				.setMode(SessionCreateParams.Mode.PAYMENT)
				.setSuccessUrl(
						requestUrl.replaceAll("/houses/[0-9]+/reservations/create", "")
								+ "/reservations?reserved")
				.setCancelUrl(
						requestUrl.replace("/reservations/create", "/reservations/confirm"))
				.setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
						.putMetadata("houseId",
								reservationRegisterForm.getHouseId().toString())
						.putMetadata("userId",
								reservationRegisterForm.getUserId().toString())
						.putMetadata("checkinDate",
								reservationRegisterForm.getCheckinDate())
						.putMetadata("checkoutDate",
								reservationRegisterForm.getCheckoutDate())
						.putMetadata("numberOfPeople",
								reservationRegisterForm.getNumberOfPeople().toString())
						.putMetadata("amount",
								reservationRegisterForm.getAmount().toString())
						.build())
				.build();

		try {
			Session session = Session.create(params);
			return session.getUrl();

		} catch (StripeException e) {
			e.printStackTrace();
			System.err.println("Stripeエラーコード: " + e.getCode());
			System.err.println("Stripeエラーメッセージ: " + e.getMessage());
			return "";
		}
	}

	public void processSessionCompleted(Event event) {

		System.out.println("① 決済完了イベントの処理開始");

		System.out.println("Stripe APIキー読み込み確認: "
				+ (stripeApiKey != null && !stripeApiKey.isBlank()));

		System.out.println("Stripe APIキーの文字数: "
				+ (stripeApiKey == null ? 0 : stripeApiKey.length()));

		Stripe.apiKey = stripeApiKey;

		System.out.println("Stripe.apiKey設定確認: "
				+ (Stripe.apiKey != null && !Stripe.apiKey.isBlank()));

		try {
			// Stripeイベントの生データを文字列として取得
			String rawJson = event.getDataObjectDeserializer().getRawJson();

			// JSONからCheckout SessionのIDを取得
			Pattern pattern = Pattern.compile(
					"\"id\"\\s*:\\s*\"([^\"]+)\"");

			Matcher matcher = pattern.matcher(rawJson);

			if (!matcher.find()) {
				throw new IllegalStateException(
						"Checkout SessionのIDを取得できませんでした");
			}

			String sessionId = matcher.group(1);

			System.out.println("② Checkout Session ID: " + sessionId);

			// Session IDを使ってStripeからセッションを再取得
			Session session = Session.retrieve(sessionId);

			// PaymentIntentの情報を取得
			String paymentIntentId = session.getPaymentIntent();

			if (paymentIntentId == null || paymentIntentId.isBlank()) {
				throw new IllegalStateException(
						"PaymentIntentのIDを取得できませんでした");
			}

			com.stripe.model.PaymentIntent paymentIntent = com.stripe.model.PaymentIntent.retrieve(paymentIntentId);

			// 予約情報のメタデータを取得
			java.util.Map<String, String> paymentIntentObject = paymentIntent.getMetadata();

			System.out.println("③ PaymentIntentのメタデータ: "
					+ paymentIntentObject);

			// 予約をデータベースに登録
			reservationService.create(paymentIntentObject);

			System.out.println("④ 予約登録処理が終了しました");

		} catch (Exception e) {
			System.err.println("Stripeの予約登録処理でエラーが発生しました");
			e.printStackTrace();

			throw new IllegalStateException(
					"予約登録に失敗しました", e);
		}
	}
}
