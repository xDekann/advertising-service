package com.aservice.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.aservice.dao.OfferDao;
import com.aservice.dao.UserDao;
import com.aservice.entity.Offer;
import com.aservice.entity.User;


@Controller
@RequestMapping("/main")
public class MainController {
	
	@Autowired
	private UserDao userDAO;
	@Autowired
	private OfferDao offerDAO;
	

	@GetMapping("/") // po udanym zalogowaniu sie wchodzimy do tej metody (zdefiniowane w securityconfig)
	public String loggedIn(Model model) {
		
		Authentication userInfo = SecurityContextHolder.getContext().getAuthentication();
		String username = userInfo.getName();
		model.addAttribute("givenName", username);

		return "main/home";
	}
	
	@GetMapping("/creation/offer/form")
	public String createOfferForm(Model model) {
		
		Offer offer = new Offer();
		model.addAttribute("offer", offer);
		
		return "main/offer-form";
	}
	
	@PostMapping("/creation/offer/creation")
	public String createOffer(@ModelAttribute("offer") Offer offer,
			@RequestParam(value="imageParam", required = false) MultipartFile image) {
		
		
		// adding offer to the database
		offer.setDateOfCreation(new Timestamp(System.currentTimeMillis()));
		offer.setActive(true);
		Authentication userInfo = SecurityContextHolder.getContext().getAuthentication();
		String username = userInfo.getName();
		User dbUser = userDAO.getUserByUsername(username);
		dbUser.addOffer(offer);
		offerDAO.addOffer(offer);

		// saving image on server
		if(!image.getOriginalFilename().isEmpty()) {
			StringBuilder dirPath = new StringBuilder("src/main/resources/static/img/offer-images/"+dbUser.getId());
			new File(dirPath.toString()).mkdirs();
			dirPath.append("/"+offer.getId());
			new File(dirPath.toString()).mkdirs();
			Path filePath = Paths.get(dirPath.toString(), image.getOriginalFilename());
			try {
				Files.write(filePath,image.getBytes());
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		
		return "redirect:/main/";
	}

}
