package com.app.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Rss {

	@Id
	@GeneratedValue
	private Long id;
	private String rsslink;
	
	public Rss() {
	}

	public Rss( String rsslink) {
		this.rsslink = rsslink;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRsslink() {
		return rsslink;
	}

	public void setRsslink(String rsslink) {
		this.rsslink = rsslink;
	}

	@Override
	public String toString() {
		return "Rss [id=" + id + ", rsslink=" + rsslink + "]";
	}
}
