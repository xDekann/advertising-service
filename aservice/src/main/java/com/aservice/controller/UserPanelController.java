package com.aservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aservice.dao.UserDao;
import com.aservice.entity.User;
import com.aservice.entity.UserDetails;
import com.aservice.util.UserUtil;

import jakarta.validation.Valid;

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
			model.addAttribute("credentials","fail");
		}else {
			model.addAttribute("credentials","success");
			userToUpdate.setPassword(passwdEncoder.encode(passwdC));
			userDao.addUser(userToUpdate);
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
	public String modifyAccount(@Valid @ModelAttribute("userDetails") UserDetails userD, BindingResult bindingUserDetails,
								@RequestParam("passwdC") String passwordC, Model model) {
		
		if(bindingUserDetails.hasErrors())
			return "user-panel/modify-account";
		
		User userToUpdate = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		
		if(!passwdEncoder.matches(passwordC, userToUpdate.getPassword())) {
			model.addAttribute("credentials","fail");
			model.addAttribute("userDetails",userToUpdate.getUserDetails());
			return "user-panel/modify-account";
		}else {
			userToUpdate.connectUserDetails(userD);
			userDao.addUser(userToUpdate);
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
		
		if(!passwdEncoder.matches(password, userToDelete.getPassword())
				|| !passwdEncoder.matches(code, userToDelete.getResetCode())) {
			model.addAttribute("credentials","fail");
			return "user-panel/delete-account";
		}else
			userDao.deleteUser(userToDelete);

		return "redirect:/logout";
	}
	
	@GetMapping("/viewprofile/picked/{userId}")
	public String viewUserPickedProfile(@PathVariable("userId") int userId, Model model) {
		
		User pickedUser = userDao.getUserById(userId);
		String lastLogin = UserUtil.getDateToMin(pickedUser, pickedUser.getUserDetails().getLastLogin());
		
		model.addAttribute("pickedUser", pickedUser);
		model.addAttribute("pickedUserD", pickedUser.getUserDetails());
		model.addAttribute("lastLogin", lastLogin);
		
		return "user-panel/user-picked-profile";
	}
}
