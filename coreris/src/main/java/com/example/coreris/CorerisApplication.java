package com.example.coreris;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.ZoneId;
import java.util.TimeZone;

@SpringBootApplication
public class CorerisApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
		System.out.println("TimeZone.getDefault() = " + TimeZone.getDefault().getID());
		System.out.println("ZoneId.systemDefault() = " + ZoneId.systemDefault());

		SpringApplication.run(CorerisApplication.class, args);
	}

}