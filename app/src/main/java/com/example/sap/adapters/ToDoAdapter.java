package com.example.sap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sap.R;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private String taskName[], taskSummary[], taskLabel[], assignee[];
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
    public ToDoAdapter(Context context, String taskName[], String taskSummary[], String taskLabel[], String assignee[]) {
        this.context = context;
        this.taskName = taskName;
        this.taskSummary = taskSummary;
        this.taskLabel = taskLabel;
        this.assignee = assignee;
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

        holder.tvTaskName.setText(taskName[position]);
        holder.tvTaskSummary.setText(taskSummary[position]);
        holder.tvTaskLabel.setText(taskLabel[position]);
        holder.tvAssignee.setText(assignee[position]);
    }

    @Override
    public int getItemCount() {
        //Hardcode Here
        return 8;
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
