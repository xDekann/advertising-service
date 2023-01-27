package com.aservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aservice.entity.Offer;
import com.aservice.entity.OfferReport;
import com.aservice.entity.Subscription;
import com.aservice.util.modifiers.Modifier;
import com.aservice.util.modifiers.OfferModifier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class OfferDao {
	
	private EntityManager entityManager;
	private Logger logger = LoggerFactory.getLogger(OfferDao.class);
	
	@Autowired
	public OfferDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

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
			logger.info("No result exception in getAllOffers()");
		}
		
		return dbOffers;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Offer> getPagedOffers(OfferModifier modifier, boolean active, int loggedUserId){
		
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
			logger.info("No result exception in getPagedOffers(OfferModifier modifier, boolean active, int loggedUserId)");
			return null;
		}catch(Exception exception) {
			logger.error("Exception in getPagedOffers(OfferModifier modifier, boolean active, int loggedUserId)");
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
			logger.info("No result exception in getOfferById(int id)");
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
			logger.info("No result exception in getSubbedOffer(int userId, int offerId)");
		}catch(Exception exception) {
			logger.error("Exception in getSubbedOffer(int userId, int offerId)");
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
			logger.info("No result exception in getAllSubsOfOffer(int offerId)");
		}catch(Exception exception) {
			logger.error("Exception in getAllSubsOfOffer(int offerId)");
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
	
	@Transactional
	public long getOfferReportsAmount(int offerId) {
		long reportAmount = 0;
		
		try {
			Query query = entityManager.createQuery("select count(*) from OfferReport ort where ort.offer.id=:offerId");
			query.setParameter("offerId", offerId);
			reportAmount = (Long) query.getSingleResult();
		}catch (NoResultException noResultException) {
			logger.info("No result exception in getOfferReportsAmount(int offerId)");
		}catch (Exception exception) {
			logger.error("Exception in getOfferReportsAmount(int offerId)");
		}
		
		return reportAmount;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<OfferReport> getOfferReports(int offerId){
		
		List<OfferReport> reportList = null;
		
		try {
			Query query = entityManager.createQuery("select ort from OfferReport ort where ort.offer.id=:offerId");
			query.setParameter("offerId", offerId);
			reportList = query.getResultList();
		}catch (NoResultException noResultException) {
			logger.info("No result exception in getOfferReports(int offerId)");
		}catch (Exception exception) {
			logger.info("Exception in getOfferReports(int offerId)");
		}
		
		return reportList;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<OfferReport> getPagedReportedOffers(Modifier modifier){

		String orderAttr = modifier.getComparingMethod();
		int startingRow = modifier.getStartingRow();
		int amountOfRows = modifier.getLimit();
		
		// main references initialization
		List<OfferReport> dbReports = null;
		Query query = null;
		String queryText=null;
		
		try {
			queryText = "select ort from OfferReport ort join fetch ort.offer";
			
			query = entityManager.createQuery(queryText+" order by ort."+orderAttr+" ASC", OfferReport.class);
			
			if(query!=null) {
				query.setFirstResult(startingRow);
				query.setMaxResults(amountOfRows);
				dbReports = (ArrayList<OfferReport>) query.getResultList();
				if(dbReports.isEmpty()) throw new NoResultException();
			}
			
		}catch(NoResultException noResultException) {
			logger.info("No result exception in getPagedReportedOffers(Modifier modifier)");
			return null;
		}catch(Exception exception) {
			logger.error("Exception in getPagedReportedOffers(Modifier modifier)");
		}
		
		return dbReports;
	}
	
	@Transactional
	public OfferReport getOfferReportById(int reportId) {
		OfferReport offerReport = null;
		try {
			Query query = entityManager.createQuery("select ort from OfferReport ort join fetch ort.offer where ort.id=:reportId", OfferReport.class);
			query.setParameter("reportId", reportId);
			offerReport = (OfferReport) query.getSingleResult();
		}catch(NoResultException noResultException) {
			logger.info("No result exception in getOfferReportById(int reportId)");
		}catch (Exception exception) {
			logger.info("Exception in getOfferReportById(int reportId)");
		}
		
		return offerReport;
	}
	
	@Transactional
	public void deleteReport(OfferReport report) {
		try {
			entityManager.remove(report);
		}catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
