package com.example.samuraitravel_2.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.samuraitravel_2.service.StripeService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

@Controller
public class StripeWebhookController {

	private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

	private final StripeService stripeService;

	private final String webhookSecret;

	public StripeWebhookController(
			StripeService stripeService,
			@Value("${stripe.webhook-secret}") String webhookSecret) {

		logger.info("StripeWebhookControllerのコンストラクタを実行しました");

		this.stripeService = stripeService;
		this.webhookSecret = webhookSecret;

		logger.info("Webhook Secretが読み込まれています：{}",
				webhookSecret != null && webhookSecret.startsWith("whsec_"));

		logger.info("Webhook Secretの文字数：{}",
				webhookSecret == null ? 0 : webhookSecret.length());
	}

	@PostMapping("/stripe/webhook")
	public ResponseEntity<String> webhook(
			@RequestBody String payload,
			@RequestHeader("Stripe-Signature") String sigHeader) {

		logger.info("Webhook受信：payload文字数={}", payload.length());
		logger.info("Stripe-Signatureヘッダー受信：{}", sigHeader != null);
		logger.info("Webhook Secret文字数={}", webhookSecret.length());

		try {
			String fingerprint = HexFormat.of().formatHex(
					MessageDigest.getInstance("SHA-256")
							.digest(webhookSecret.getBytes(StandardCharsets.UTF_8)));

			logger.info("Webhook Secret SHA-256：{}", fingerprint);

		} catch (Exception e) {
			throw new IllegalStateException(e);
		}

		Event event;

		try {
			event = Webhook.constructEvent(
					payload, sigHeader, webhookSecret);

		} catch (SignatureVerificationException e) {
			logger.error("Stripeの署名検証に失敗しました。", e);

			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body("Invalid signature");
		}

		if ("checkout.session.completed".equals(event.getType())) {

			try {
				stripeService.processSessionCompleted(event);

			} catch (Exception e) {
				logger.error("Stripeの予約登録処理に失敗しました。", e);

				return ResponseEntity
						.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Reservation processing failed");
			}
		}

		return ResponseEntity.ok("Success");
	}
}