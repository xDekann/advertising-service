package com.aservice.util;

import java.sql.Timestamp;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.aservice.entity.User;

public class UserUtil {
	
	public enum UserConst{
		PASSWD_LOW_BOUND(4),
		PASSWD_UP_BOUND(70);

		private final int value;
		
		private UserConst(int value) {
			this.value=value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	public static String getLoggedUserName() {
		Authentication userInfo = SecurityContextHolder.getContext().getAuthentication();
		return userInfo.getName();
	}
	public static String getDateToMin(User user, Timestamp time) {
		int start=0;
		int end=16;
		return user.getUserDetails().getLastLogin().toString().substring(start, end);
	}
}
