package me.nasukhov.TukitaLearner.config

import me.nasukhov.TukitaLearner.Updater
import me.nasukhov.TukitaLearner.bot.bridge.tg.Telegram
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order

@Configuration
@ComponentScan(basePackages = ["me.nasukhov.TukitaLearner"])
class AppConfig {
    @Bean
    @Order(1)
    @Profile("!test")
    fun createUpdater(updater: Updater): CommandLineRunner {
        return CommandLineRunner { updater.execute() }
    }

    @Bean
    @Order(2)
    @Profile("!test")
    fun createLauncher(telegram: Telegram): CommandLineRunner {
        return CommandLineRunner {
            try {
                telegram.run()
            } catch (e: Throwable) {
                // TODO
                e.printStackTrace()
            }
        }
    }
}
