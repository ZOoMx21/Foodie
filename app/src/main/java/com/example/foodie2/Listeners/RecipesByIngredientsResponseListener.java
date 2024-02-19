package com.example.foodie2.Listeners;

import com.example.foodie2.Models.SearchByIngredientsApiResponse;

public interface RecipesByIngredientsResponseListener {
    void didFetch(SearchByIngredientsApiResponse response, String message);
    void didError(String message);
}
