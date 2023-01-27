package com.aservice.dao;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aservice.entity.Block;
import com.aservice.entity.Message;
import com.aservice.entity.User;
import com.aservice.util.modifiers.Modifier;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class MessageDao {

	private EntityManager entityManager;
	private Logger logger = LoggerFactory.getLogger(MessageDao.class);
	
	@Autowired
	public MessageDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	@Transactional
	public void addMessage(Message message) {
		Message dbMessage = entityManager.merge(message);
		message.setId(dbMessage.getId());
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<User> getContacts(Modifier modifier, int loggedUserId) {
		
		int startingRow = modifier.getStartingRow();
		int amountOfRows = modifier.getLimit();
		
		// main references initialization
		List<User> dbContacts = null;
		Query query = null;
		String queryText=null;
		
		try {
			queryText = 
				 "select distinct u from User u join fetch u.messages um where u.id in "
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
			logger.info("No result exception in getContacts(Modifier modifier, int loggedUserId)");
			return null;
		}catch(Exception exception) {
			logger.error("Exception in getContacts(Modifier modifier, int loggedUserId)");
		}
		
		return dbContacts;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<Message> getMessagesWithUser(Modifier modifier, int loggedUserId,
											 int pickedUserId) {
		
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
			query.setParameter("pickedUserId", pickedUserId);
			
			if(query!=null) {
				query.setFirstResult(startingRow);
				query.setMaxResults(amountOfRows);
				dbMessages = (ArrayList<Message>) query.getResultList();
				if(dbMessages.isEmpty()) throw new NoResultException();
			}
			
		}catch(NoResultException noResultException) {
			logger.info("No result exception in getMessagesWithUser(Modifier modifier, int loggedUserId, int pickedUserId)");
			return null;
		}catch(Exception exception) {
			logger.error("Exception in getMessagesWithUser(Modifier modifier, int loggedUserId, int pickedUserId)");
		}
		
		return dbMessages;
	}
	
	@Transactional
	public Block getBlock(int currentUserId, int opposingUserId) {
		
		Block block = null;
		
		try {
			Query query = entityManager.createQuery("select b from Block b left join fetch b.user where b.user.id=:currentUserId "
					+ "and b.blockedUserId=:opposingUserId", Block.class);
			query.setParameter("currentUserId", currentUserId);
			query.setParameter("opposingUserId", opposingUserId);
			
			block = (Block) query.getSingleResult();
		}catch(NoResultException noResultException) {
			logger.info("No result exception in getBlock(int currentUserId, int opposingUserId)");
			return null;
		}catch(Exception exception) {
			logger.info("Exception in getBlock(int currentUserId, int opposingUserId)");
		}
		
		return block;
	}
	
	@Transactional
	public void addBlock(Block block) {
		Block dbBlock = entityManager.merge(block);
		block.setId(dbBlock.getId());
	}
	
	@Transactional
	public void deleteBlock(Block block) {
		try {
			entityManager.remove(block);
		}catch (Exception exception) {
			exception.printStackTrace();
		}
	}
	
}
