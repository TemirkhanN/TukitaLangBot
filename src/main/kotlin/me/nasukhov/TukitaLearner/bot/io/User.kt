package me.nasukhov.TukitaLearner.bot.io

data class User(
    val id: String,
    val name: String
) {
    companion object {
        // TODO check if I need this
        var System = User("SystemUser", "SystemUser")
    }
}
