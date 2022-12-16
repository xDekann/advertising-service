package com.aservice.util;

import com.aservice.util.OfferUtil.OfferConst;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class ListModifier {

	private int startingRow=0;
	private int previousPage=0;
	private int currentPage=1;
	private boolean isShowClicked=false;
	private boolean isNext=true;
	private String comparingMethod = "id";
	
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
}
