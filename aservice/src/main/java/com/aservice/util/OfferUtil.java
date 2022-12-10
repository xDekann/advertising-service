package com.aservice.util;

import java.sql.Timestamp;

import com.aservice.entity.Offer;

public class OfferUtil {
	
	public enum OfferConst{
		ROWS_PER_PAGE(4);

		private final int value;
		
		private OfferConst(int value) {
			this.value=value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	public static boolean checkIfFilterValid(String filter) {
		if(filter==null) return false;
		if(filter.isEmpty()) return false;
		return true;
	}
	public static String getDateToMin(Offer offer, Timestamp time) {
		int start=0;
		int end=16;
		return offer.getDateOfCreation().toString().substring(start, end);
	}
}
