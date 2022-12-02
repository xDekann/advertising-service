package com.aservice.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="offers")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Offer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="offer_id")
	private int id;
	@Column(name="date_of_creation")
	@NonNull
	private Timestamp dateOfCreation;
	@Column(name="description")
	@NonNull
	private String description;
	@Column(name="is_active")
	private boolean isActive;
	@Column(name="title")
	@NonNull
	private String title;
	@Column(name="price")
	private double price;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_fk")
	private User user;
	
	public void connectUser(User user) {
		this.user=user;
	}
}
