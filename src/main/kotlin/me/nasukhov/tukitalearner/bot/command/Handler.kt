package me.nasukhov.tukitalearner.bot.command

import me.nasukhov.tukitalearner.bot.io.Input
import me.nasukhov.tukitalearner.bot.io.Output

interface Handler {
    fun supports(input: Input): Boolean

    fun handle(
        input: Input,
        output: Output,
    )
}
