package com.aservice.util;

import java.util.Comparator;

import com.aservice.entity.Offer;

public class OfferComparator implements Comparator<Offer> {

	@Override
	public int compare(Offer o1, Offer o2) {
		if(o1.getId()>o2.getId())
			return 1;
		if(o1.getId()==o2.getId())
			return 0;
		else
			return -1;
	}

}
