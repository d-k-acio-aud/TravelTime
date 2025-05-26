package com.example.travel_time;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectTtApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().filename("amazon.env").load();
        System.setProperty("SECRET", dotenv.get("SECRET"));
        SpringApplication.run(ProjectTtApplication.class, args);
    }
    
}
