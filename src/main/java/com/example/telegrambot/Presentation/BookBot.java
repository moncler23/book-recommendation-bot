package com.example.telegrambot.Presentation;

import com.example.telegrambot.BusinessLogic.GoogleBooksService;
import com.example.telegrambot.BusinessLogic.KeywordExtractor;
import com.example.telegrambot.Data.Book;
import com.example.telegrambot.Data.UserSession;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

public class BookBot extends TelegramLongPollingBot {

    private final GoogleBooksService googleBooksService = new GoogleBooksService();
    

    private final Map<String, UserSession> userSessions = new HashMap<>(); // data structure : HashMap that implements Map interface ->   Maps chat IDs to sessions


    private final Set<String> expectingQuotedGenre = new HashSet<>();

    @Override
    public String getBotUsername() {
        return "BooksRecommandations_bot";
    }

    @Override
    public String getBotToken() {
        return "7712945081:AAENwbCccARyXFNjOMqbMuIcrh8tIFC-_Wk";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                CallbackQuery callback = update.getCallbackQuery();
                String data = callback.getData();
                String chatId = callback.getMessage().getChatId().toString();

                if (data.startsWith("page:")) {
                    int newPage = Integer.parseInt(data.split(":")[1]);
                    UserSession session = userSessions.get(chatId);
                    if (session != null) {
                        session.setCurrentPage(newPage);
                        sendBookPage(chatId, session, callback.getMessage().getMessageId());
                    }
                } else {
                    List<Book> books = googleBooksService.fetchBooksFromGoogleAPI(data);
                    books.sort(Comparator.comparing(Book::getTitle));
                    UserSession session = new UserSession(books);
                    userSessions.put(chatId, session);
                    sendBookPage(chatId, session, callback.getMessage().getMessageId());
                }
                return;
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                String userMessage = update.getMessage().getText();
                String chatId = update.getMessage().getChatId().toString();

                if (userMessage.equals("/start")) {
                    execute(new SendMessage(chatId, "📚 Welcome to BookBot! Please press /genres command for available genres."));
                } else if (userMessage.equals("/stop")) {
                    execute(new SendMessage(chatId, "👋 See you again Bibliophile!"));
                } else if (userMessage.equals("/genres")) {
                    sendGenreButtons(chatId);
                    expectingQuotedGenre.add(chatId);
                } else if (userMessage.equals("/about")) {
                    execute(new SendMessage(chatId, " About BookBot\n\nThe purpose of this bot is to recommend you the best books of the chosen genre across the entire internet, selected from some of the best well-known authors around the globe. 🌍\n\nA book per day, keeps the philosopher away. ✨"));
                } else if (userMessage.equals("/help")) {
                    execute(new SendMessage(chatId, "ℹ️ This bot will recommend list of books based on available genres in the bot or from your own genre.\n\nDo followings:\n❗Press /genres to check for provided genres.\n❗You can send to bot a prompt about your book preference at least with one genre keyword inside \" \".\nExample: I like \"fiction\" books."));
                } else {
                    if (expectingQuotedGenre.contains(chatId)) {
                        if (userMessage.contains("\"")) {
                            String[] parts = userMessage.split("\"");
                            if (parts.length >= 2) {
                                String extractedGenre = parts[1];
                                List<Book> books = googleBooksService.fetchBooksFromGoogleAPI(extractedGenre);
                                books.sort(Comparator.comparing(Book::getTitle));
                                UserSession session = new UserSession(books);
                                userSessions.put(chatId, session);
                                sendBookPage(chatId, session, null);
                            } else {
                                execute(new SendMessage(chatId, "⚠️ Unclear input. Please use at least one genre keyword inside double quotes.\n\nExample: I like \"fiction\" books."));
                            }
                        } else {
                            execute(new SendMessage(chatId, "⚠️ Unclear input. Please include your genre in double quotes.\n\nExample: I like \"fiction\" books."));
                        }
                        expectingQuotedGenre.remove(chatId);
                    } else {
                        execute(new SendMessage(chatId, "❌ Unknown command! /help may be useful for you!"));
                    }
                }
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendGenreButtons(String chatId) throws TelegramApiException {
        SendMessage genreMessage = new SendMessage();
        genreMessage.setChatId(chatId);
        genreMessage.setText("📚 Choose a genre from the list below or you can send your genre!");

        Map<String, String> genreEmojis = Map.ofEntries(
                Map.entry("romance", "💖"),
                Map.entry("horror", "👻"),
                Map.entry("fantasy", "🧙"),
                Map.entry("mystery", "🕵️"),
                Map.entry("comedy", "😂"),
                Map.entry("science", "🔬"),
                Map.entry("thriller", "🔪"),
                Map.entry("finance", "💰"),
                Map.entry("religion", "🙏"),
                Map.entry("politics", "🏛️")
        );


        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (String genre : KeywordExtractor.getAvailableGenres()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            String emoji = genreEmojis.getOrDefault(genre, "");
            button.setText(emoji + " " + genre.substring(0, 1).toUpperCase() + genre.substring(1));
            button.setCallbackData(genre);
            buttons.add(Collections.singletonList(button));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(buttons);

        genreMessage.setReplyMarkup(markup);
        execute(genreMessage);
    }

    private void sendBookPage(String chatId, UserSession session, Integer messageId) throws TelegramApiException {
        int start = session.getCurrentPage() * 10;
        int end = Math.min(start + 10, session.getBooks().size());
        List<Book> pageBooks = session.getBooks().subList(start, end);

        StringBuilder text = new StringBuilder("📚 Recommended Books (" + (start + 1) + "–" + end + "):\n");
        for (int i = 0; i < pageBooks.size(); i++) {
            Book book = pageBooks.get(i);
            text.append(start + i + 1).append(". ")
                    .append(book.getTitle()).append(" — ")
                    .append(book.getAuthor()).append("\n");
        }

        InlineKeyboardButton prev = new InlineKeyboardButton();
        prev.setText("⏮ Previous");
        prev.setCallbackData("page:" + (session.getCurrentPage() - 1));

        InlineKeyboardButton next = new InlineKeyboardButton();
        next.setText("⏭ Next");
        next.setCallbackData("page:" + (session.getCurrentPage() + 1));

        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        if (session.getCurrentPage() > 0) rows.add(Collections.singletonList(prev));
        if ((session.getCurrentPage() + 1) * 10 < session.getBooks().size()) rows.add(Collections.singletonList(next));

        InlineKeyboardMarkup nav = new InlineKeyboardMarkup(rows);

        if (messageId != null) {
            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);
            editMessage.setText(text.toString());
            editMessage.setReplyMarkup(nav);
            execute(editMessage);
        } else {
            SendMessage message = new SendMessage(chatId, text.toString());
            message.setReplyMarkup(nav);
            execute(message);
        }
    }
}
