package com.aservice.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Table(name="offer_report")
@Entity
@Setter
@Getter
@NoArgsConstructor
@RequiredArgsConstructor
public class OfferReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_offer_report")
	private int id;
	
	@Column(name="offer_report_desc")
	@NonNull
	@NotEmpty(message="Cannot be empty!")
	@Size(min = 20, message="Must be at least 20 characters length, please provide us more information!")
	@Size(max = 1000, message="Limit of 1000 characters has been reached!")
	private String description;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="reported_offer_id")
	@NonNull
	private Offer offer;

	@Override
	public int hashCode() {
		return Objects.hash(description, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OfferReport other = (OfferReport) obj;
		return Objects.equals(description, other.description) && id == other.id;
	}
	
	
	
}
