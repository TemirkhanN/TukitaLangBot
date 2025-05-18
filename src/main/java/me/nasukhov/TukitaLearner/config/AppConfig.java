package me.nasukhov.TukitaLearner.config;

import me.nasukhov.TukitaLearner.Updater;
import me.nasukhov.TukitaLearner.bot.bridge.tg.Telegram;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "me.nasukhov.TukitaLearner")
public class AppConfig {
    @Bean
    public CommandLineRunner createLauncher(Updater updater, Telegram telegram) {
        return (args) -> {
            try {
                updater.execute();

                telegram.run();
            } catch (Throwable e) {
                // TODO
                e.printStackTrace();
            }
        };
    }
}
