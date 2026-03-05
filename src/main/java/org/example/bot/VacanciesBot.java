package org.example.bot;

import org.example.model.Vacancy;
import org.example.service.WebScraperManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class VacanciesBot extends TelegramLongPollingBot {

    private final WebScraperManager webScraperManager;

    @Value("${telegram.bot.username}")
    private String botUsername;

    public VacanciesBot(@Value("${telegram.bot.token}") String botToken,
                        WebScraperManager webScraperManager) {
        super(botToken);
        this.webScraperManager = webScraperManager;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        String text = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        switch (text) {
            case "/start" -> sendMessageWithKeyboard(chatId,
                    "Привіт! Я бот для пошуку вакансій.\n\n" +
                            "Оберіть рівень вакансій:\n" +
                            "Junior — 0-1 рік досвіду (dou.ua + djinni.co)\n" +
                            "Middle — 1-3 роки досвіду (dou.ua + djinni.co, 2+ роки)\n" +
                            "Senior — 3-5 роки досвіду (dou.ua + djinni.co, 3+ роки)\n\n" +
                            "/help — показати довідку");

            case "Junior вакансії" -> {
                sendMessage(chatId, "Шукаю Junior вакансії на dou.ua та djinni.co...");
                List<Vacancy> vacancies = webScraperManager.scrapeJuniorVacancies();
                if (vacancies.isEmpty()) {
                    sendMessage(chatId, "Наразі Junior вакансій не знайдено. Спробуйте пізніше.");
                } else {
                    sendInChunks(chatId, "Java Junior вакансії (0-1 рік):\n\n", vacancies);
                }
            }

            case "Middle вакансії" -> {
                sendMessage(chatId, "Шукаю Middle вакансії на dou.ua та djinni.co...");
                List<Vacancy> vacancies = webScraperManager.scrapeMiddleVacancies();
                if (vacancies.isEmpty()) {
                    sendMessage(chatId, "Наразі Middle вакансій не знайдено. Спробуйте пізніше.");
                } else {
                    sendInChunks(chatId, "Java Middle вакансії (1-3 роки):\n\n", vacancies);
                }
            }

            case "Senior вакансії" -> {
                sendMessage(chatId, "Шукаю Senior вакансії на dou.ua та djinni.co...");
                List<Vacancy> vacancies = webScraperManager.scrapeSeniorVacancies();
                if (vacancies.isEmpty()) {
                    sendMessage(chatId, "Наразі Senior вакансій не знайдено. Спробуйте пізніше.");
                } else {
                    sendInChunks(chatId, "Java Senior вакансії (3-5 роки):\n\n", vacancies);
                }
            }

            case "/help" -> sendMessage(chatId,
                    "Доступні кнопки:\n" +
                            "Junior вакансії — Java вакансії 0-1 рік досвіду\n" +
                            "Middle вакансії — Java вакансії 1-3 роки досвіду\n" +
                            "Senior вакансії — Java вакансії 3-5 роки досвіду");

            default -> sendMessage(chatId,
                    "Натисніть кнопку або напишіть /help для довідки або /start.");
        }
    }

    private void sendInChunks(long chatId, String header, List<Vacancy> vacancies) {
        final int MAX_LENGTH = 4000;
        StringBuilder chunk = new StringBuilder(header);

        for (Vacancy v : vacancies) {
            String entry = """
📌 %s
🌍 Джерело: %s
🔗 %s

""".formatted(v.getTitle(), v.getSource(), v.getUrl());

            if (chunk.length() + entry.length() > MAX_LENGTH) {
                sendMessage(chatId, chunk.toString());
                chunk = new StringBuilder(header);
            }
            chunk.append(entry);
        }

        if (!chunk.isEmpty()) {
            sendMessage(chatId, chunk.toString());
        }
    }

    private void sendMessageWithKeyboard(long chatId, String text) {
        KeyboardRow row1 = new KeyboardRow();
        row1.add(new KeyboardButton("Junior вакансії"));
        row1.add(new KeyboardButton("Middle вакансії"));
        row1.add(new KeyboardButton("Senior вакансії"));

        KeyboardRow row2 = new KeyboardRow();
        row2.add(new KeyboardButton("/help"));
        row2.add(new KeyboardButton("/start"));

        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder()
                .keyboardRow(row1)
                .keyboardRow(row2)
                .resizeKeyboard(true)
                .build();

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(keyboard)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
