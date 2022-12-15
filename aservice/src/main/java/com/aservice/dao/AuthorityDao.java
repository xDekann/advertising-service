package com.aservice.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aservice.entity.Authority;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class AuthorityDao {
	
	private EntityManager entityManager;

	@Autowired
	public AuthorityDao(EntityManager entityManager) {
		this.entityManager=entityManager;
	}
	
	@Transactional
	public Authority getAuthorityByName(String authorityName) {
		Authority authority = null;
		
		try {
			Query query = entityManager.createQuery("FROM Authority a LEFT JOIN FETCH a.users where a.authorityName=:n",Authority.class);
			query.setParameter("n", authorityName);
			authority = (Authority) query.getSingleResult();
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
		}
		
		return authority;
	}
	
	@Transactional
	public List<Authority> getAllAuthorities(){
		List<Authority> auths = null;
		
		try {
			Query query = entityManager.createQuery("FROM Authority a", Authority.class);
			auths = query.getResultList();
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
		}catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return auths;
	}
}
