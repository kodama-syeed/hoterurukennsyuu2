package com.example.samuraitravel_3.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel_3.entity.House;
import com.example.samuraitravel_3.form.ReservationInputForm;
import com.example.samuraitravel_3.repository.HouseRepository;
import com.example.samuraitravel_3.service.ReservationService;

@Controller
@RequestMapping("/houses")
public class HouseController {

	private final HouseRepository houseRepository;
	private final ReservationService reservationService;

	public HouseController(
			HouseRepository houseRepository,
			ReservationService reservationService) {

		this.houseRepository = houseRepository;
		this.reservationService = reservationService;
	}

	@GetMapping
	public String index(
			@RequestParam(name = "keyword", required = false) String keyword,
			@RequestParam(name = "area", required = false) String area,
			@RequestParam(name = "price", required = false) Integer price,
			@RequestParam(name = "order", required = false) String order,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {

		Page<House> housePage;

		// キーワード検索
		if (keyword != null && !keyword.isEmpty()) {

			if (order != null && order.equals("priceAsc")) {

				housePage = houseRepository
						.findByNameLikeOrAddressLikeOrderByPriceAsc(
								"%" + keyword + "%", "%" + keyword + "%", pageable);

			} else if (order != null && order.equals("popularityDesc")) {

				housePage = houseRepository
						.findByKeywordOrderByTotalNumberOfPeopleDesc(
								"%" + keyword + "%", pageable);

			} else {

				housePage = houseRepository
						.findByNameLikeOrAddressLikeOrderByCreatedAtDesc(
								"%" + keyword + "%", "%" + keyword + "%", pageable);
			}

			// エリア検索
		} else if (area != null && !area.isEmpty()) {

			if (order != null && order.equals("priceAsc")) {

				housePage = houseRepository
						.findByAddressLikeOrderByPriceAsc(
								"%" + area + "%", pageable);

			} else if (order != null && order.equals("popularityDesc")) {

				housePage = houseRepository
						.findByAreaOrderByTotalNumberOfPeopleDesc(
								"%" + area + "%", pageable);

			} else {

				housePage = houseRepository
						.findByAddressLikeOrderByCreatedAtDesc(
								"%" + area + "%", pageable);
			}

			// 価格検索
		} else if (price != null) {

			if (order != null && order.equals("priceAsc")) {

				housePage = houseRepository
						.findByPriceLessThanEqualOrderByPriceAsc(
								price, pageable);

			} else if (order != null && order.equals("popularityDesc")) {

				housePage = houseRepository
						.findByPriceOrderByTotalNumberOfPeopleDesc(
								price, pageable);

			} else {

				housePage = houseRepository
						.findByPriceLessThanEqualOrderByCreatedAtDesc(
								price, pageable);
			}

			// 検索条件なし
		} else {

			if (order != null && order.equals("priceAsc")) {

				housePage = houseRepository
						.findAllByOrderByPriceAsc(pageable);

			} else if (order != null && order.equals("popularityDesc")) {

				housePage = houseRepository
						.findAllOrderByTotalNumberOfPeopleDesc(pageable);

			} else {

				housePage = houseRepository
						.findAllByOrderByCreatedAtDesc(pageable);
			}
		}

		List<Object[]> reservationUsers = reservationService.reservationUsers();

		model.addAttribute("housePage", housePage);
		model.addAttribute("keyword", keyword);
		model.addAttribute("area", area);
		model.addAttribute("price", price);
		model.addAttribute("order", order);
		model.addAttribute("reservationUsers", reservationUsers);

		return "houses/index";
	}

	@GetMapping("/{id}")
	public String show(
			@PathVariable(name = "id") Integer id,
			Model model) {

		House house = houseRepository.getReferenceById(id);

		model.addAttribute("house", house);
		model.addAttribute(
				"reservationInputForm",
				new ReservationInputForm());

		return "houses/show";
	}
}