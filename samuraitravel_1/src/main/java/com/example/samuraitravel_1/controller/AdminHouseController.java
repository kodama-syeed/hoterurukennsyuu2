package com.example.samuraitravel_1.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel_1.entity.House;
import com.example.samuraitravel_1.form.HouseRegisterForm;
import com.example.samuraitravel_1.repository.HouseRepository;
import com.example.samuraitravel_1.service.HouseService;

@Controller

@RequestMapping("/admin/houses")
public class AdminHouseController {

	private final HouseService houseService;
	private final HouseRepository houseRepository;

	public AdminHouseController(HouseRepository houseRepository, HouseService houseService) {

		this.houseRepository = houseRepository;

		this.houseService = houseService;
	}

	@GetMapping

	public String index(Model model,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			@RequestParam(name = "keyword", required = false) String keyword) {

		Page<House> housePage = houseRepository.findAll(pageable);

		if (keyword != null && !keyword.isEmpty()) {
			housePage = houseRepository.findByNameLike("%" + keyword + "%", pageable);

		} else {

			housePage = houseRepository.findAll(pageable);

		}

		model.addAttribute("housePage", housePage);

		model.addAttribute("keyword", keyword);

		return "admin/houses/index";

	}

	@GetMapping("/{id}")

	public String show(@PathVariable(name = "id") Integer id, Model model) {
		House house = houseRepository.getReferenceById(id);

		model.addAttribute("house", house);

		return "admin/houses/show";
	}

	@GetMapping("/register")

	public String register(Model model) {

		model.addAttribute("houseRegisterForm", new HouseRegisterForm());

		return "admin/houses/register";

	}

	@PostMapping("/create")

	public String create(@ModelAttribute @Validated HouseRegisterForm houseRegisterForm, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {

			return "admin/houses/register";

		}
		houseService.create(houseRegisterForm);

		redirectAttributes.addAttribute("successMessage", "民宿を登録しました。");

		return "redirect:/admin/houses";

	}

}
