package me.nasukhov.tukitalearner.bot.io

interface Output {
    fun write(text: String)

    fun promptChoice(
        question: String,
        replyOptions: Map<String, String>,
    )

    fun mention(userId: String): String = "<user>$userId</user>"
}
