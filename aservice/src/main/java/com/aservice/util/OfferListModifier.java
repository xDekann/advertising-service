package com.aservice.util;

import java.util.Comparator;

import com.aservice.entity.Offer;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OfferListModifier {
	
	private int startingRow=0;
	private int previousPage=0;
	private int currentPage=1;
	private int nextPage=2;
	private String filter=null;
	
	private boolean isShowClicked=false;
	private boolean isLeftClicked=false;
	private boolean isRightClicked=false;
	private boolean isNext=true;
	
	private String comparingMethod = "id";
	
	public void increment() {
		previousPage++;
		currentPage++;
		nextPage++;
	}
	public void decrement() {
		previousPage--;
		currentPage--;
		nextPage--;
	}
	public void setShowClicked() {
		isShowClicked=true;
	}
	public void setLRnotClicked() {
		isLeftClicked=false;
		isRightClicked=false;
	}
	// Thymeleaf getters/setters
	public boolean getIsShowClicked() {
		return isShowClicked;
	}
	public void setIsShowClicked(boolean isShowClicked) {
		this.isShowClicked=isShowClicked;
	}
	/*
	public boolean getIsLeftClicked() {
		return isLeftClicked;
	}
	public void setIsLeftClicked(boolean isLeftClicked) {
		this.isLeftClicked=isLeftClicked;
	}
	public boolean getIsRigtClicked() {
		return isRightClicked;
	}
	public void setIsRightClicked(boolean isRightClicked) {
		this.isRightClicked=isRightClicked;
	}
	*/
	public boolean getIsNext() {
		return isNext;
	}
	public void setIsNext(boolean isNext) {
		this.isNext=isNext;
	}
}
