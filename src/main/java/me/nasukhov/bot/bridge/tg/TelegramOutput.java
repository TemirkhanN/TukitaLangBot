package me.nasukhov.bot.bridge.tg;

import me.nasukhov.bot.Output;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TelegramOutput implements Output {
    private static final Pattern PLACEHOLDER_USERNAME = Pattern.compile("<user>(-?\\d+)</user>");
    private static final Map<Long, String> userNames = new ConcurrentHashMap<>();

    private final Long chatId;
    private final Telegram api;

    public TelegramOutput(Long chatId, Telegram tg) {
        this.chatId = chatId;
        api = tg;
    }

    @Override
    public void write(String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(renderText(text));
        message.disableNotification();
        try {
            api.execute(message);
        } catch (TelegramApiException e) {
            // TODO
            e.printStackTrace();
        }
    }

    @Override
    public void promptChoice(String question, Map<String, String> replyOptions) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(question);
        message.setReplyMarkup(createOptions(replyOptions));
        message.disableNotification();
        try {
            api.execute(message);
        } catch (TelegramApiException e) {
            // TODO
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup createOptions(Map<String, String> options) {
        InlineKeyboardMarkup inlineKeyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (Map.Entry<String, String> entry : options.entrySet()) {
            InlineKeyboardButton button1 = new InlineKeyboardButton();
            button1.setText(entry.getKey());
            // allows only 64bytes
            button1.setCallbackData(entry.getValue());

            // One button per line
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(button1);
            rowsInline.add(rowInline);
        }

        inlineKeyboard.setKeyboard(rowsInline);

        return inlineKeyboard;
    }

    private String renderText(String text) {
        Matcher matcher = PLACEHOLDER_USERNAME.matcher(text);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            Long userId = Long.parseLong(matcher.group(1));

            Optional<String> name = getUsername(userId);
            if (name.isEmpty()) {
                // This effectively means user had left the channel
                matcher.appendReplacement(result, "Unknown");
            } else {
                matcher.appendReplacement(result, name.get());
            }
        }
        matcher.appendTail(result);

        return result.toString();
    }

    public Optional<String> getUsername(Long userId) {
        if (userNames.containsKey(userId)) {
            return Optional.of(userNames.get(userId));
        }

        GetChatMember command = new GetChatMember();
        command.setChatId(chatId);
        command.setUserId(userId);
        try {
            String name = api.execute(command).getUser().getFirstName();
            userNames.put(userId, name);

            return Optional.of(name);
        } catch (TelegramApiException e) {
            return Optional.empty();
        }
    }
}
