package com.example.samuraitravel_1.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.samuraitravel_1.entity.User;

@Component
public class SignupEventPublisher {

	private final ApplicationEventPublisher applicationEventPublisher;

	public SignupEventPublisher(ApplicationEventPublisher applicationEventPublisher) {

		this.applicationEventPublisher = applicationEventPublisher;
	}

	public void publishSignupEvent(User user, String requestUrl) {

		applicationEventPublisher.publishEvent(new SignupEvent(this, user, requestUrl));

	}

}
