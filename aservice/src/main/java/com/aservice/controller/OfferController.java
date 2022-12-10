package com.aservice.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileSystemUtils;
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
import com.aservice.entity.Subscription;
import com.aservice.entity.User;
import com.aservice.util.OfferDeletionIdsKeeper;
import com.aservice.util.OfferListModifier;
import com.aservice.util.OfferUtil;
import com.aservice.util.UserUtil;

import jakarta.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/offer")
public class OfferController {
	
	@Autowired
	private OfferDao offerDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private PasswordEncoder passwdEncoder;
		
	@GetMapping("/list/{subbed}/{ownOffers}")
	public String listMenuSubbed(@PathVariable("subbed") boolean subbed,
								@PathVariable("ownOffers") boolean ownOffers, Model model) {
		OfferListModifier listModifier = new OfferListModifier(subbed, ownOffers);
		System.out.println(listModifier.getWantSubbedList());
		model.addAttribute("listModifier",listModifier);
		return "offer/offer-menu";
	}
	
	@GetMapping("/list/view/{task}")
	public String viewAllOffers(@PathVariable("task") String task,
								@ModelAttribute OfferListModifier listModifier,
								Model model) {
		
		// if thymeleaf parses object with comma at the beginning
		if(OfferUtil.checkIfFilterValid(listModifier.getFilter()) && listModifier.getFilter().startsWith(",")) {
			listModifier.setFilter(listModifier.getFilter().substring(1));
		}
		
		// user button click
		switch (task) {
		case "show": {
			listModifier.setShowClicked();
			break;
		}
		case "left": {
			if(listModifier.getPreviousPage()<=0) return "redirect:/offer/list";
			listModifier.setStartingRow(listModifier.getStartingRow()-listModifier.getLimit());
			listModifier.decrement();
			break;
		}
		case "right":{
			if(!listModifier.getIsNext()) return "redirect:/offer/list";
			listModifier.setStartingRow(listModifier.getStartingRow()+listModifier.getLimit());
			listModifier.increment();
			break;
		}
		default:
			if(task.equals("id") || task.equals("title") || task.equals("dateOfCreation")
					|| task.equals("price")) {
				listModifier.setComparingMethod(task);
				break;
			}
			return "redirect:/offer/list";
		}

		int currentLoggedUserId = userDao.getUserByUsername(UserUtil.getLoggedUserName()).getId();
		List<Offer> dbOffers = null;
		dbOffers = offerDao.getPagedOffers(listModifier, true, currentLoggedUserId);
		
		// checking if there is a next page
		listModifier.setStartingRow(listModifier.getStartingRow()+listModifier.getLimit());
		if(offerDao.getPagedOffers(listModifier, true, currentLoggedUserId)!=null)
			listModifier.setIsNext(true);
		else 
			listModifier.setIsNext(false);
		listModifier.setStartingRow(listModifier.getStartingRow()-listModifier.getLimit());
		
		
		// only one image per offer supported, read that image and sort map by offer id
		if(dbOffers==null) return "redirect:/offer/list";
		Map<Offer,String> offers = new LinkedHashMap<>();
		dbOffers.forEach(offer->{
			File directory = new File("src/main/resources/static/img/offer-images/"
								 	  + offer.getUser().getId() +"/" + offer.getId() +"/");
			String[] fileNamesFromDir = directory.list();
			if(fileNamesFromDir!=null && fileNamesFromDir.length>0) 
				offers.put(offer,fileNamesFromDir[0]);
			else{
				offers.put(offer,null);
			}
		});
		
		model.addAttribute("offers",offers);
		model.addAttribute("listModifier",listModifier);
		model.addAttribute("loggedUserId", currentLoggedUserId);
		
		
		return "offer/offer-menu";
	}
	@GetMapping("/list/pickedoffer/{id}/{followFail}/{backToListTracker}")
	public String showOfferWithId(@PathVariable("id") int offerId, 
								  @PathVariable("followFail") boolean followFail,
								  @PathVariable("backToListTracker") boolean backTwice, Model model) {
		
		Offer offer = offerDao.getOfferById(offerId);
		
		// in case offer got deleted and user did not make a refresh
		if(offer==null) return "redirect:/main/";
		
		User currentLoggedUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		String formattedDate = OfferUtil.getDateToMin(offer, offer.getDateOfCreation());
		
		File directory = new File("src/main/resources/static/img/offer-images/"
			 	  + offer.getUser().getId() +"/" + offer.getId() +"/");
		String[] fileNamesFromDir = directory.list();
			
		model.addAttribute("offer", offer);
		model.addAttribute("currentUser", currentLoggedUser);
		model.addAttribute("offerImages",fileNamesFromDir);
		model.addAttribute("date", formattedDate);
	
		boolean isSubbed=false;
		if(offerDao.getSubbedOffer(userDao.getUserByUsername(UserUtil.getLoggedUserName()).getId(), offerId)!=null)
			isSubbed = true;

		model.addAttribute("isSubbed", isSubbed); // wykorzystaj do unfollow
		model.addAttribute("failedToFollow", followFail);
		model.addAttribute("backTwice", backTwice);

		return "offer/picked-offer";
	}
	
