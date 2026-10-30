package com.example.samuraitravel_2.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.samuraitravel_2.entity.Reservation;
import com.example.samuraitravel_2.entity.User;
import com.example.samuraitravel_2.repository.ReservationRepository;
import com.example.samuraitravel_2.security.UserDetailsImpl;

@Controller

public class ReservationController {

	private final ReservationRepository reservationRepository;

	public ReservationController(ReservationRepository reservationRepository) {

		this.reservationRepository = reservationRepository;

	}

	@GetMapping("/reservations")

	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PageableDefault(page = 0, sort = "id", direction = Direction.ASC) Pageable pageable, Model model) {

		User user = userDetailsImpl.getUser();

		Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

		model.addAttribute("reservationPage", reservationPage);

		return "reservations/index";

	}

}
