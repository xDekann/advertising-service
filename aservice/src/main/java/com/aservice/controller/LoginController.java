package com.aservice.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aservice.dao.AuthorityDAO;
import com.aservice.dao.UserDAO;
import com.aservice.entity.User;
import com.aservice.entity.UserDetails;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	private PasswordEncoder passwdEncoder;
	@Autowired
	private AuthorityDAO authorityDAO;
	@Autowired
	private UserDAO userDAO;
	
	@GetMapping("/start")
	public String showStartingPage() {
		return "login-and-register/start";
	}
	@GetMapping("/showLoginPage")
	public String showLoginPage() {
		return "login-and-register/login-form";
	}
	@GetMapping("/access-denied")
	public String accessDenied() {
		return "login-and-register/access-denied";
	}
	@GetMapping("/registerForm")
	public String registerForm(Model model) {
		
		User user = new User();
		UserDetails userDetails = new UserDetails();
		
		model.addAttribute("user", user);
		model.addAttribute("userDetails", userDetails);
		
		
		return "login-and-register/register";
	}
	
	@PostMapping("/registerForm/creation")
	public String createUser(@ModelAttribute("user") User user,
			@ModelAttribute("userDetails") UserDetails userDetails) {
		
		System.out.println("Proces rejestracji");
		
		user.setEnabled(true);
		user.setPassword(passwdEncoder.encode(user.getPassword()));
		user.addAuthority(authorityDAO.getAuthorityByName("ROLE_USER"));
		userDetails.setLastLogin(new Date(System.currentTimeMillis()));
		user.connectUserDetails(userDetails);
		userDetails.connectUser(user);
		userDAO.addUser(user);
		
		System.out.println("Proces rejestracji zakonczony");
		
		return "redirect:/login/start";
	}
}
