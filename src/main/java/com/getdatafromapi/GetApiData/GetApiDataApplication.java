package com.getdatafromapi.GetApiData;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GetApiDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(GetApiDataApplication.class, args);
	}
}
