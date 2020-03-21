package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AppApplication {
	
	public static boolean isEmail = false;

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

}
