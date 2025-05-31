package me.nasukhov.bot.io

data class Input(
    val raw: String,
    val channel: Channel,
    val sender: User,
) {
    val isEmpty
        get() = raw.isEmpty()

    fun isDirectCommand(command: String): Boolean = raw == "/$command" || raw.startsWith("/$command@")

    override fun toString(): String = raw
}
