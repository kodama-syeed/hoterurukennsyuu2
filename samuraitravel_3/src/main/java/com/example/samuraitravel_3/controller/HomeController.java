package com.example.samuraitravel_3.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.samuraitravel_3.entity.House;
import com.example.samuraitravel_3.repository.HouseRepository;
import com.example.samuraitravel_3.service.ReservationService;

@Controller

public class HomeController {

	private final HouseRepository houseRepository;

	private final ReservationService reservationService;

	public HomeController(HouseRepository houseRepository, ReservationService reservationService) {

		this.houseRepository = houseRepository;

		this.reservationService = reservationService;

	}

	@GetMapping("/")

	public String index(Model model) {

		List<House> newHouses = houseRepository.findTop10ByOrderByCreatedAtDesc();

		model.addAttribute("newHouses", newHouses);

		return "index";

	}

}
