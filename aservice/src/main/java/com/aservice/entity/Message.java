package com.aservice.entity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
import lombok.Setter;

@Entity
@Table(name="messages")
@Getter
@Setter
@NoArgsConstructor
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="message_id")
	private int id;
	
	@Column(name="receiver_id")
	private int receiverId;
	
	@Column(name="message_date")
	private Timestamp messageDate;
	
	@Column(name="message_content")
	private String messageContent;
	
	@ManyToOne
	@JoinColumn(name="sender_id")
	@NonNull
	private User user;

	@Override
	public int hashCode() {
		return Objects.hash(id, messageContent, messageDate);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		return id == other.id && Objects.equals(messageContent, other.messageContent)
				&& Objects.equals(messageDate, other.messageDate);
	}
}
