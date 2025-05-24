package me.nasukhov.tukitalearner.bot.io

data class Input(
    val raw: String,
    val channel: Channel,
    val sender: User,
) {
    fun isDirectCommand(command: String): Boolean = raw == "/$command" || raw.startsWith("/$command@")

    override fun toString(): String = raw
}
