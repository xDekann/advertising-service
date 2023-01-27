package com.aservice.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private Logger logger = LoggerFactory.getLogger(AuthorityDao.class);

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
			logger.info("No result exception in getAuthorityByName(String authorityName)");
		}
		
		return authority;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Authority> getAllAuthorities(){
		List<Authority> auths = null;
		
		try {
			Query query = entityManager.createQuery("FROM Authority a", Authority.class);
			auths = query.getResultList();
		}catch(NoResultException noResultException) {
			logger.info("No result exception in getAllAuthorities()");
		}catch (Exception exception) {
			logger.error("Exception caught in getAllAuthorities()");
		}
		
		return auths;
	}
}
