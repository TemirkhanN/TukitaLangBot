package me.nasukhov.TukitaLearner;

import me.nasukhov.TukitaLearner.DI.ServiceLocator;
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
    private static final ServiceLocator serviceLocator = new ServiceLocator();

    public static void main(String[] args) {
        var app = new SpringApplication(LearnerApp.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Bean
    public static ApplicationRunner createRunner() {
        return (args) -> {
            try {
                serviceLocator.locate(Telegram.class).run();
            } catch (Throwable e) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
        };
    }
}