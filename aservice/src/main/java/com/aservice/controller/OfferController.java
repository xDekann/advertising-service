package com.aservice.controller;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aservice.dao.OfferDao;
import com.aservice.entity.Offer;
import com.aservice.entity.User;
import com.aservice.util.OfferListModifier;
import com.aservice.util.OfferUtil;


@Controller
@RequestMapping("/offer")
public class OfferController {
	
	@Autowired
	private OfferDao offerDAO;

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
		int limit=4;
		List<Offer> dbOffers = null;
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
					|| task.equals("price") || task.equals("author")) {
				listModifier.setComparingMethod(task);
				break;
			}
			return "redirect:/offer/list";
		}
		
		
		dbOffers = offerDAO.getPagedOffers(listModifier.getStartingRow(), limit, listModifier.getFilter(), listModifier.getComparingMethod());
		if(offerDAO.getPagedOffers(listModifier.getStartingRow()+limit, limit, listModifier.getFilter(), listModifier.getComparingMethod())!=null)
			listModifier.setIsNext(true);
		else 
			listModifier.setIsNext(false);
		Map<Offer,String> offers = new LinkedHashMap<>();

		
		// only one image per offer supported, read that image and sort map by offer id
		if(dbOffers==null) return "redirect:/offer/list";
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
	@GetMapping("/list/pickedoffer/{id}")
	public String showOfferWithId(@PathVariable("id") int id, Model model) {
		
		Offer offer = offerDAO.getOfferById(id);
		User offerOwner = offer.getUser();
		String formattedDate = OfferUtil.getDateToMin(offer, offer.getDateOfCreation());
		
		File directory = new File("src/main/resources/static/img/offer-images/"
			 	  + offer.getUser().getId() +"/" + offer.getId() +"/");
		String[] fileNamesFromDir = directory.list();
			
		model.addAttribute("offer", offer);
		model.addAttribute("offerOwner", offerOwner);
		model.addAttribute("offerImages",fileNamesFromDir);
		model.addAttribute("date", formattedDate);
		
		return "offer/picked-offer";
	}
}
