package com.aservice.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.aservice.dao.AuthorityDao;
import com.aservice.dao.UserDao;
import com.aservice.entity.Authority;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {
	
	@MockBean
	private UserDao userDao;
	@MockBean
	private AuthorityDao authorityDao;
	@MockBean
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Test
	void testShowStartingPage() {
		fail("Not yet implemented");
	}

	@Test
	void testShowLoginPage() {
		fail("Not yet implemented");
	}

	@Test
	void testAccessDenied() {
		fail("Not yet implemented");
	}

	
	@Test
	void testRegisterForm() throws Exception {
		
		Authority admin = new Authority();
		admin.setAuthorityName("admin");
		admin.setId(1);
		
		Authority user = new Authority();
		user.setAuthorityName("user");
		user.setId(2);
		
		List<Authority> auths = List.of(admin,user);
		
		when(authorityDao.getAllAuthorities()).thenReturn(auths);
		
		this.mockMvc
			.perform(MockMvcRequestBuilders.get("/login/registerForm"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.view().name("login-and-register/register"))
			.andExpect(MockMvcResultMatchers.model().attributeExists("user"))
			.andExpect(MockMvcResultMatchers.model().attribute("auths", Matchers.hasItems(admin,user)))
			.andExpect(MockMvcResultMatchers.model().attributeExists("userDetails"));
	}

	@Test
	void testCreateUser() {
		fail("Not yet implemented");
	}

	@Test
	void testPasswordResetForm() {
		fail("Not yet implemented");
	}

	@Test
	void testPasswordReset() {
		fail("Not yet implemented");
	}

}
