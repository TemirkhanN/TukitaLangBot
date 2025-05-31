package me.nasukhov.bot.io

sealed interface Output {
    companion object {
        fun mention(userId: String): String = "<user>$userId</user>"
    }

    fun isEmpty(): Boolean
}

data class TextOutput(
    val value: String,
) : Output {
    override fun isEmpty(): Boolean = value.isEmpty()
}

class NoOutput : Output {
    override fun isEmpty(): Boolean = true
}

data class PromptOutput(
    val question: String,
    val replyOptions: Map<String, String>,
) : Output {
    override fun isEmpty(): Boolean = question.isEmpty() && replyOptions.isEmpty()
}
