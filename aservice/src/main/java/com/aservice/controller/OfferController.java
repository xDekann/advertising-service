package com.aservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.aservice.dao.OfferDAO;
import com.aservice.entity.Offer;

@Controller
@RequestMapping("/offer")
public class OfferController {
	
	@Autowired
	private OfferDAO offerDAO;

	@GetMapping("/list")
	public String listMenu() {
		return "offer/offer-menu";
	}
	@GetMapping("/list/viewAll")
	public String viewAllOffers(Model model) {
		
		Map<Integer, Offer> offers = new HashMap<>();
		List<Offer> dbOffers = offerDAO.getAllOffers();
		dbOffers.forEach(offer->{
			offers.put(offer.getId(), offer);
		});
		
		model.addAttribute("offers",offers);
		
		return "offer/offer-menu";
	}
}
