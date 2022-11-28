package com.aservice.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="authorities")
@Getter
@Setter
@NoArgsConstructor
public class Authority {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="role_id")
	private int id;
	@Column(name="authority")
	private String authorityName;
	
	@ManyToMany(fetch = FetchType.LAZY,
				mappedBy = "roles")
	private List<User> users;
	
}
