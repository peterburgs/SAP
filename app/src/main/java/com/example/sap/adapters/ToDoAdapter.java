package com.example.sap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private ArrayList<Task> taskList;
    private Context context;
    private OnItemClickListener mListener;

    //Interface to Handle Clicking a specific item
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    //Constructor
    public ToDoAdapter(Context context, ArrayList<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.todo_row, parent, false);

        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvTaskName.setText(taskList.get(position).getName());
        holder.tvTaskSummary.setText(taskList.get(position).getSummary());
        holder.tvTaskLabel.setText(taskList.get(position).getLabel());
        holder.tvAssignee.setText(taskList.get(position).getAssignee().getUsername());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTaskName, tvTaskSummary, tvTaskLabel, tvAssignee;
        protected CardView cvTodo;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            tvTaskName = itemView.findViewById(R.id.tvTaskName);
            tvTaskSummary = itemView.findViewById(R.id.tvTaskSummary);
            tvTaskLabel = itemView.findViewById(R.id.tvTaskLabel);
            tvAssignee = itemView.findViewById(R.id.tvAssignee);
            cvTodo = itemView.findViewById(R.id.cvToDo);

            cvTodo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
