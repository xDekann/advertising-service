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

import com.aservice.dao.MessageDao;
import com.aservice.dao.UserDao;
import com.aservice.entity.User;
import com.aservice.entity.UserDetails;
import com.aservice.entity.UserReport;
import com.aservice.util.UserUtil;
import com.aservice.util.UserUtil.UserConst;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserPanelController {
	
	@Autowired
	private PasswordEncoder passwdEncoder;
	@Autowired
	private UserDao userDao;
	@Autowired
	private MessageDao messageDao;

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
		
		User checkIfUserByEmailExists = userDao.getUserByEmail(userD.getEmail());
		
		if(bindingUserDetails.hasErrors())
			return "user-panel/modify-account";
		
		User userToUpdate = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		
		if(checkIfUserByEmailExists!=null) {
			model.addAttribute("credentials","existsEmail");
			model.addAttribute("userDetails",userToUpdate.getUserDetails());
			return "user-panel/modify-account";
		}
		
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
		
		User pickedUser = userDao.getUserByIdWithParam(userId, "offers");
		User currentLoggedUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		boolean isBlocked=false;
		
		if(messageDao.getBlock(currentLoggedUser.getId(), userId)!=null)
			isBlocked=true;
		
		model.addAttribute("pickedUser", pickedUser);
		model.addAttribute("currentUser", currentLoggedUser);
		model.addAttribute("isBlocked", isBlocked);
		
		return "user-panel/user-picked-profile";
	}
	
	@GetMapping("/report/{id}")
	public String reportOffer(@PathVariable("id") int userId, Model model) {
		
		if(userDao.getUserReportsAmount(userId)>=UserConst.USER_REPORT_LIMIT.getValue()) {
			model.addAttribute("info", "reportUserLimit");
			model.addAttribute("givenName", UserUtil.getLoggedUserName());
			return "main/home";
		}
		
		UserReport newReport = new UserReport();
		model.addAttribute("report", newReport);
		model.addAttribute("userId", userId);
		
		return "user-panel/report-user";
	}
	
	@PostMapping("report/submit")
	public String reportSubmit(@RequestParam("userId") int userId,
							   @Valid @ModelAttribute("report") UserReport userReport,
							   BindingResult bindReport, Model model) {
		
		if(bindReport.hasErrors()) {
			model.addAttribute("userId", userId);
			return "user-panel/report-user";
		}
		
		User userToReport = userDao.getUserByIdWithParam(userId, "reports");
		userReport.setReportingUserId(userDao.getUserByUsername(UserUtil.getLoggedUserName()).getId());
		userToReport.addReport(userReport);
		userReport.setUser(userToReport);
		userDao.addUser(userToReport);

		model.addAttribute("info", "reportUserSuccess");
		model.addAttribute("givenName", UserUtil.getLoggedUserName());
		
		return "main/home";	
	}
}
