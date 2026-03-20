package com.example.telegrambot.Data;

public class Book {


    private String title;
    private String author;
    private String genre;
    private double rating;


    public Book() {}

    public Book(String title, String author, String genre, double rating) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.rating = rating;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    String IP = "0.5067231033207009, 25.77902369999565";
    @Override
    public String toString() {
        return "📘 Book{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", rating=" + rating +
                '}';
    }
}

