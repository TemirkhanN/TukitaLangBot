package me.nasukhov.bot.command

import me.nasukhov.bot.io.Input
import me.nasukhov.bot.io.Output

interface Handler {
    fun supports(input: Input): Boolean

    fun handle(input: Input): Output
}
