package com.aservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aservice.entity.Offer;
import com.aservice.entity.Subscription;
import com.aservice.util.OfferListModifier;

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
	
	@SuppressWarnings("unchecked")
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
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Offer> getPagedOffers(OfferListModifier modifier, boolean active, int loggedUserId){
		
		String filter = modifier.getFilter();
		String orderAttr = modifier.getComparingMethod();
		int startingRow = modifier.getStartingRow();
		int amountOfRows = modifier.getLimit();
		boolean wantSubbed = modifier.getWantSubbedList();
		boolean wantOwn = modifier.getWantOwnOffers();
		
		// filter check 
		boolean isFilterNull=false;
		try {
			filter.isEmpty();
		}catch(NullPointerException nullPointerException) {
			isFilterNull=true;
		}
		
		// main references initialization
		List<Offer> dbOffers = null;
		Query query = null;
		String queryText=null;
		
		// used for checking if reached from view subbed (followed) offers
		String querySubFill=null;
		if(wantSubbed) querySubFill=" in ";
		else querySubFill=" not in ";
		
		try {
			if(!wantOwn)
				queryText = "select o from Offer o"
						+ " join fetch o.user u where o.isActive=:active"
						+ " and o.id"
						+ querySubFill
						+ "(select s.offer.id from Subscription s where s.user.id=:loggedId)";
			else 
				queryText = "select o from Offer o" 
						  + " join fetch o.user u where u.id=:loggedId";
			
			// if filter and sort choosen
			if(!isFilterNull && !filter.isEmpty()) {
				query = entityManager.createQuery(queryText+" and o.title like "+"'%"+filter+"%'"+" order by o."+orderAttr+" ASC", Offer.class);
			}
			// if only sort chosen (if nothing is chosen, the sort is id by default)
			else if(isFilterNull || filter.isEmpty()) {
				query = entityManager.createQuery(queryText+" order by o."+orderAttr+" ASC", Offer.class);
			}
			
			if(!wantOwn) query.setParameter("active", active);
			
			query.setParameter("loggedId", loggedUserId);
			
			if(query!=null) {
				query.setFirstResult(startingRow);
				query.setMaxResults(amountOfRows);
				dbOffers = (ArrayList<Offer>) query.getResultList();
				if(dbOffers.isEmpty()) throw new NoResultException();
			}
			
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
			return null;
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		return dbOffers;
	}
	
	@Transactional
	public Offer getOfferById(int id) {
		Offer offer = null;
		
		try {
			Query query = entityManager.createQuery("from Offer o left join fetch o.user left join fetch o.subs where o.id=:givenid", Offer.class);
			query.setParameter("givenid", id);
			offer = (Offer) query.getSingleResult();
		}catch (NoResultException noResultException) {
			noResultException.printStackTrace();
		}
		
		return offer;
	}
	
	@Transactional
	public void addSub(Subscription sub) {
		Subscription dbSub = entityManager.merge(sub);
		sub.setId(dbSub.getId());
	}
	
	@Transactional
	public Subscription getSubbedOffer(int userId, int offerId) {
		
		Subscription dbSub = null;
		
		try {
			Query query = 
					entityManager.createQuery("from Subscription s "
											  + "where s.user.id=:userId and s.offer.id=:offerId", Subscription.class);
			query.setParameter("userId", userId);
			query.setParameter("offerId", offerId);
			dbSub = (Subscription) query.getSingleResult();
		}catch (NoResultException noResultException) {
			noResultException.printStackTrace();
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		return dbSub;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Subscription> getAllSubsOfOffer(int offerId){
		List<Subscription> dbSubs = null;
		
		try {
			Query query = entityManager.createQuery("from Subscription s "
													+"where s.offer.id=:offerId", Subscription.class);
			query.setParameter("offerId", offerId);
			dbSubs = query.getResultList();
		}catch (NoResultException noResultException) {
			noResultException.printStackTrace();
			return null;
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		return dbSubs;
	}
	
	@Transactional
	public void deleteSub(Subscription sub) {
		try {
			entityManager.remove(sub);
		}catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
	@Transactional
	public void deleteOffer(Offer offer) {
		try {
			entityManager.remove(offer);
		}catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
