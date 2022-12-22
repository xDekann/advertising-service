package com.aservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aservice.entity.Message;
import com.aservice.entity.OfferReport;
import com.aservice.entity.User;
import com.aservice.util.modifiers.Modifier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class MessageDao {

	@Autowired
	private EntityManager entityManager;
	
	@Transactional
	public void addMessage(Message message) {
		Message dbMessage = entityManager.merge(message);
		message.setId(dbMessage.getId());
	}
	
	@Transactional
	public List<User> getContacts(Modifier modifier, int loggedUserId) {
		
		int startingRow = modifier.getStartingRow();
		int amountOfRows = modifier.getLimit();
		
		// main references initialization
		List<User> dbContacts = null;
		Query query = null;
		String queryText=null;
		
		
		//m.receiverId=u.id and
		// m.user.id=u.id and
		try {
			queryText = 
			 "select distinct u from User u left join fetch u.messages um where u.id in "
			+"(select m.receiverId from Message m where m.receiverId!=:loggedUserId and m.user.id=:loggedUserId) "
			+"OR u.id in "
			+"(select m.user.id from Message m where m.user.id!=:loggedUserId and m.receiverId=:loggedUserId) "
			+ "and u.id!=:loggedUserId "
			+"order by um.messageDate DESC";
			
			query = entityManager.createQuery(queryText, User.class);
			query.setParameter("loggedUserId", loggedUserId);
			
			if(query!=null) {
				query.setFirstResult(startingRow);
				query.setMaxResults(amountOfRows);
				dbContacts = (ArrayList<User>) query.getResultList();
				if(dbContacts.isEmpty()) throw new NoResultException();
			}
			
		}catch(NoResultException noResultException) {
			//noResultException.printStackTrace();
			return null;
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		return dbContacts;
		
	}
	
	@Transactional
	public List<Message> getMessagesWithUser(Modifier modifier, int loggedUserId,
											 int pickeduserId) {
		
		int startingRow = modifier.getStartingRow();
		int amountOfRows = modifier.getLimit();
		
		// main references initialization
		List<Message> dbMessages = null;
		Query query = null;
		String queryText=null;
		
		try {
			queryText = 
			   "select m from Message m left join m.user where "
			 + "(m.receiverId=:loggedUserId or m.receiverId=:pickedUserId) and "
			 + "(m.user.id=:loggedUserId or m.user.id=:pickedUserId) "
			 + "order by m.messageDate DESC";
			
			query = entityManager.createQuery(queryText, Message.class);
			query.setParameter("loggedUserId", loggedUserId);
			query.setParameter("pickedUserId", pickeduserId);
			
			if(query!=null) {
				query.setFirstResult(startingRow);
				query.setMaxResults(amountOfRows);
				dbMessages = (ArrayList<Message>) query.getResultList();
				if(dbMessages.isEmpty()) throw new NoResultException();
			}
			
		}catch(NoResultException noResultException) {
			//noResultException.printStackTrace();
			return null;
		}catch(Exception exception) {
			exception.printStackTrace();
		}
		
		return dbMessages;
		
	}
	
}
