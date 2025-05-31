package me.nasukhov.tukitalearner.config

import me.nasukhov.bot.adapter.tg.Telegram
import me.nasukhov.tukitalearner.Updater
import org.springframework.boot.CommandLineRunner
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.annotation.Order

@Configuration
@EnableCaching
@ComponentScan(
    basePackages = [
        "me.nasukhov.bot",
        "me.nasukhov.utility",
        "me.nasukhov.tukitalearner",
    ],
)
class AppConfig {
    @Bean
    @Profile("!test")
    @Order(1)
    fun createUpdater(updater: Updater): CommandLineRunner = CommandLineRunner { updater.execute() }

    @Bean
    @Order(2)
    @Profile("!test")
    fun createLauncher(telegram: Telegram): CommandLineRunner = CommandLineRunner { telegram.run() }
}
