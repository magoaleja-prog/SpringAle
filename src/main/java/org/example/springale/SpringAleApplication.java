package org.example.springale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringAleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringAleApplication.class, args);
	}

}
