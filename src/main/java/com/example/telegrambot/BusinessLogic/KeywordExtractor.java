package com.example.telegrambot.BusinessLogic;

import java.util.*;

public class KeywordExtractor {


    private static final Map<String, String> genreKeywords = new HashMap<>();


    static {
        genreKeywords.put("horror", "horror");
        genreKeywords.put("romance", "romance");
        genreKeywords.put("fantasy", "fantasy");
        genreKeywords.put("comedy", "comedy");
        genreKeywords.put("science", "science");
        genreKeywords.put("thriller", "thriller");
        genreKeywords.put("mystery", "mystery");
        genreKeywords.put("finance", "finance");
        genreKeywords.put("religion", "religion");
        genreKeywords.put("politics", "politics");

    }


    public static List<String> extractGenres(String prompt) {
        List<String> extractedGenres = new ArrayList<>();


        for (String word : prompt.toLowerCase().split(" ")) {


            if (genreKeywords.containsKey(word)) {
                extractedGenres.add(genreKeywords.get(word));
            }
        }

        return extractedGenres;
    }


    public static List<String> getAvailableGenres() {   // data structure : ArrayList
        return new ArrayList<>(genreKeywords.keySet());
    }
}
