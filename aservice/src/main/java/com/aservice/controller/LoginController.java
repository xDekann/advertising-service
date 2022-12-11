package com.aservice.controller;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aservice.dao.AuthorityDao;
import com.aservice.dao.UserDao;
import com.aservice.entity.User;
import com.aservice.entity.UserDetails;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {
	
	@Autowired
	private PasswordEncoder passwdEncoder;
	@Autowired
	private AuthorityDao authorityDao;
	@Autowired
	private UserDao userDao;
	
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
	public String createUser(@Valid @ModelAttribute("user") User user, BindingResult bindUser,
			@Valid @ModelAttribute("userDetails") UserDetails userDetails, BindingResult bindUserDetails, Model model) {
		
		if(bindUser.hasErrors() || bindUserDetails.hasErrors()) {
			return "login-and-register/register";
		}
		
		if(userDao.getUserByEmail(userDetails.getEmail())!=null 
				|| userDao.getUserByUsername(user.getUsername())!=null) {
			model.addAttribute("userExists", "exists");
			return "login-and-register/register";
		}
		
		user.setEnabled(true);
		user.setPassword(passwdEncoder.encode(user.getPassword()));
		user.setResetCode(passwdEncoder.encode(user.getResetCode()));

		user.addAuthority(authorityDao.getAuthorityByName("ROLE_USER"));
		userDetails.setLastLogin(new Timestamp(System.currentTimeMillis()));
		user.connectUserDetails(userDetails);
		userDetails.connectUser(user);
		userDao.addUser(user);
		
		return "redirect:/login/start";
	}
	
	@GetMapping("/passwordreset/form")
	public String passwordResetForm(Model model) {	
		return "login-and-register/password-form";
	}
	
	@PostMapping("/passwordreset")
	public String passwordReset(@RequestParam("username") String username,
								@RequestParam("code") String code,
								@RequestParam("passwd") String password, Model model) 
	{
		User userToUpdate = userDao.getUserByUsername(username);
		
		if(!passwdEncoder.matches(code, userToUpdate.getResetCode()) 
				|| userToUpdate==null) {
			model.addAttribute("credentials", "fail");
		}else {
			userToUpdate.setPassword(passwdEncoder.encode(password));
			userDao.addUser(userToUpdate);
			model.addAttribute("credentials", "success");
		}
		
		return "login-and-register/password-form";
	}
}
