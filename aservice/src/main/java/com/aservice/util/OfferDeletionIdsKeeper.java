package com.aservice.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferDeletionIdsKeeper {
	private int offerId;
	private int ownerId;
	
	public OfferDeletionIdsKeeper(int offerId, int ownerId) {
		this.offerId=offerId;
		this.ownerId=ownerId;
	}
}
