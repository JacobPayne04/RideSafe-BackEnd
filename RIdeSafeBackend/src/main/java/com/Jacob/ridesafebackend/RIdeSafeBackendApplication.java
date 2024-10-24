package com.Jacob.ridesafebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class})
public class RIdeSafeBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RIdeSafeBackendApplication.class, args);
	}

}
