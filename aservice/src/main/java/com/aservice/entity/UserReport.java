package com.aservice.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Table(name="user_report")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class UserReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="report_id")
	private int id;
	
	@Column(name="user_report_desc")
	@NonNull
	@NotEmpty(message="Cannot be empty!")
	@Size(min = 20, message="Must be at least 20 characters length, please provide us more information!")
	@Size(max = 1000, message="Limit of 1000 characters has been reached!")
	private String description;
	
	@Column(name="reporting_user_id")
	private int reportingUserId;
	
	@ManyToOne
	@JoinColumn(name="reported_user_id")
	@NonNull
	private User user;

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
		UserReport other = (UserReport) obj;
		return Objects.equals(description, other.description) && id == other.id;
	}
}
