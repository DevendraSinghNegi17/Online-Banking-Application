package com.operations.banking.OnlineBanking;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@OpenAPIDefinition(info = @Info(title = "My API", version = "1.0", description = "API for My Application"))
public class OnlineBankingApplication {
	public static void main(String[] args) {
		SpringApplication.run(OnlineBankingApplication.class, args);
	}

}
