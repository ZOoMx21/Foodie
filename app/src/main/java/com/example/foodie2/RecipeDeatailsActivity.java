package com.example.foodie2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodie2.Adapters.IngredientsAdapter;
import com.example.foodie2.Adapters.InstructionsAdapter;
import com.example.foodie2.Listeners.InstructionsListener;
import com.example.foodie2.Listeners.RecipeDetailsListener;
import com.example.foodie2.Models.InstructionsResponse;
import com.example.foodie2.Models.RecipeDetailsResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeDeatailsActivity extends AppCompatActivity {

    int id ;
    TextView textView_meal_name,textview_meal_source;
    ImageView imageView_meal_image, img_watchYoutube;
    RecyclerView recycler_meal_ingredients, recycler_meal_instructions;
    RequestManager manager;
    ProgressDialog dialog ;
    IngredientsAdapter ingredientsAdapter;
    InstructionsAdapter instructionsAdapter;
    String recipeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_deatails);
        img_watchYoutube = findViewById(R.id.img_watchYoutube);
        img_watchYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRecipeUrl();
            }
        });

        findViews();
        id = Integer.parseInt(getIntent().getStringExtra("id"));
        manager = new RequestManager(this);
        manager.getRecipeDetails(recipeDetailsListener,id);
        manager.getInstructions(instructionsListener, id);
        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading Details ...");
        dialog.show();
    }

    private void gotoUrl(String url) {
        Uri uri = Uri.parse(url);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            dialog.dismiss();
            textView_meal_name.setText(response.title);
            recipeUrl = response.title;
            textview_meal_source.setText("sorce: "+response.sourceName);
            Picasso.get().load(response.image).into(imageView_meal_image);
            recycler_meal_ingredients.setHasFixedSize(true);
            recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDeatailsActivity.this,LinearLayoutManager.HORIZONTAL, false));
            ingredientsAdapter = new IngredientsAdapter(RecipeDeatailsActivity.this,response.extendedIngredients);
            recycler_meal_ingredients.setAdapter(ingredientsAdapter);
        }
        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDeatailsActivity.this,message, Toast.LENGTH_SHORT).show();
        }
    };
    private void findViews() {
        textView_meal_name = findViewById(R.id.textView_meal_name);
        textview_meal_source = findViewById(R.id.textview_meal_source);
        imageView_meal_image = findViewById(R.id.imageView_meal_image);
        recycler_meal_ingredients = findViewById(R.id.recycler_meal_ingredients);
        recycler_meal_instructions =findViewById(R.id.recycler_meal_instructions);
    }
    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didFetch(List<InstructionsResponse> response, String message) {
            recycler_meal_instructions.setHasFixedSize(true);
            recycler_meal_instructions.setLayoutManager(new LinearLayoutManager(RecipeDeatailsActivity.this, LinearLayoutManager.VERTICAL,false));
            instructionsAdapter= new InstructionsAdapter(RecipeDeatailsActivity.this, response);
            recycler_meal_instructions.setAdapter(instructionsAdapter);
        }

        @Override
        public void didError(String message) {

        }
    };
    private void getRecipeUrl()
    {
        if (recipeUrl.contains(" ")) recipeUrl = recipeUrl.replace(" ", "+");
        gotoUrl("https://www.youtube.com/results?search_query="+recipeUrl);
    }
}