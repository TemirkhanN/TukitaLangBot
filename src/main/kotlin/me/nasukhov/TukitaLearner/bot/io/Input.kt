package me.nasukhov.TukitaLearner.bot.io

data class Input(val raw: String, val channel: Channel, val sender: User) {
    fun isDirectCommand(command: String): Boolean {
        return raw == "/$command" || raw.startsWith("/$command@")
    }

    override fun toString(): String {
        return raw
    }
}
