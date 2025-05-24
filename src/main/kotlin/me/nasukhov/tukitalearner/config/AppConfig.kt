package me.nasukhov.tukitalearner.config

import me.nasukhov.tukitalearner.Updater
import me.nasukhov.tukitalearner.bot.bridge.tg.Telegram
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order

@Configuration
@ComponentScan(basePackages = ["me.nasukhov.tukitalearner"])
class AppConfig {
    @Bean
    @Order(1)
    @Profile("!test")
    fun createUpdater(updater: Updater): CommandLineRunner = CommandLineRunner { updater.execute() }

    @Bean
    @Order(2)
    @Profile("!test")
    fun createLauncher(telegram: Telegram): CommandLineRunner =
        CommandLineRunner {
            telegram.run()
        }
}
