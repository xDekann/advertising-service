package com.aservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserUtil {
	public static String getLoggedUserName() {
		Authentication userInfo = SecurityContextHolder.getContext().getAuthentication();
		return userInfo.getName();
	}
}