	@GetMapping("/follow/{id}")
	public String addOfferToSubList(@PathVariable("id") int offerId, RedirectAttributes redirection){
		
		Offer pickedOffer = offerDao.getOfferById(offerId);
		User currentUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		
		if(offerDao.getSubbedOffer(currentUser.getId(), offerId)!=null) {
			return "redirect:/offer/list/pickedoffer/"+offerId+"/true/true";
		}
		
		Subscription newSub = new Subscription(new Date(System.currentTimeMillis()), pickedOffer, currentUser);
		pickedOffer.addSub(newSub);
		currentUser.addSub(newSub);
		offerDao.addSub(newSub);
		
		return "redirect:/offer/list/pickedoffer/"+offerId+"/false/true";
	}
	
	@GetMapping("/unfollow/{id}")
	public String removeOfferFromSubList(@PathVariable("id") int offerId) {
		
		Offer pickedOffer = offerDao.getOfferById(offerId);
		User currentUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		
		Subscription dbSub = offerDao.getSubbedOffer(currentUser.getId(), offerId);
		offerDao.deleteSub(dbSub);
		
		return "main/home";
		
	}
	
	@GetMapping("/delete/form/{offerId}/{offerOwnerId}/{failedCredentials}")
	public String deleteOfferForm(@PathVariable("offerId") int offerId,
							  	  @PathVariable("offerOwnerId") int ownerId,
							  	  @PathVariable("failedCredentials") String credentials, Model model) {
		
		// in case of unauthorized deletion attempt
		if(ownerId!=userDao.getUserByUsername(UserUtil.getLoggedUserName()).getId())
			return "redirect:/main/";
		
		/*
		Offer offerToDelete = offerDAO.getOfferById(offerId);
		offerDAO.deleteOffer(offerToDelete);
		*/
		
		OfferDeletionIdsKeeper ids = new OfferDeletionIdsKeeper(offerId, ownerId);
		
		model.addAttribute("offerAndOwnerIds", ids);
		model.addAttribute("credentials", credentials);
		
		return "offer/delete-offer-confirmation";
	}
	
	@PostMapping("/delete")
	public String deleteOffer(@RequestParam("passwd") String passwdC,
							  @ModelAttribute("offerAndOwnerIds") OfferDeletionIdsKeeper ids, Model model) {
		
		User currentLoggedUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		if(!passwdEncoder.matches(passwdC, currentLoggedUser.getPassword()))
			return "redirect:/offer/delete/form/"+ids.getOfferId()+"/"+ids.getOwnerId()+"/fail";
		
		Offer offerToDelete = offerDao.getOfferById(ids.getOfferId());
		offerDao.deleteOffer(offerToDelete);

		StringBuilder dirPath = new StringBuilder("src/main/resources/static/img/offer-images/"
												  +currentLoggedUser.getId()
												  +"/"+offerToDelete.getId());
		
		Path offerDirPath = Path.of(dirPath.toString());
		if(Files.exists(offerDirPath)) {
			try {
				FileSystemUtils.deleteRecursively(offerDirPath);
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		
		return "redirect:/main/";
	}
	
	@GetMapping("/disable/{offerId}/{offerOwnerId}")
	public String disableOffer(@PathVariable("offerId") int offerId,
							   @PathVariable("offerOwnerId") int ownerId){
		if(ownerId!=userDao.getUserByUsername(UserUtil.getLoggedUserName()).getId())
			return "redirect:/main/";
		
		Offer dbOffer = offerDao.getOfferById(offerId);
		dbOffer.setActive(false);
		offerDao.addOffer(dbOffer);

		return "redirect:/main/";
		
	}
	
	@GetMapping("/enable/{offerId}/{offerOwnerId}")
	public String enableOffer(@PathVariable("offerId") int offerId,
							   @PathVariable("offerOwnerId") int ownerId){
		if(ownerId!=userDao.getUserByUsername(UserUtil.getLoggedUserName()).getId())
			return "redirect:/main/";
		
		Offer dbOffer = offerDao.getOfferById(offerId);
		dbOffer.setActive(true);
		offerDao.addOffer(dbOffer);

		return "redirect:/main/";
		
	}
}
