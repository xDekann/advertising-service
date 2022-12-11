package com.aservice.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aservice.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class UserDao {

	private EntityManager entityManager;
	
	@Autowired
	public UserDao(EntityManager entityManager) {
		this.entityManager=entityManager;
	}
	
	@Transactional
	public User getUserByUsername(String username) {
		User user = null;
		
		try {
			Query query = entityManager.createQuery("select u from User u left join fetch u.roles left join fetch u.userDetails where u.username=:u",User.class);
			query.setParameter("u", username);
			user = (User) query.getSingleResult();
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
		}
		
		return user;
	}
	
	@Transactional
	public void addUser(User user) {
		User dbUser = entityManager.merge(user);
		user.setId(dbUser.getId());
	}
	
	@Transactional
	public User getUserById(int id) {
		User user = null;
		
		try {
			Query query = entityManager.createQuery("select u from User u left join fetch u.roles left join fetch u.userDetails where u.id=:userId",User.class);
			query.setParameter("userId", id);
			user = (User) query.getSingleResult();
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
		}
		
		return user;
	}
	
	@Transactional
	public void deleteUser(User user) {
		try {
			entityManager.remove(user);
		}catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	@Transactional
	public User getUserByEmail(String email) {
		User user = null;
		
		try {
			Query query = entityManager.createQuery("select u from User u left join fetch u.roles left join fetch u.userDetails d where d.email=:userEmail",User.class);
			query.setParameter("userEmail", email);
			user = (User) query.getSingleResult();
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
		}
		
		return user;
	}
}
