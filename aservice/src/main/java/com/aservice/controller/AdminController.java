package com.aservice.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aservice.dao.UserDao;
import com.aservice.entity.User;
import com.aservice.util.ListModifier;
import com.aservice.util.OfferListModifier;
import com.aservice.util.SharedUtil;
import com.aservice.util.UserListModifier;
import com.aservice.util.UserUtil;
import com.aservice.util.UserUtil.UserConst;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@GetMapping("/panel")
	public String getAdminPanel() {
		return "admin-panel/admin-panel";
	}

	@GetMapping("/showusers")
	public String showUsers(Model model) {
		
		ListModifier listModifier = new UserListModifier();
		
		model.addAttribute("listModifier", listModifier);
		
		
		return "admin-panel/user-list";
	}
	
	@GetMapping("/list/view/{task}")
	public String viewAllUsers(@PathVariable("task") String task,
							   @ModelAttribute UserListModifier listModifier,
							   Model model) {
		
		// if thymeleaf parses object with comma at the beginning
		if(SharedUtil.checkIfFilterValid(listModifier.getFilter()) && listModifier.getFilter().startsWith(",")) {
			listModifier.setFilter(listModifier.getFilter().substring(1));
		}
		
		// user button click
		switch (task) {
		case "show": {
			listModifier.setShowClicked();
			break;
		}
		case "left": {
			if(listModifier.getPreviousPage()<=0) return "redirect:/admin/showusers";
			listModifier.setStartingRow(listModifier.getStartingRow()-listModifier.getLimit());
			listModifier.decrement();
			break;
		}
		case "right":{
			if(!listModifier.getIsNext()) return "redirect:/admin/showusers";
			listModifier.setStartingRow(listModifier.getStartingRow()+listModifier.getLimit());
			listModifier.increment();
			break;
		}
		default:
			if(task.equals("id") || task.equals("username")) {
				listModifier.setComparingMethod(task);
				break;
			}
			return "redirect:/admin/panel";
		}
		
		List<User> dbUsers = null;
		dbUsers = userDao.getPagedUsers(listModifier);
		listModifier.setStartingRow(listModifier.getStartingRow()+listModifier.getLimit());
		if(userDao.getPagedUsers(listModifier)!=null) {
			System.out.println("USERS: "+userDao.getPagedUsers(listModifier));
			listModifier.setIsNext(true);
		}
		else 
			listModifier.setIsNext(false);
		listModifier.setStartingRow(listModifier.getStartingRow()-listModifier.getLimit());
		
		if(dbUsers==null) dbUsers = new ArrayList<>();
		
		System.out.println(listModifier.getIsNext());
		dbUsers.forEach(user->System.out.println(user));
		
		model.addAttribute("users",dbUsers);
		model.addAttribute("listModifier",listModifier);
		
		return "admin-panel/user-list";
	}
	
	@GetMapping("/user/delete/form/{id}")
	public String deleteUserForm(@PathVariable("id") int userId,Model model) {
		
		User userToDelete = userDao.getUserById(userId);
		
		model.addAttribute("username", userToDelete.getUsername());
		model.addAttribute("userToDelId", userId);
		
		return "admin-panel/delete-user-confirmation";
	}
	
	@PostMapping("/user/delete")
	public String deleteUser(@RequestParam("passwd") String passwd,
							 @RequestParam("userToDelId") int userToDelId, Model model,
							 RedirectAttributes redirectAttributes) {
		User currentUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		if(!passwordEncoder.matches(passwd, currentUser.getPassword())) {
			User userToDelete = userDao.getUserById(userToDelId);
			
			model.addAttribute("username", userToDelete.getUsername());
			model.addAttribute("userToDelId", userToDelId);
			model.addAttribute("credentials","fail");
			
			return "admin-panel/delete-user-confirmation";
		}
		
		userDao.deleteUser(userDao.getUserById(userToDelId));
		redirectAttributes.addFlashAttribute("info", "deleted");
		
		return "redirect:/admin/showusers";
	}
	
	@GetMapping("/user/editpasswd/form/{id}")
	public String editPasswdUserForm(@PathVariable("id") int userId,Model model) {
		
		User userToEdit = userDao.getUserById(userId);
		
		model.addAttribute("username", userToEdit.getUsername());
		model.addAttribute("userToEditId", userId);
		
		return "admin-panel/edit-user-password";
	}
	
	@PostMapping("/user/editpasswd")
	public String editPasswdUser(@RequestParam(name="passwd") String passwd,
								 @RequestParam(name="passwdC") String passwdC,
							 	 @RequestParam("userToEditId") int userToEditId,		
							 	 Model model, RedirectAttributes redirectAttributes) {
		
		if(!passwd.equals(passwdC) || passwd.length()<UserConst.PASSWD_LOW_BOUND.getValue() 
				|| passwd.length()>UserConst.PASSWD_UP_BOUND.getValue()) {
			User userToEdit = userDao.getUserById(userToEditId);
			
			model.addAttribute("username", userToEdit.getUsername());
			model.addAttribute("userToEditId", userToEditId);
			model.addAttribute("credentials","fail");
			
			return "admin-panel/edit-user-password";
		}
		
		User userToEdit = userDao.getUserById(userToEditId);
		userToEdit.setPassword(passwordEncoder.encode(passwd));
		userDao.addUser(userToEdit);
		
		redirectAttributes.addFlashAttribute("info", "edited");
		
		return "redirect:/admin/showusers";
	}
}
