package com.example.foodie2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodie2.Models.Step;
import com.example.foodie2.R;

import java.util.List;

public class InstructionStepAdapter extends RecyclerView.Adapter<InstructionStepViewHolder>{

    Context context;
    List<Step> list;

    public InstructionStepAdapter(Context context, List<Step> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionStepViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_steps, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionStepViewHolder holder, int position) {
        holder.txt_instruction_step_number.setText(String.valueOf(list.get(position).number));
        holder.txt_instruction_step_title.setText(list.get(position).step);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class InstructionStepViewHolder extends RecyclerView.ViewHolder{

    TextView txt_instruction_step_number, txt_instruction_step_title;
    public InstructionStepViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_instruction_step_number = itemView.findViewById(R.id.txt_instruction_step_number);
        txt_instruction_step_title = itemView.findViewById(R.id.txt_instruction_step_title);
    }
}
