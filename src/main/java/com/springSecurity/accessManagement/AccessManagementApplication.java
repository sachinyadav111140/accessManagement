package com.springSecurity.accessManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableJpaAuditing(auditorAwareRef = "auditorAwareImpl")
public class AccessManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessManagementApplication.class, args);
	}

}
