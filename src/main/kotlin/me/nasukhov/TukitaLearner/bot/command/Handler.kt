package me.nasukhov.TukitaLearner.bot.command

import me.nasukhov.TukitaLearner.bot.io.Input
import me.nasukhov.TukitaLearner.bot.io.Output

interface Handler {
    fun supports(input: Input): Boolean
    fun handle(input: Input, output: Output)
}
