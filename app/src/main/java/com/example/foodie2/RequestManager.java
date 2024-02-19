package com.example.foodie2;
import android.content.Context;

import com.example.foodie2.Listeners.InstructionsListener;
import com.example.foodie2.Listeners.RandomRecipeResponseListener;
import com.example.foodie2.Listeners.RecipeDetailsListener;
import com.example.foodie2.Listeners.RecipesByIngredientsResponseListener;
import com.example.foodie2.Models.InstructionsResponse;
import com.example.foodie2.Models.RandomRecipeApiResponse;
import com.example.foodie2.Models.RecipeDetailsResponse;
import com.example.foodie2.Models.SearchByIngredientsApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
//this class is to manage api calls
public class RequestManager {
    Context context;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.spoonacular.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public RequestManager(Context context) {
        //this is class constructor
        this.context = context;
    }
    public void getRandomRecipes(RandomRecipeResponseListener listener,List<String> tags){
        CallRandomRecipes callRandomRecipes = retrofit.create(CallRandomRecipes.class);
        Call<RandomRecipeApiResponse> call = callRandomRecipes.callRandomRecipe(context.getString(R.string.api_Key), "10",tags);
        call.enqueue(new Callback<RandomRecipeApiResponse>() {
            @Override
            public void onResponse(Call<RandomRecipeApiResponse> call, Response<RandomRecipeApiResponse> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(),response.message());
            }
            @Override
            public void onFailure(Call<RandomRecipeApiResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }
    public void getRecipesByIngredients(RecipesByIngredientsResponseListener listener,String searchWord, String ingreds)
    {
        CallRecipesByIngredients callRecipesByIngredients = retrofit.create(CallRecipesByIngredients.class);
        Call<SearchByIngredientsApiResponse> call = callRecipesByIngredients.callRecipesByingredients(context.getString(R.string.api_Key),searchWord,ingreds,"10");
        call.enqueue(new Callback<SearchByIngredientsApiResponse>() {
            @Override
            public void onResponse(Call<SearchByIngredientsApiResponse> call, Response<SearchByIngredientsApiResponse> response) {
                if(!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(),response.message());
            }
            @Override
            public void onFailure(Call<SearchByIngredientsApiResponse> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }
    public void getRecipeDetails(RecipeDetailsListener listener, int id ){
        CallRecipeDetails callRecipeDetails = retrofit.create(CallRecipeDetails.class);
        Call<RecipeDetailsResponse> call = callRecipeDetails.callRecipeDetails(id, context.getString(R.string.api_Key));
        call.enqueue(new Callback<RecipeDetailsResponse>() {
            @Override
            public void onResponse(Call<RecipeDetailsResponse> call, Response<RecipeDetailsResponse> response) {
                if(!response.isSuccessful()){
                    listener.didFetch(response.body(), response.message());
                    return;
                }
                listener.didFetch(response.body(),response.message());
            }
            @Override
            public void onFailure(Call<RecipeDetailsResponse> call, Throwable t) {
            listener.didError(t.getMessage());
            }
        });
    }

    public void getInstructions(InstructionsListener listener, int id){
        CallInstructions callInstructions= retrofit.create(CallInstructions.class);
        Call<List<InstructionsResponse>> call = callInstructions.callInstructions(id, context.getString(R.string.api_Key));
        call.enqueue(new Callback<List<InstructionsResponse>>() {
            @Override
            public void onResponse(Call<List<InstructionsResponse>> call, Response<List<InstructionsResponse>> response) {
                if (!response.isSuccessful()){
                    listener.didError(response.message());
                    return;
                }
                listener.didFetch(response.body(),response.message());
            }

            @Override
            public void onFailure(Call<List<InstructionsResponse>> call, Throwable t) {
                listener.didError(t.getMessage());
            }
        });
    }

    private interface CallRandomRecipes{//this interface to handle GET method
        @GET("recipes/random")
        Call<RandomRecipeApiResponse> callRandomRecipe(
                @Query("apiKey") String apiKey,
                @Query("number") String number,
                @Query("tags") List<String> tags
                );
    }
    private interface CallRecipesByIngredients{//this interface to handle GET method
        @GET("recipes/complexSearch")
        Call<SearchByIngredientsApiResponse> callRecipesByingredients(
                @Query("apiKey") String apiKey,
                @Query("query") String searchWord,
                @Query("includeIngredients") String ingreds,//excludeIngredients, includeIngredients
                @Query("number") String number
        );
    }
    private interface CallRecipeDetails{
        @GET("recipes/{id}/information")
        Call<RecipeDetailsResponse> callRecipeDetails(
                @Path("id") int id,
                @Query("apiKey") String apiKey
        );
    }
    private interface CallInstructions{
        @GET("recipes/{id}/analyzedInstructions")
        Call<List<InstructionsResponse>> callInstructions(
                @Path("id") int id,
                @Query("apiKey") String apiKey
        );
    }
}