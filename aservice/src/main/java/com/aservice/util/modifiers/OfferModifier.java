package com.aservice.util.modifiers;

import com.aservice.util.OfferUtil.OfferConst;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class OfferModifier extends Modifier {

	protected boolean wantSubbedList;
	protected boolean wantOwnOffers;

	public boolean getWantSubbedList() {
		return wantSubbedList;
	}
	public boolean getWantOwnOffers() {
		return wantOwnOffers;
	}
}
