package com.aservice.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
public class MainController {
	
	@GetMapping("/") // po udanym zalogowaniu sie wchodzimy do tej metody (zdefiniowane w securityconfig)
	public String loggedIn(Model model) {
		
		Authentication userInfo = SecurityContextHolder.getContext().getAuthentication();
		String username = userInfo.getName();
		model.addAttribute("givenName", username);
		
		return "main/home";
	}
}
