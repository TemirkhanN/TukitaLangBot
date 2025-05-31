package me.nasukhov.bot.io

data class User(
    val id: String,
    val name: String,
) {
    companion object {
        // TODO check if I need this
        val system = User("SystemUser", "SystemUser")
    }
}
