package com.aservice.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aservice.dao.OfferDao;
import com.aservice.dao.UserDao;
import com.aservice.entity.Offer;
import com.aservice.entity.OfferReport;
import com.aservice.entity.User;
import com.aservice.util.ListModifier;
import com.aservice.util.OfferListModifier;
import com.aservice.util.OfferReportsModifier;
import com.aservice.util.SharedUtil;
import com.aservice.util.UserListModifier;
import com.aservice.util.UserUtil;
import com.aservice.util.UserUtil.UserConst;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private OfferDao offerDao;
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
			listModifier.setIsNext(true);
		}
		else 
			listModifier.setIsNext(false);
		listModifier.setStartingRow(listModifier.getStartingRow()-listModifier.getLimit());
		
		if(dbUsers==null) dbUsers = new ArrayList<>();
		
		model.addAttribute("users",dbUsers);
		model.addAttribute("listModifier",listModifier);
		
		return "admin-panel/user-list";
	}
	
	@GetMapping("/user/delete/form/{userId}")
	public String deleteUserForm(@PathVariable("userId") int userId,Model model) {
		
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
	
	@GetMapping("/user/editpasswd/form/{userId}")
	public String editPasswdUserForm(@PathVariable("userId") int userId,Model model) {
		
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
	
	@GetMapping("/offer/modify/form/{offerId}")
	public String modifyOfferForm(@PathVariable("offerId") int offerId, Model model) {
		
		Offer offerToModify = offerDao.getOfferById(offerId);
		model.addAttribute("offer",offerToModify);
		
		return "main/offer-form";
		
	}
	
	@PostMapping("/offer/modify/{offerOwnerId}")
	public String modifyOffer(@Valid @ModelAttribute("offer") Offer offer, BindingResult offerResult,
							  @PathVariable("offerOwnerId") int offerOwnerId) {
		
		if(offerResult.hasErrors())
			return "main/offer-form";
		User offerOwner = userDao.getUserById(offerOwnerId);
		// adding offer to the database
		offer.setDateOfCreation(new Timestamp(System.currentTimeMillis()));
		offer.setActive(true);
		offerOwner.addOffer(offer);
		offer.setSubs(offerDao.getAllSubsOfOffer(offer.getId()));
		offer.setReports(offerDao.getOfferReports(offer.getId()));
		offerDao.addOffer(offer);
		
		return "redirect:/main/";
	}
	
	@GetMapping("/offer/delete/form/{offerId}")
	public String deleteOfferForm(@PathVariable("offerId") int offerId, Model model) {
		
		Offer offerToDelete = offerDao.getOfferById(offerId);
		
		model.addAttribute("offerTitle", offerToDelete.getTitle());
		model.addAttribute("offerId", offerToDelete.getId());
		
		return "admin-panel/delete-offer-confirmation";
	}
	
	@PostMapping("/offer/delete")
	public String deleteOffer(@RequestParam("offerId") int offerId,
							  @RequestParam("passwd") String passwd, Model model,
							  RedirectAttributes redirectAttributes) {
		
		User currentUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		Offer offerToDelete = offerDao.getOfferById(offerId);
		
		if(!passwordEncoder.matches(passwd,currentUser.getPassword())){
			
			model.addAttribute("offerTitle", offerToDelete.getTitle());
			model.addAttribute("offerId", offerToDelete.getId());
			model.addAttribute("credentials","fail");
			
			return "admin-panel/delete-offer-confirmation";
		}
		
		StringBuilder dirPath = new StringBuilder("src/main/resources/static/img/offer-images/"
				  								  + offerToDelete.getUser().getId()
				  								  +"/"+offerToDelete.getId());

		Path offerDirPath = Path.of(dirPath.toString());
		
		if(Files.exists(offerDirPath)) {
			try {
				FileSystemUtils.deleteRecursively(offerDirPath);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}		
		
		offerDao.deleteOffer(offerDao.getOfferById(offerId));
		
		redirectAttributes.addFlashAttribute("info", "deleteOfferSuccess");
		return "redirect:/main/";
		
	}
	
	@GetMapping("/showreports/offers")
	public String showReportedOffers(Model model) {
		
		ListModifier listModifier = new OfferReportsModifier();
		
		model.addAttribute("listModifier", listModifier);
	
		return "admin-panel/reported-offer-list";
	}
	
	@GetMapping("/list/view/reportedOffers/{task}")
	public String viewReportedOffers(@PathVariable("task") String task,
									 @ModelAttribute OfferReportsModifier listModifier,
									 Model model) {
		// user button click
		switch (task) {
		case "show": {
			listModifier.setShowClicked();
			break;
		}
		case "left": {
			if(listModifier.getPreviousPage()<=0) return "redirect:/admin/showreports/offers";
			listModifier.setStartingRow(listModifier.getStartingRow()-listModifier.getLimit());
			listModifier.decrement();
			break;
		}
		case "right":{
			if(!listModifier.getIsNext()) return "redirect:/admin/showreports/offers";
			listModifier.setStartingRow(listModifier.getStartingRow()+listModifier.getLimit());
			listModifier.increment();
			break;
		}
		default:{
			if(task.equals("id"))
				listModifier.setComparingMethod(task);
			}
			break;
		}
		
		List<OfferReport> dbOffers = null;
		dbOffers = offerDao.getPagedReportedOffers(listModifier);
		listModifier.setStartingRow(listModifier.getStartingRow()+listModifier.getLimit());
		if(offerDao.getPagedReportedOffers(listModifier)!=null) {
			listModifier.setIsNext(true);
		}
		else 
			listModifier.setIsNext(false);
		listModifier.setStartingRow(listModifier.getStartingRow()-listModifier.getLimit());
		
		if(dbOffers==null) dbOffers = new ArrayList<>();
		
		model.addAttribute("offers",dbOffers);
		model.addAttribute("listModifier",listModifier);
		
		return "admin-panel/reported-offer-list";
	}
	
	
	@GetMapping("/reportedOffer/description/{offerReportId}")
	public String seeReportDescription(@PathVariable("offerReportId") int offerReportId, Model model) {
		OfferReport report = offerDao.getOfferReportById(offerReportId);
		
		model.addAttribute("description", report.getDescription());
		model.addAttribute("offerTitle", report.getOffer().getTitle());
		
		return "admin-panel/see-report-description";
	}
	
	@GetMapping("/reportedOffer/cancelReport/{offerReportId}")
	public String cancelReport(@PathVariable("offerReportId") int offerReportId) {
		OfferReport report = offerDao.getOfferReportById(offerReportId);
		offerDao.deleteReport(report);
		
		return "redirect:/admin/showreports/offers";
	}
}
