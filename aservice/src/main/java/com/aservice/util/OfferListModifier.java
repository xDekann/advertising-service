package com.aservice.util;


import com.aservice.util.OfferUtil.OfferConst;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OfferListModifier extends ListModifier {
	
	private boolean wantSubbedList;
	private boolean wantOwnOffers;
	private final int limit = OfferConst.ROWS_PER_PAGE.getValue();
	
	public OfferListModifier(boolean wantSubbedList, boolean wantOwnOffers) {
		this.wantSubbedList=wantSubbedList;
		this.wantOwnOffers=wantOwnOffers;
	}
	
	public boolean getWantSubbedList() {
		return wantSubbedList;
	}
	public boolean getWantOwnOffers() {
		return wantOwnOffers;
	}
}
