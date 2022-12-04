package com.aservice;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import com.aservice.dao.UserDao;
import com.aservice.entity.User;
import com.aservice.util.UserUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

/*
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:mysql://localhost:3306/serwis_ogloszeniowy?useSSL=false&serverTimezone=UTC"
})
@TestInstance(Lifecycle.PER_CLASS)
*/
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@Transactional
class AserviceApplicationTests {

	private User user;
	private String username;
	@Autowired
	private UserDao userDao;
	
	@BeforeAll
	public void setUserName() {
		username="utest";
	}
	
	@BeforeEach
	public void setUser() {
		user=userDao.getUserByUsername(username);
		System.out.println("user from db"+user);
		assertEquals(user.getUsername(),"utest");
	}
	
	@Test
	public void deleteUser() {
		User userToRemove = user;
		System.out.println(user);
		userDao.deleteUser(userToRemove);
	}
	
}
