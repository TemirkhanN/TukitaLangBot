package me.nasukhov.TukitaLearner;

import me.nasukhov.TukitaLearner.bot.bridge.tg.Telegram;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class LearnerApp {
    public static void main(String[] args) {
        var app = new SpringApplication(LearnerApp.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Bean
    public static ApplicationRunner createRunner(Updater updater, Telegram telegram) {
        return (args) -> {
            try {
                updater.execute();

                telegram.run();
            } catch (Throwable e) {
                // TODO
                e.printStackTrace();
                System.exit(1);
            }
        };
    }
}