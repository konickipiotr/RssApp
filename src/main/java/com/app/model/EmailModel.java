package com.app.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EmailModel {

	@Id
	private Long id;
	private String email;

	
	public EmailModel() {
		this.id = 1l;
	}
	public EmailModel( String email) {
		this.email = email;
		this.id = 1l;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "Email [id=" + id + ", email=" + email + "]";
	}	
}
