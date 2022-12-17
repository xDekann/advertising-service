package com.aservice.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.aservice.entity.Subscription;
import com.aservice.entity.User;
import com.aservice.util.OfferDeletionIdsKeeper;
import com.aservice.util.OfferUtil;
import com.aservice.util.SharedUtil;
import com.aservice.util.UserUtil;
import com.aservice.util.modifiers.OfferModifier;
import com.aservice.util.modifiers.OfferListModifier;

import jakarta.validation.Valid;

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
		
		OfferModifier listModifier = new OfferListModifier(subbed, ownOffers);
		
		model.addAttribute("listModifier",listModifier);
		
		return "offer/offer-menu";
	}
	
	@GetMapping("/list/view/{task}")
	public String viewAllOffers(@PathVariable("task") String task,
								@ModelAttribute OfferListModifier listModifier,
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
			return "redirect:/main/";
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
		if(dbOffers==null) dbOffers = new ArrayList<>();
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
		
		OfferDeletionIdsKeeper ids = new OfferDeletionIdsKeeper(offerId, ownerId);
		
		model.addAttribute("offerAndOwnerIds", ids);
		model.addAttribute("credentials", credentials);
		
		return "offer/delete-offer-confirmation";
	}
	
	@PostMapping("/delete")
	public String deleteOffer(@RequestParam("passwd") String passwdC,
							  @ModelAttribute("offerAndOwnerIds") OfferDeletionIdsKeeper ids, Model model,
							  RedirectAttributes redirectAttributes) {
		
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
		
		
		redirectAttributes.addFlashAttribute("info", "deleteOfferSuccess");
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
	
	@GetMapping("/report/{id}")
	public String reportOffer(@PathVariable("id") int offerId, Model model) {
		
		if(offerDao.getOfferReportsAmount(offerId)>=OfferUtil.OfferConst.OFFER_REPORT_LIMIT.getValue()) {
			model.addAttribute("info", "reportLimit");
			return "main/home";
		}
		
		OfferReport newReport = new OfferReport();
		model.addAttribute("report", newReport);
		model.addAttribute("offerId", offerId);
		
		return "offer/report-offer";
	}
	
	@PostMapping("report/submit")
	public String reportSubmit(@RequestParam("offerId") int offerId,
							   @Valid @ModelAttribute("report") OfferReport offerReport,
							   BindingResult bindReport, Model model) {
		
		if(bindReport.hasErrors()) {
			model.addAttribute("offerId", offerId);
			return "offer/report-offer";
		}
		
		Offer offerToReport = offerDao.getOfferById(offerId);
		offerToReport.addReport(offerReport);
		offerReport.setOffer(offerToReport);
		offerDao.addOffer(offerToReport);
		
		model.addAttribute("info", "reportSuccess");
		
		return "main/home";
		
	}
}
