package com.aservice.entity;

import java.sql.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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


@Table(name="subscriptions")
@Entity
@Setter
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class Subscription {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="sub_id")
	private int id;
	@Column(name="date_of_sub")
	@NonNull
	private Date dateOfSub;
	
	@ManyToOne
	@JoinColumn(name="subbed_offer_id")
	@NonNull
	private Offer offer;
	
	@ManyToOne
	@JoinColumn(name="subbing_user_id")
	@NonNull
	private User user;

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
		Subscription other = (Subscription) obj;
		return id == other.id;
	}
	
}
