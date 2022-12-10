package com.aservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.aservice.dao.UserDao;
import com.aservice.entity.User;

public class AdvertUserDetailsService implements UserDetailsService {

	@Autowired
	private UserDao userDAO;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user = userDAO.getUserByUsername(username);
		
		if(user == null) {
			throw new UsernameNotFoundException("User has not been found! - Spring security");
		}
		
		return new AdvertUserDetails(user);
	}

	
}
