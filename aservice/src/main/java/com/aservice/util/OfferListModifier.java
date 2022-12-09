package com.aservice.util;

import java.util.Comparator;

import com.aservice.entity.Offer;
import com.aservice.util.OfferUtil.OfferConst;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class OfferListModifier {
	
	private int startingRow=0;
	private int previousPage=0;
	private int currentPage=1;
	private boolean isShowClicked=false;
	private boolean isNext=true;
	
	private final int limit = OfferConst.ROWS_PER_PAGE.getValue();
	private String filter=null;
	private String comparingMethod = "id";
	private boolean wantSubbedList;
	private boolean wantOwnOffers;
	
	public OfferListModifier(boolean wantSubbedList, boolean wantOwnOffers) {
		this.wantSubbedList=wantSubbedList;
		this.wantOwnOffers=wantOwnOffers;
	}
	
	public void increment() {
		previousPage++;
		currentPage++;
	}
	public void decrement() {
		previousPage--;
		currentPage--;
	}
	public void setShowClicked() {
		isShowClicked=true;
	}
	// Thymeleaf getters/setters
	public boolean getIsShowClicked() {
		return isShowClicked;
	}
	public void setIsShowClicked(boolean isShowClicked) {
		this.isShowClicked=isShowClicked;
	}
	public boolean getIsNext() {
		return isNext;
	}
	public void setIsNext(boolean isNext) {
		this.isNext=isNext;
	}
	public boolean getWantSubbedList() {
		return wantSubbedList;
	}
	public boolean getWantOwnOffers() {
		return wantOwnOffers;
	}
}
