package com.aservice.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aservice.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Repository
public class UserDao {

	private EntityManager entityManager;
	
	// Spring w tle tworzy obiekt entity managera, ktory zawiera konfiguracje potrzebna do komunikacji z baza (m.in.: z parametrami z application.properties)
	@Autowired
	public UserDao(EntityManager entityManager) {
		this.entityManager=entityManager;
	}
	
	@Transactional
	public User getUserByUsername(String username) {
		User user = null;
		try {
			Query query = entityManager.createQuery("select u from User u left join fetch u.roles left join fetch u.userDetails where u.username=:u",User.class);
			query.setParameter("u", username);
			user = (User) query.getSingleResult();
		}catch(NoResultException noResultException) {
			noResultException.printStackTrace();
		}
		return user;
	}
	
	@Transactional // merge zarowno insertuje nowy obiekt, jak i moze go updateowac w przypadku, gdy podane ID jest juz zajete w bazie.
	public void addUser(User user) {
		User dbUser = entityManager.merge(user);
		user.setId(dbUser.getId()); // to jest metoda do dodawania, wiec user kiedy go chcemy dodac, nie ma przypisanego id (jak sie nie myle to domyslnie ma 0)
									// po dodaniu go do bazy, otrzymuje on tam nastepne wolne id (autoinkrement)
									// zeby zachowac integralnosc apki z bazą, entityManager zwraca nam przy okazji zapisany obiekt usera z bazy (czyli po dodaniu mu ID)
									// , przypisujemy go do dbUser, i przez to ze on jest z bazy (ma przypisane id), pobieramy jego ID i przypisujemy do obiektu, ktory jest w apce
	}
	
	@Transactional
	public User getUserById(int id) {
		User user = null;
		try {
			Query query = entityManager.createQuery("select u from User u left join fetch u.roles left join fetch u.userDetails where u.id=:userId",User.class);
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
}
