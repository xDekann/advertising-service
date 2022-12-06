package com.aservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aservice.entity.Offer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaQuery;
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
	@Transactional
	public List<Offer> getPagedOffers(int startingRow, int amountOfRows, String filter, String order){
		
		// filter check 
		boolean isFilterNull=false;
		try {
			filter.isEmpty();
		}catch(NullPointerException nullPointerException) {
			isFilterNull=true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		// order check
		boolean isOrderNull = false;
		try {
			order.isEmpty();
		}catch(NullPointerException nullPointerException) {
			isOrderNull=true;
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.print(isFilterNull+"\n"+isOrderNull+"\n"+filter+"\n"+order);
		List<Offer> dbOffers = null;
		try {
			Query query = null;
			String queryText = "select o from Offer o left join fetch o.user";
			if(!isFilterNull && !filter.isEmpty() && !isOrderNull && !order.isEmpty()) {
				query = entityManager.createQuery(queryText+" where o.title like "+"'%"+filter+"%'"+" order by o."+order+" ASC", Offer.class);
				//query = entityManager.createQuery("select o from Offer o left join fetch o.user where o.title like :filter order by o.:order DESC", Offer.class);
				//query.setParameter("filter", "%"+filter+"%");
				//query.setParameter("order", order);
				
			}
			else if((isFilterNull || filter.isEmpty()) && !isOrderNull && !order.isEmpty()) {
				query = entityManager.createQuery(queryText+" order by o."+order+" ASC", Offer.class);
				//query = entityManager.createQuery("select o from Offer o left join fetch o.user order by o.:order DESC", Offer.class);
				//query.setParameter("order", order);
			}
			else if(!isFilterNull && !filter.isEmpty() && (!isOrderNull || !order.isEmpty())) {
				query = entityManager.createQuery(queryText+" where o.title like "+"'%"+filter+"%'", Offer.class);
				//query = entityManager.createQuery("select o from Offer o left join fetch o.user where o.title like :filter", Offer.class);
			}
			else {
				query = entityManager.createQuery(queryText, Offer.class);
			}
			query.setFirstResult(startingRow);
			query.setMaxResults(amountOfRows);
			dbOffers = (ArrayList<Offer>) query.getResultList();
			if(dbOffers.isEmpty()) return null;
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return dbOffers;
	}
}
