package me.nasukhov.TukitaLearner.bot.io;

public record User(String id, String name) {
    public static User System = new User("SystemUser", "SystemUser");
}
