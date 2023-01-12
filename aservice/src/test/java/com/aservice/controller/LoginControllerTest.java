package com.aservice.controller;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.sql.Timestamp;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.aservice.dao.AuthorityDao;
import com.aservice.dao.UserDao;
import com.aservice.entity.Authority;
import com.aservice.entity.User;
import com.aservice.entity.UserDetails;
import com.aservice.util.UserUtil;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc
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
	@WithMockUser
	@WithAnonymousUser
	void testShowStartingPage() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/login/start"))
			   .andExpect(view().name("login-and-register/start"))
			   .andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	@WithAnonymousUser
	void testShowLoginPage() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/login/showLoginPage"))
		   	   .andExpect(view().name("login-and-register/login-form"))
		   	   .andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	@WithAnonymousUser
	void testAccessDenied() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/login/access-denied"))
	   	   .andExpect(view().name("login-and-register/access-denied"))
	   	   .andExpect(status().isOk());
	}

	
	@Test
	@WithMockUser
	@WithAnonymousUser
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
	@WithMockUser
	@WithAnonymousUser
	void testCreateUserByUserSuccess() throws Exception { // more cases required
		
		String username = "admin";
		User user = new User();
		user.setUsername(username);
		user.setResetCode("admin");
		user.setPassword("admin");
		
		Authority adminAuth = new Authority();
		adminAuth.setAuthorityName("admin");
		adminAuth.setId(1);
		
		Authority userAuth = new Authority();
		userAuth.setAuthorityName("user");
		userAuth.setId(2);
		
		UserDetails userDetails =
				new UserDetails("surname", "lastName", "admin@email.com",
								"cityCracow", "1234567890",
								new Timestamp(System.currentTimeMillis()));
		
		MultiValueMap<String, String> authsParams = new LinkedMultiValueMap<>();
		authsParams.add("auths", adminAuth.getAuthorityName());
		authsParams.add("auths", userAuth.getAuthorityName());
		
		try(MockedStatic<UserUtil> utilities = Mockito.mockStatic(UserUtil.class)){
			utilities.when(UserUtil::getLoggedUserName).thenReturn("pass");
		}
		
		when(userDao.getUserByUsername("pass")).thenReturn(user);
		
		when(userDao.getUserByUsername("admin")).thenReturn(null);
		when(userDao.getUserByEmail("admin@email.com")).thenReturn(null);
		when(passwordEncoder.encode("admin")).thenReturn("hashedPassword");
		
		when(authorityDao.getAuthorityByName("ROLE_USER")).thenReturn(userAuth);
			
		this.mockMvc
			.perform(MockMvcRequestBuilders.post("/login/registerForm/creation")
			.params(authsParams)
			.flashAttr("user", user)
			.flashAttr("userDetails", userDetails)
			.with(csrf()))
			//.andDo(print())
			.andExpect(redirectedUrl("/login/start"))
			.andExpect(status().isFound());
	}

	@Test
	@WithMockUser
	@WithAnonymousUser
	void testPasswordResetForm() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/login/passwordreset/form"))
		   .andExpect(view().name("login-and-register/password-form"))
		   .andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	@WithAnonymousUser
	void testPasswordResetSuccess() throws Exception {
		
		String username = "admin";
		User user = new User();
		user.setUsername(username);
		user.setResetCode("admin");
		user.setPassword("admin");
		
		when(userDao.getUserByUsername(username)).thenReturn(user);
		when(passwordEncoder.matches(user.getResetCode(), user.getResetCode())).thenReturn(true);
		when(passwordEncoder.encode("adminNew")).thenReturn("hashedPassword");
		
		this.mockMvc
			.perform(MockMvcRequestBuilders.post("/login/passwordreset")
			.param("username", username)
			.param("code", "admin")
			.param("passwd", "adminNew")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("login-and-register/password-form"))
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.model().attribute("credentials", Matchers.equalTo("success")));
	}

	@Test
	@WithMockUser
	@WithAnonymousUser
	void testPasswordResetFail() throws Exception {
		
		String username = "admin";
		User user = new User();
		user.setUsername(username);
		user.setResetCode("admin");
		user.setPassword("admin");
		
		when(userDao.getUserByUsername(username)).thenReturn(user);
		when(passwordEncoder.matches(user.getResetCode(), user.getResetCode())).thenReturn(false);
		when(passwordEncoder.encode("adminNew")).thenReturn("hashedPassword");
		
		this.mockMvc
			.perform(MockMvcRequestBuilders.post("/login/passwordreset")
			.param("username", username)
			.param("code", "admin")
			.param("passwd", "adminNew")
			.with(csrf()))
			.andExpect(status().isOk())
			.andExpect(view().name("login-and-register/password-form"))
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.model().attribute("credentials", Matchers.equalTo("fail")));
	}
}
