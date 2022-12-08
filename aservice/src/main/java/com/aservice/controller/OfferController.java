package com.aservice.controller;

import java.io.File;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.aservice.dao.OfferDao;
import com.aservice.dao.UserDao;
import com.aservice.entity.Offer;
import com.aservice.entity.Subscription;
import com.aservice.entity.User;
import com.aservice.util.OfferListModifier;
import com.aservice.util.OfferUtil;
import com.aservice.util.UserUtil;


@Controller
@RequestMapping("/offer")
public class OfferController {
	
	@Autowired
	private OfferDao offerDAO;
	@Autowired
	private UserDao userDao;

	@GetMapping("/list")
	public String listMenu(Model model) {
		OfferListModifier listModifier = new OfferListModifier();
		model.addAttribute("listModifier",listModifier);
		return "offer/offer-menu";
	}
	@GetMapping("/list/viewAll/{task}")
	public String viewAllOffers(@PathVariable("task") String task,
								@ModelAttribute OfferListModifier listModifier,
								Model model) {
		
		// display limit per site
		int limit=OfferUtil.OfferConst.ROWS_PER_PAGE.getValue();
		
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
			listModifier.setStartingRow(listModifier.getStartingRow()-limit);
			listModifier.decrement();
			break;
		}
		case "right":{
			if(!listModifier.getIsNext()) return "redirect:/offer/list";
			listModifier.setStartingRow(listModifier.getStartingRow()+limit);
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

		
		//List<Offer> debugOffers = null;
		int currentLoggedUserId = userDao.getUserByUsername(UserUtil.getLoggedUserName()).getId();
		List<Offer> dbOffers = null;
		dbOffers = offerDAO.getPagedOffers(listModifier.getStartingRow(), limit, listModifier, true, currentLoggedUserId);
		if(offerDAO.getPagedOffers(listModifier.getStartingRow()+limit, limit, listModifier, true, currentLoggedUserId)!=null)
			listModifier.setIsNext(true);
		else 
			listModifier.setIsNext(false);
		
		/*
		debugOffers = offerDAO.getPagedOffers(listModifier.getStartingRow()+limit, limit, listModifier, true, currentLoggedUserId);
		System.out.println("PAGI TEST!");
		System.out.println(debugOffers);
		debugOffers.forEach(offer->System.out.println("OFFER!: "+offer));
		*/
		
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
		
		
		return "offer/offer-menu";
	}
	@GetMapping("/list/pickedoffer/{id}/{followFail}")
	public String showOfferWithId(@PathVariable("id") int offerId, 
								  @PathVariable("followFail") boolean followFail, Model model) {
		
		System.out.println("RAZ");
		Offer offer = offerDAO.getOfferById(offerId);
		User offerOwner = offer.getUser();
		String formattedDate = OfferUtil.getDateToMin(offer, offer.getDateOfCreation());
		
		File directory = new File("src/main/resources/static/img/offer-images/"
			 	  + offer.getUser().getId() +"/" + offer.getId() +"/");
		String[] fileNamesFromDir = directory.list();
			
		model.addAttribute("offer", offer);
		model.addAttribute("offerOwner", offerOwner);
		model.addAttribute("offerImages",fileNamesFromDir);
		model.addAttribute("date", formattedDate);
		System.out.println("DWA");
		System.out.println("FOLLOW FAIL!"+followFail);
		boolean isSubbed=false;
		if(offerDAO.isSubbed(userDao.getUserByUsername(UserUtil.getLoggedUserName()).getId(), offerId))
			isSubbed = true;
		System.out.println("TRZY");
		model.addAttribute("isSubbed", isSubbed);
		model.addAttribute("failedToFollow", followFail);
		System.out.println("CZTERY");
		return "offer/picked-offer";
	}
	
	@GetMapping("/follow/{id}")
	public String addOfferToSubList(@PathVariable("id") int offerId, RedirectAttributes redirection){
		
		Offer pickedOffer = offerDAO.getOfferById(offerId);
		User currentUser = userDao.getUserByUsername(UserUtil.getLoggedUserName());
		
		if(offerDAO.isSubbed(currentUser.getId(), offerId)) {
			return "redirect:/offer/list/pickedoffer/"+offerId+"/true";
		}
		
		Subscription newSub = new Subscription(new Date(System.currentTimeMillis()), pickedOffer, currentUser);
		pickedOffer.addSub(newSub);
		currentUser.addSub(newSub);
		offerDAO.addSub(newSub);
		
		redirection.addFlashAttribute("followFailResp", false);
		
		return "redirect:/offer/list/pickedoffer/"+offerId+"/false";
	}
}
