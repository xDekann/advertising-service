package com.aservice.entity;


import java.sql.Timestamp;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="user_details")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class UserDetails {
	
	@Id
	@Column(name="user_id")
	private int id;
	@Column(name="name")
	@NonNull
	private String name;
	@Column(name="surname")
	@NonNull
	private String surname;
	@Column(name="email")
	@NonNull
	private String email;
	@Column(name="city")
	@NonNull
	private String city;
	@Column(name="phone_number")
	@NonNull
	private String phoneNumber;
	@Column(name="last_login")
	@NonNull
	private Timestamp lastLogin;

	
	@OneToOne
	@JoinColumn(name="user_id")
	@MapsId
	private User user;
	
	public void connectUser(User user) {
		this.user=user;
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
		UserDetails other = (UserDetails) obj;
		return id == other.id;
	}
}
