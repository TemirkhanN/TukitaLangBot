package me.nasukhov.tukitalearner

import org.springframework.boot.WebApplicationType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
class LearnerApp

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    SpringApplicationBuilder(LearnerApp::class.java)
        .web(WebApplicationType.NONE)
        .run(*args)
}
