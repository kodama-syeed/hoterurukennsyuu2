package com.example.samuraitravel_3.service;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.samuraitravel_3.form.ReservationRegisterForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

@Service

public class StripeService {

	@Value("${stripe.api-key}")

	private String stripeApiKey;

	private final ReservationService reservationService;

	private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

	public StripeService(ReservationService reservationService) {

		this.reservationService = reservationService;

	}

	public String createStripeSession(String houseName, ReservationRegisterForm reservationRegisterForm,
			HttpServletRequest httpServletRequest) {

		Stripe.apiKey = stripeApiKey;

		String requestUrl = httpServletRequest.getScheme() + "://"
				+ httpServletRequest.getServerName()
				+ ":"
				+ httpServletRequest.getServerPort();

		SessionCreateParams params = SessionCreateParams.builder()
				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)

				.addLineItem(SessionCreateParams.LineItem.builder()

						.setPriceData(

								SessionCreateParams.LineItem.PriceData.builder()

										.setProductData(

												SessionCreateParams.LineItem.PriceData.ProductData.builder()

														.setName(houseName)

														.build())
										.setUnitAmount((long) reservationRegisterForm.getAmount())

										.setCurrency("jpy")

										.build())

						.setQuantity(1L)

						.build())

				.setMode(SessionCreateParams.Mode.PAYMENT)

				.setSuccessUrl(requestUrl + "/reservations?reserved")

				.setCancelUrl(requestUrl + "/houses/"
						+ reservationRegisterForm.getHouseId()
						+ "/reservations/confirm")

				.setPaymentIntentData(

						SessionCreateParams.PaymentIntentData.builder()

								.putMetadata("houseId", reservationRegisterForm.getHouseId().toString())

								.putMetadata("userId", reservationRegisterForm.getUserId().toString())

								.putMetadata("checkinDate", reservationRegisterForm.getCheckinDate())

								.putMetadata("checkoutDate", reservationRegisterForm.getCheckoutDate())

								.putMetadata("numberOfPeople", reservationRegisterForm.getNumberOfPeople().toString())

								.putMetadata("amount", reservationRegisterForm.getAmount().toString())

								.build())

				.build();

		try {
			Session session = Session.create(params);
			return session.getUrl();
		} catch (StripeException e) {
			e.printStackTrace();
			return "";
		}
	}

	public void processSessionCompleted(Event event) {

		logger.info("Webhookイベント受信: type={}", event.getType());

		Session eventSession = (Session) event.getData().getObject();

		String sessionId = eventSession.getId();
		if (sessionId == null || sessionId.isBlank()) {
			throw new IllegalStateException(
					"WebhookイベントからSession IDを取得できませんでした。");
		}

		logger.info("Session ID取得成功: {}", sessionId);

		SessionRetrieveParams params = SessionRetrieveParams.builder()
				.addExpand("payment_intent")
				.build();

		try {
			Session session = Session.retrieve(sessionId, params, null);

			if (session.getPaymentIntentObject() == null) {
				throw new IllegalStateException(
						"PaymentIntentを取得できませんでした。");
			}

			Map<String, String> metadata = session.getPaymentIntentObject().getMetadata();

			logger.info("Metadata取得成功: {}", metadata.keySet());

			reservationService.create(metadata);

			logger.info("予約登録処理が完了しました。");

		} catch (StripeException e) {
			logger.error("Stripe Session取得失敗", e);
			throw new IllegalStateException(
					"Stripe Sessionの取得に失敗しました。", e);
		}
	}
}
