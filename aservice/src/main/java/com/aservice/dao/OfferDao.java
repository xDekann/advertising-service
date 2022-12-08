package com.aservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aservice.entity.Offer;
import com.aservice.entity.Subscription;
import com.aservice.util.OfferListModifier;
import com.aservice.util.UserUtil;

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
	public List<Offer> getPagedOffers(int startingRow, int amountOfRows, OfferListModifier modifier, boolean active, int loggedUserId){
		
		String filter = modifier.getFilter();
		String orderAttr = modifier.getComparingMethod();
		
		// filter check 
		boolean isFilterNull=false;
		try {
			filter.isEmpty();
		}catch(NullPointerException nullPointerException) {
			isFilterNull=true;
		}
		System.out.print(isFilterNull+"\n"+"\n"+filter+"\n"+orderAttr);
		List<Offer> dbOffers = null;
		try {
			Query query = null;
			String queryText = "select o from Offer o"
					+ " left join fetch o.user u where o.isActive=:active"
					+ " and o.id not in "
					+ "(select s.offer.id from Subscription s where s.user.id=:loggedId)";
			
			// if filter and sort choosen
			if(!isFilterNull && !filter.isEmpty()) {
				query = entityManager.createQuery(queryText+" and o.title like "+"'%"+filter+"%'"+" order by o."+orderAttr+" ASC", Offer.class);
				query.setParameter("active", active);
				System.out.println("WAR1");
			}
			// if only sort chosen (if nothing is chosen, the sort is id by default)
			else if(isFilterNull || filter.isEmpty()) {
				query = entityManager.createQuery(queryText+" order by o."+orderAttr+" ASC", Offer.class);
				query.setParameter("active", active);
				System.out.println("WAR2");
			}
			
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
			Query query = entityManager.createQuery("from Offer o join fetch o.user where o.id=:givenid", Offer.class);
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
	public boolean isSubbed(int userId, int offerId) {
		try {
			Query query = 
					entityManager.createQuery("from Subscription s "
											+ "join fetch s.offer o "
											+ "join fetch s.user u "
											+ "where u.id=:userId and o.id=:offerId", Subscription.class);
			query.setParameter("userId", userId);
			query.setParameter("offerId", offerId);
			if(query.getSingleResult()!=null) return true;
		}catch (NoResultException noResultException) {
			noResultException.printStackTrace();
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}
}
