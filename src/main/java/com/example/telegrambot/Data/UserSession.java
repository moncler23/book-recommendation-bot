package com.example.telegrambot.Data;

import java.util.List;
import com.example.telegrambot.Data.Book;

public class UserSession {
    private List<Book> books;
    private int currentPage;

    public UserSession(List<Book> books) {
        this.books = books;
        this.currentPage = 0;
    }

    public List<Book> getBooks() {
        return books;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int page) {
        this.currentPage = page;
    }
}
