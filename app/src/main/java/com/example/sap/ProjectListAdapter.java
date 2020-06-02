package com.example.sap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {
    String data1[], data2[];
    int image[];
    Context context;

    public ProjectListAdapter(Context ct, String s1[], String s2[], int img[]) {
        context = ct;
        data1 = s1;
        data2 = s2;
        image = img;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.project_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvProjectName.setText(data1[position]);
        holder.tvProjectKey.setText(data2[position]);
        holder.imvProjectImage.setImageResource(image[position]);
    }

    @Override
    public int getItemCount() {

        return data1.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvProjectName;
        TextView tvProjectKey;
        ImageView imvProjectImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.tvProjectName);
            tvProjectKey = itemView.findViewById(R.id.tvProjectKey);
            imvProjectImage = itemView.findViewById(R.id.imvProjectImage);

        }
    }
}
