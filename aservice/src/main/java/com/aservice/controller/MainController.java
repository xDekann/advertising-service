package com.aservice.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.aservice.dao.OfferDao;
import com.aservice.dao.UserDao;
import com.aservice.entity.Offer;
import com.aservice.entity.User;
import com.aservice.util.UserUtil;

import jakarta.validation.Valid;


@Controller
@RequestMapping("/main")
public class MainController {
	
	@Autowired
	private UserDao userDao;
	@Autowired
	private OfferDao offerDao;
	

	@GetMapping("/")
	public String loggedIn(Model model) {
		
		User userToUpdate = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		model.addAttribute("givenName", UserUtil.getLoggedUserName());
		userToUpdate.getUserDetails().setLastLogin(new Timestamp(System.currentTimeMillis()));
		userDao.addUser(userToUpdate);
		
		return "main/home";
	}
	
	@GetMapping("/creation/offer/form/{offerId}/{userId}")
	public String createOfferForm(@PathVariable("offerId") int offerId,
								  @PathVariable("userId") int userId, Model model) {
		
		Offer offer = new Offer();
		
		if(userId==userDao.getUserByUsername(UserUtil.getLoggedUserName()).getId()){
			offer = offerDao.getOfferById(offerId);
		}
		model.addAttribute("offer", offer);
		
		return "main/offer-form";
	}
	
	@PostMapping("/creation/offer/creation")
	public String createOffer(@Valid @ModelAttribute("offer") Offer offer,
			BindingResult bindingResult, 
			@RequestParam(value="imageParam", required = false) MultipartFile[] images) {
		
		if(bindingResult.hasErrors())
			return "main/offer-form";
		
		// adding offer to the database
		offer.setDateOfCreation(new Timestamp(System.currentTimeMillis()));
		offer.setActive(true);
		User dbUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		dbUser.addOffer(offer);
		offer.setSubs(offerDao.getAllSubsOfOffer(offer.getId()));
		offer.setReports(offerDao.getOfferReports(offer.getId()));
		offerDao.addOffer(offer);

		// saving images on server
		StringBuilder dirPath = new StringBuilder("src/main/resources/static/img/offer-images/"+dbUser.getId());
		
		// delete offer dir if exists (modify offer purposes)
		Path offerDirPath = Path.of(dirPath.toString()+"/"+offer.getId());
		if(Files.exists(offerDirPath)) {
			try {
				FileUtils.deleteDirectory(new File(offerDirPath.toString()));
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		
		if(!images[0].getOriginalFilename().isEmpty()) {
			new File(dirPath.toString()).mkdir();
			dirPath.append("/"+offer.getId());
			
			new File(dirPath.toString()).mkdirs();
			for(MultipartFile image : images) {
				Path filePath = Paths.get(dirPath.toString(), image.getOriginalFilename());
				try {
					Files.write(filePath,image.getBytes());
				} catch (IOException ioException) {
					ioException.printStackTrace();
				}
			}
		}
		
		return "redirect:/main/";
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public String maxUploadExceptionHandler(Model model) {
		
		model.addAttribute("info", "uploadFail");

		return "main/home";
	}
}
