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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
	@NotEmpty(message="Name must not be empty!")
	@Size(min = 3, message="Must be at least 3 characters length!")
	@Size(max = 45, message="Must be at most 45 characters length!")
	private String name;
	@Column(name="surname")
	@NonNull
	@NotEmpty(message="Name must not be empty!")
	@Size(min = 3, message="Must be at least 3 characters length!")
	@Size(max = 45, message="Must be at most 45 characters length!")
	private String surname;
	@Column(name="email")
	@NonNull
	@Email(regexp = ".+[@].+[\\.].+", message="Must be a valid email address!")
	@Size(max = 45, message="Must be at most 45 characters length!")
	private String email;
	@Column(name="city")
	@NonNull
	@NotEmpty(message="City must not be empty!")
	@Size(min = 3, message="Must be at least 1 characters length!")
	@Size(max = 45, message="Must be at most 45 characters length!")
	private String city;
	@Column(name="phone_number")
	@NonNull
	@Pattern(regexp="([0-9]{9,15})", message = "Must be between 9-15, without country code")
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
