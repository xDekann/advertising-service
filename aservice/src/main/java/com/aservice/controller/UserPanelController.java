package com.aservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aservice.dao.UserDao;
import com.aservice.entity.User;
import com.aservice.util.UserUtil;

@Controller
@RequestMapping("/user")
public class UserPanelController {
	
	@Autowired
	private UserDao userDao;

	@GetMapping("/panel")
	public String getPanel(Model model) {
		return "user-panel/user-panel";
	}
	
}
