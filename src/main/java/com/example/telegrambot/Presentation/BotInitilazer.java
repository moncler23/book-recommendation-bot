package com.example.telegrambot.Presentation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
 class BotInitializer {


    @Bean
    public TelegramBotsApi telegramBotsApi(BookBot bookBot) throws TelegramApiException {

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);


        botsApi.registerBot(bookBot);

        return botsApi;
    }


    @Bean
    public BookBot bookBot() {
        return new BookBot();
    }
}
