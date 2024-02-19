package com.example.foodie2.Models;

import java.util.ArrayList;

//this class to handle Search By Ingredients Response from the Api

public class SearchByIngredientsApiResponse {
    public ArrayList<Result> results;
    public int offset;
    public int number;
    public int totalResults;
}
