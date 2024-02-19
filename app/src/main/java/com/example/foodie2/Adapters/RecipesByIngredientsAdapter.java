package com.example.foodie2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodie2.Listeners.RecipeClickListener;
import com.example.foodie2.Models.Result;
import com.example.foodie2.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipesByIngredientsAdapter extends RecyclerView.Adapter<RecipesByIngredsViewHolder>{
    Context context;
    List<Result> list;
    RecipeClickListener listener;
    public RecipesByIngredientsAdapter(Context context, List<Result> list,RecipeClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener=listener;
    }
    @NonNull
    @Override
    public RecipesByIngredsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecipesByIngredsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_random_recipe, parent,false));
    }
    @Override
    public void onBindViewHolder(@NonNull RecipesByIngredsViewHolder holder, int position) {
        holder.textView_title.setText(list.get(position).title);
        holder.textView_title.setSelected(true);
        Picasso.get().load(list.get(position).image).into(holder.imageView_food);
        holder.random_list_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRecipeClicked(String.valueOf(list.get(holder.getAdapterPosition()).id));
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }
}
class RecipesByIngredsViewHolder extends RecyclerView.ViewHolder {
    CardView random_list_container;
    TextView textView_title;
    ImageView imageView_food;
    public RecipesByIngredsViewHolder(@NonNull View itemView) {
        super(itemView);
        random_list_container = itemView.findViewById(R.id.random_list_container);
        textView_title = itemView.findViewById(R.id.textView_title);
        imageView_food = itemView.findViewById(R.id.imageView_food);
    }
}
