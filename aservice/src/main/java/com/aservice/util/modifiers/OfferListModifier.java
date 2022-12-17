package com.aservice.util.modifiers;


import org.springframework.stereotype.Component;

import com.aservice.util.OfferUtil.OfferConst;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OfferListModifier extends OfferModifier {
	
	// rows to change
	private final int limit = OfferConst.ROWS_PER_PAGE.getValue();
	
	public OfferListModifier(boolean wantSubbedList, boolean wantOwnOffers) {
		this.wantSubbedList=wantSubbedList;
		this.wantOwnOffers=wantOwnOffers;
	}
}
