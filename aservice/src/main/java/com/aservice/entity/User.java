package com.aservice.entity;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import jakarta.persistence.OneToMany;

import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	@Column(name="username")
	@NonNull
	@NotEmpty(message="Username must not be empty!")
	@Size(min = 3, message="Must be at least 3 characters length!")
	@Size(max = 45, message="Must be at most 45 characters length!")
	private String username;
	@Column(name="password")
	@NonNull
	@NotEmpty(message="Password field must not be empty!")
	@Size(min = 4, message="Must be at least 4 characters length!")
	@Size(max = 70, message="Must be at most 70 characters length!")
	private String password;
	@Column(name="enabled")
	private boolean enabled;
	@Column(name="reset_code")
	@NotEmpty(message="Reset code field must not be empty!")
	@Size(min = 4, message="Must be at least 4 characters length!")
	@Size(max = 70, message="Must be at most 70 characters length!")
	private String resetCode;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="user_auth",
			   joinColumns = @JoinColumn(name="user_id_auth"),
			   inverseJoinColumns = @JoinColumn(name="role_id_auth")
	)
	private Set<Authority> roles;
	
	@OneToOne(mappedBy = "user",
			cascade = CascadeType.ALL)
	private UserDetails userDetails;
	
	@OneToMany(mappedBy="user",
			   cascade = {CascadeType.MERGE, CascadeType.REMOVE},
			   orphanRemoval = true)
	private List<Offer> offers;
	
	@OneToMany(mappedBy="user",
			   cascade = {CascadeType.MERGE, CascadeType.REMOVE},
			   orphanRemoval = true)
	private List<Subscription> subs;
	
	
	@OneToMany(mappedBy = "user",
			  cascade = {CascadeType.MERGE, CascadeType.REMOVE},
			  orphanRemoval = true)
	private List<UserReport> reports;
	
	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password + ", enabled=" + enabled + "]";
	}
	
	public void addAuthority(Authority auth) {
		if(roles==null) roles = new HashSet<>();
		roles.add(auth);
	}
	
	public void connectUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}
	
	public void addOffer(Offer offer) {
		if(offers==null) offers = new ArrayList<>();
		offers.add(offer);
		offer.connectUser(this);
	}
	
	public void addSub(Subscription subscription) {
		if(subs==null) subs = new ArrayList<>();
		subs.add(subscription);
	}

	public void addReport(UserReport report) {
		if(reports==null) reports = new ArrayList<>();
		reports.add(report);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id, resetCode, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return id == other.id && Objects.equals(resetCode, other.resetCode) && Objects.equals(username, other.username);
	}
}
