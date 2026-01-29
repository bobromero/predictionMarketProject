package com.predictionMarket.predictionMarket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PredictionMarketApplication {

	public static void main(String[] args) {
		SpringApplication.run(PredictionMarketApplication.class, args);
	}

}
