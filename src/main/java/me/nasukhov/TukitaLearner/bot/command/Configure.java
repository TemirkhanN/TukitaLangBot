package me.nasukhov.TukitaLearner.bot.command;

import jakarta.persistence.EntityManager;
import me.nasukhov.TukitaLearner.bot.io.Input;
import me.nasukhov.TukitaLearner.bot.io.Output;
import me.nasukhov.TukitaLearner.study.Group;
import me.nasukhov.TukitaLearner.study.Preferences;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Configure implements Handler {
    private static final Pattern INTERVAL_PATTERN = Pattern.compile("^cfg .+ (\\d+)([mhd])$");
    private static final int MIN_INTERVAL = 60;
    private static final int MAX_INTERVAL = 7 * 24 * 60;

    private final EntityManager db;

    public Configure(EntityManager db) {
        this.db = db;
    }

    @Override
    public boolean supports(Input input) {
        return input.isDirectCommand("config") || input.input().startsWith("cfg ");
    }

    @Override
    public void handle(Input input, Output output) {
        if (!supports(input)) {
            return;
        }

        if (input.isDirectCommand("config")) {
            output.promptChoice("Настройка", new LinkedHashMap<>() {{
                put("Включить ежедневные факты", "cfg facts enable");
                put("Выключить ежедневные факты", "cfg facts disable");
                put("Включить авто-вопросы", "cfg asker enable");
                put("Выключить авто-вопросы", "cfg asker disable");
                put("Спрашивать каждый час", "cfg asker interval 1h");
                put("Спрашивать каждые 2 часа", "cfg asker interval 2h");
                put("Спрашивать раз в день", "cfg asker interval 1d");
            }});

            return;
        }

        Group group = new Group(input.channel().id);
        Preferences preferences = group.preferences(db);

        if (input.input().equals("cfg asker enable")) {
            preferences.enableAutoAsker(true);
            output.write("Авто-вопросы включены");

            return;
        }

        if (input.input().equals(("cfg asker disable"))) {
            preferences.enableAutoAsker(false);
            output.write("Авто-вопросы выключены");

            return;
        }

        if (input.input().equals(("cfg facts enable"))) {
            preferences.enableFactSharing(true);
            output.write("Факты включены");

            return;
        }

        if (input.input().equals(("cfg facts disable"))) {
            preferences.enableFactSharing(false);
            output.write("Факты выключены");

            return;
        }

        if (input.input().startsWith(("cfg asker interval"))) {
            preferences.autoAskEveryXMinutes(parseInterval(input));
            output.write("Интервал между авто-вопросами сохранен");

            return;
        }

        if (input.input().startsWith(("cfg fact interval"))) {
            preferences.shareFactEveryXMinutes(parseInterval(input));
            output.write("Интервал между фактами сохранен");

            return;
        }

        output.write("Unknown configuration command!");
    }

    private int parseInterval(Input input) {
        Matcher matcher = INTERVAL_PATTERN.matcher(input.toString());
        if (!matcher.find()) {
            return 0;
        }

        int interval = Integer.parseInt(matcher.group(1));
        switch (matcher.group(2)) {
            case "d" -> interval *= 24 * 60;
            case "h" -> interval *= 60;
            default -> {
            }
        }

        if (interval > MAX_INTERVAL) {
            interval = MAX_INTERVAL;
        }

        if (interval < MIN_INTERVAL) {
            interval = MIN_INTERVAL;
        }

        return interval;
    }
}
