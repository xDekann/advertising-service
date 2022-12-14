package com.aservice.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
	@NotEmpty(message="Cannot be empty!")
	@Size(min = 20, message="Must be at least 20 characters length, please provide other users some information!")
	@Size(max = 1000, message="Limit of 1000 characters has been reached!")
	private String description;
	@Column(name="is_active")
	private boolean isActive;
	@Column(name="title")
	@NonNull
	@NotEmpty(message="Cannot be empty!")
	@Size(min = 3, message="Must be at least 3 characters length!")
	@Size(max = 45, message="Must be no more than 45 characters length!")
	private String title;
	@Column(name="price")
	@Digits(integer=6, fraction=2, message="Provide a correct price")
	@NotNull(message="Provide a value")
	@DecimalMin(value="1.00", message = "Price must be at least 1$")
	@DecimalMax(value="100000.00", message = "Price must be no more than 100 000$")
	private Double price;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_fk")
	private User user;
	
	@OneToMany(mappedBy = "offer",
			   cascade = {CascadeType.MERGE, CascadeType.REMOVE},
			   orphanRemoval = true)
	private List<Subscription> subs;
	
	@OneToMany(mappedBy="offer",
			   cascade = {CascadeType.MERGE, CascadeType.REMOVE},
			   orphanRemoval = true)
	private List<OfferReport> reports;
	
	public void connectUser(User user) {
		this.user=user;
	}
	
	public void addSub(Subscription subscription) {
		if(subs==null) subs = new ArrayList<>();
		subs.add(subscription);
	}
	
	public void addReport(OfferReport report) {
		if(reports==null) reports = new ArrayList<>();
		reports.add(report);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Offer other = (Offer) obj;
		return id == other.id;
	}
}
