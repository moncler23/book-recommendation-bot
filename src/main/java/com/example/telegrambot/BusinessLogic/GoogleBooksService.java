package com.example.telegrambot.BusinessLogic;

import com.example.telegrambot.Data.Book;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class GoogleBooksService {

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=";


    public List<Book> fetchBooksFromGoogleAPI(String genre) {
        List<Book> books = new ArrayList<>();
        RestTemplate restTemplate = new RestTemplate();

        int totalToFetch = 100;  // max we want
        int step = 40;           // Google allows 40 per request max

        for (int startIndex = 0; startIndex < totalToFetch; startIndex += step) {
            String url = GOOGLE_BOOKS_API_URL + genre +
                    "&startIndex=" + startIndex +
                    "&maxResults=" + step;

            String response = restTemplate.getForObject(url, String.class);

            if (response == null || response.isEmpty()) continue;

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray items = jsonResponse.optJSONArray("items");

            if (items == null) continue;

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);
                if (!item.has("volumeInfo")) continue;

                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                String title = volumeInfo.optString("title", "Unknown Title");

                String author = "Unknown Author";
                if (volumeInfo.has("authors")) {
                    JSONArray authorsArray = volumeInfo.optJSONArray("authors");
                    if (authorsArray != null && authorsArray.length() > 0) {
                        author = authorsArray.optString(0, "Unknown Author");
                    }
                }

                double rating = volumeInfo.optDouble("averageRating", 0.0);

                books.add(new Book(title, author, genre, rating));
            }
        }

        // Sort alphabetically by title
        books.sort(Comparator.comparing(Book::getTitle));

        return books.size() > 100 ? books.subList(0, 100) : books;
    }
}
