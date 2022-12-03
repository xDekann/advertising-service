package com.aservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aservice.entity.Offer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class OfferDao {
	
	@Autowired
	private EntityManager entityManager;

	@Transactional
	public void addOffer(Offer offer) {
		Offer dbOffer = entityManager.merge(offer);
		offer.setId(dbOffer.getId()); 
	}
	@Transactional
	public List<Offer> getAllOffers(){
		List<Offer> dbOffers = null;
		try {
			Query query = entityManager.createQuery("select o from Offer o left join fetch o.user", Offer.class);
			dbOffers = (ArrayList<Offer>) query.getResultList();
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
		}
		return dbOffers;
	}
}
