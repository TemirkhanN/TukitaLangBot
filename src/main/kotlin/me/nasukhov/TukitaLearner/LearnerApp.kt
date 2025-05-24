package me.nasukhov.TukitaLearner

import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
object LearnerApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val app = SpringApplication(LearnerApp::class.java)
        app.webApplicationType = WebApplicationType.NONE
        app.run(*args)
    }
}