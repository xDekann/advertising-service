package com.aservice.entity;

import java.util.HashSet;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	@Column(name="username")
	@NonNull
	private String username;
	@Column(name="password")
	@NonNull
	private String password;
	@Column(name="enabled")
	@NonNull
	private boolean enabled;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name="user_auth",
			   joinColumns = @JoinColumn(name="user_id_auth"),
			   inverseJoinColumns = @JoinColumn(name="role_id_auth")
	)
	private Set<Authority> roles;
	
	@OneToOne(mappedBy = "user",
			cascade = CascadeType.ALL)
	private UserDetails userDetails;
	
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
}