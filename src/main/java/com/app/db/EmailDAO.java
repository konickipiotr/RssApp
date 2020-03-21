package com.app.db;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.model.EmailModel;

public interface EmailDAO extends JpaRepository<EmailModel, Long> {

}
