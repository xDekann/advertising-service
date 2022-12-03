package com.aservice.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aservice.dao.OfferDao;
import com.aservice.entity.Offer;
import com.aservice.util.OfferComparator;


@Controller
@RequestMapping("/offer")
public class OfferController {
	
	@Autowired
	private OfferDao offerDAO;

	@GetMapping("/list")
	public String listMenu() {
		return "offer/offer-menu";
	}
	@GetMapping("/list/viewAll")
	public String viewAllOffers(Model model) {
		
		List<Offer> dbOffers = offerDAO.getAllOffers();
		Map<Offer,String> offers = new TreeMap<>(new OfferComparator());
		
		// only one image per offer supported, read that image and sort map by offer id
		dbOffers.forEach(offer->{
			File directory = new File("src/main/resources/static/img/offer-images/"
								 	  + offer.getUser().getId() +"/" + offer.getId() +"/");
			String[] fileNamesFromDir = directory.list();
			if(fileNamesFromDir!=null) 
				offers.put(offer,fileNamesFromDir[0]);
			else{
				offers.put(offer,null);
			}
		});
		
		model.addAttribute("offers",offers);
		
		return "offer/offer-menu";
	}
}
