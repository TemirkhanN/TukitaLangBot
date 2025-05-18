package me.nasukhov.TukitaLearner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LearnerApp {
    public static void main(String[] args) {
        var app = new SpringApplication(LearnerApp.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}