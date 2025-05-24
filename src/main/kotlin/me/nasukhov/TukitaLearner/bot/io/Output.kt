package me.nasukhov.TukitaLearner.bot.io

interface Output {
    fun write(text: String)
    fun promptChoice(question: String, replyOptions: Map<String, String>)

    fun mention(userId: String): String {
        return "<user>$userId</user>"
    }
}
