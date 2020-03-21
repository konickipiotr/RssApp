package com.app.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.model.Rss;

public interface RssDAO extends JpaRepository<Rss, Long> {

}
