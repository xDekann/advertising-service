package com.aservice.util;

public class SharedUtil {
	public static boolean checkIfFilterValid(String filter) {
		if(filter==null) return false;
		if(filter.isEmpty()) return false;
		return true;
	}
}
