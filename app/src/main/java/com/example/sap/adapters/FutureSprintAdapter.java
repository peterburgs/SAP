package com.example.sap.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Sprint;
import com.example.sap.R;

import java.util.ArrayList;

public class FutureSprintAdapter extends RecyclerView.Adapter<FutureSprintAdapter.ViewHolder> {

    private ArrayList<Sprint> sprintList;
    Context context;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    //Constructor
    public FutureSprintAdapter(Context context, ArrayList<Sprint> sprintList) {
        this.sprintList = sprintList;
        this.context = context;
    }

    @NonNull
    @Override
    public FutureSprintAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.future_row, parent, false);

        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvSprintName.setText(sprintList.get(position).getName());
        holder.tvSprintGoal.setText(sprintList.get(position).getGoal());
    }

    @Override
    public int getItemCount() {
        return sprintList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSprintName, tvSprintGoal;
        protected CardView cvFuture;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            tvSprintName = itemView.findViewById(R.id.tvSprintName);
            tvSprintGoal = itemView.findViewById(R.id.tvSprintGoal);
            cvFuture = itemView.findViewById(R.id.cvFuture);

            cvFuture.setOnClickListener(new View.OnClickListener() {
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
