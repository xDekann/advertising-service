package com.aservice;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.aservice.dao.UserDao;
import com.aservice.entity.User;

import jakarta.persistence.EntityManager;

/*
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:mysql://localhost:3306/serwis_ogloszeniowy?useSSL=false&serverTimezone=UTC"
})
@TestInstance(Lifecycle.PER_CLASS)
*/
//@SpringBootTest
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestInstance(Lifecycle.PER_CLASS)
class AserviceApplicationTests {

	private User user;
	private String username;
	
	@Autowired
	private EntityManager entityManager;
	
	private UserDao userDao;
		
	@BeforeAll
	public void setUserName() {
		userDao = new UserDao(this.entityManager);
		username="admin";
	}
	
	@BeforeEach
	public void setUser() {
		user=userDao.getUserByUsername(username);
		System.out.println("user from db"+user);
		assertEquals(user.getUsername(),"admin");
	}
	
	@Test
	public void deleteUser() {
		User userToRemove = user;
		System.out.println(user);
		userDao.deleteUser(userToRemove);
	}
	
}
