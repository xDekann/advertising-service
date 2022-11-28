package com.aservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	@GetMapping("/") // jak ktos wpisze samo localhost, to od razu odsyla do metody w LoginControllerze
	public String showStartingPage() {
		return "redirect:/login/start";
	}
}
