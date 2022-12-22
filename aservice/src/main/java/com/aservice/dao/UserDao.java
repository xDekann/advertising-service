package com.aservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aservice.entity.Offer;
import com.aservice.entity.OfferReport;
import com.aservice.entity.User;
import com.aservice.entity.UserReport;
import com.aservice.util.modifiers.Modifier;
import com.aservice.util.modifiers.OfferListModifier;
import com.aservice.util.modifiers.UserListModifier;

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
			Query query = entityManager.createQuery("select distinct u from User u left join fetch u.roles left join fetch u.userDetails"
					+ " where u.username=:u", User.class);
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
	public User getUserByIdWithParam(int id, String param) {
		User user = null;
		
		try {
			Query query = entityManager.createQuery("select u from User u left join fetch u.roles left join fetch u.userDetails left join fetch u."
													+ param
													+ " where u.id=:userId", User.class);
			query.setParameter("userId", id);
			user = (User) query.getSingleResult();
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
		}
		
		return user;
	}
	
	@Transactional
	public User getUserById(int id) {
		User user = null;
		
		try {
			Query query = entityManager.createQuery("select u from User u left join fetch u.roles left join fetch u.userDetails"
													+ " where u.id=:userId", User.class);
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
	
	@Transactional
	public List<User> getPagedUsers(Modifier modifier){
		
		String filter = modifier.getFilter();
		String orderAttr = modifier.getComparingMethod();
		int startingRow = modifier.getStartingRow();
		int amountOfRows = modifier.getLimit();
		
		// filter check 
		boolean isFilterNull=false;
		try {
			filter.isEmpty();
		}catch(NullPointerException nullPointerException) {
			isFilterNull=true;
		}
		
		// main references initialization
		List<User> dbUsers = null;
		Query query = null;
		String queryText=null;
		
		try {
			queryText = "select u from User u left join fetch u.roles left join fetch u.userDetails";
			// if filter and sort choosen
			if(!isFilterNull && !filter.isEmpty()) {
				query = entityManager.createQuery(queryText+" where u.username like "+"'%"+filter+"%'"+" order by u."+orderAttr+" ASC", User.class);
			}
			// if only sort chosen (if nothing is chosen, the sort is id by default)
			else if(isFilterNull || filter.isEmpty()) {
				query = entityManager.createQuery(queryText+" order by u."+orderAttr+" ASC", User.class);
			}
			
			if(query!=null) {
				query.setFirstResult(startingRow);
				query.setMaxResults(amountOfRows);
				dbUsers = (ArrayList<User>) query.getResultList();
				if(dbUsers.isEmpty()) throw new NoResultException();
			}
			
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
			return null;
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		return dbUsers;
	}
	
	@Transactional
	public long getUserReportsAmount(int userId) {
		long reportAmount = 0;
		
		try {
			Query query = entityManager.createQuery("select count(*) from UserReport ur where ur.user.id=:userId");
			query.setParameter("userId", userId);
			reportAmount = (Long) query.getSingleResult();
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
		}catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return reportAmount;
	}
	
	@Transactional
	public List<UserReport> getPagedReportedUsers(Modifier modifier){
		
		String orderAttr = modifier.getComparingMethod();
		int startingRow = modifier.getStartingRow();
		int amountOfRows = modifier.getLimit();
		
		// main references initialization
		List<UserReport> dbReports = null;
		Query query = null;
		String queryText=null;
		
		try {
			queryText = "select ur from UserReport ur left join fetch ur.user";
			query = entityManager.createQuery(queryText+" order by ur."+orderAttr+" ASC", UserReport.class);
			
			if(query!=null) {
				query.setFirstResult(startingRow);
				query.setMaxResults(amountOfRows);
				dbReports = (ArrayList<UserReport>) query.getResultList();
				if(dbReports.isEmpty()) throw new NoResultException();
			}
			
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
			return null;
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		return dbReports;
	}
	
	
	@Transactional
	public UserReport getUserReportById(int reportId) {
		UserReport userReport = null;
		
		try {
			Query query = entityManager.createQuery("select ur from UserReport ur left join ur.user where ur.id=:reportId", UserReport.class);
			query.setParameter("reportId", reportId);
			userReport = (UserReport) query.getSingleResult();	
		}catch (NoResultException noResultException) {
			noResultException.printStackTrace();
		}catch (Exception exception) {
			exception.printStackTrace();
		}
		
		return userReport;
	}
	
	@Transactional
	public void deleteUserReport(UserReport report) {
		try {
			entityManager.remove(report);
		}catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
