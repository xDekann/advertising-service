package com.aservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aservice.dao.UserDao;
import com.aservice.entity.User;
import com.aservice.entity.UserDetails;
import com.aservice.util.UserUtil;

@Controller
@RequestMapping("/user")
public class UserPanelController {
	
	@Autowired
	private PasswordEncoder passwdEncoder;
	@Autowired
	private UserDao userDao;

	@GetMapping("/panel")
	public String getPanel(Model model) {
		return "user-panel/user-panel";
	}
	
	@GetMapping("/password/form")
	public String changePasswdForm() {
		return "user-panel/password-change-form";
	}
	
	@PostMapping("/password/change")
	public String changePasswd(@RequestParam("passwdO") String passwdO,
							   @RequestParam("passwd") String passwd,
							   @RequestParam("passwdC") String passwdC, Model model) {
		
		User userToUpdate = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		if(!passwd.equals(passwdC)
				|| !passwdEncoder.matches(passwdO, userToUpdate.getPassword())) {
			System.out.println("Failed to change password!");
			model.addAttribute("credentials","fail");
		}else {
			model.addAttribute("credentials","success");
			userToUpdate.setPassword(passwdEncoder.encode(passwdC));
			userDao.addUser(userToUpdate);
			System.out.println("Password change success!");
		}
		
		return "user-panel/password-change-form";
	}
	@GetMapping("/modify/form")
	public String modifyForm(Model model) {
		User userToUpdate = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		model.addAttribute("userDetails",userToUpdate.getUserDetails());
		return "user-panel/modify-account";
	}
	
	@PostMapping("/modify")
	public String modifyAccount(@ModelAttribute("userDetails") UserDetails userD,
								@RequestParam("passwdC") String passwordC, Model model) {
		User userToUpdate = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		if(!passwdEncoder.matches(passwordC, userToUpdate.getPassword())) {
			model.addAttribute("credentials","fail");
			model.addAttribute("userDetails",userToUpdate.getUserDetails());
			System.out.println("Failed to modify!");
			return "user-panel/modify-account";
		}else {
			userToUpdate.connectUserDetails(userD);
			userDao.addUser(userToUpdate);
			System.out.println("Succeeded to update!");
		}
		return "redirect:/user/panel";
	}
	
	@GetMapping("/delete/form")
	public String deleteForm(Model model){
		return "user-panel/delete-account";
	}
	
	@PostMapping("/delete")
	public String deleteAccount(@RequestParam("passwd") String password,
								@RequestParam("code") String code, Model model) {
		User userToDelete = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		System.out.println(password+" "+code+" "+userToDelete.getPassword()+" "+userToDelete.getResetCode());
		if(!passwdEncoder.matches(password, userToDelete.getPassword())
				|| !passwdEncoder.matches(code, userToDelete.getResetCode())) {
			System.out.println("Failed to delete user!");
			model.addAttribute("credentials","fail");
			return "user-panel/delete-account";
		}else {
			userDao.deleteUser(userToDelete);
			return "redirect:/logout";
		}
	}
}
