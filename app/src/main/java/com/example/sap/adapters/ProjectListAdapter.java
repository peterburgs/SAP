package com.example.sap.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;
import com.example.sap.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {
    List<Project> projectList;

    public ProjectListAdapter(List<Project> projectList) {
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.project_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvProjectName.setText(projectList.get(position).getName());
        holder.tvProjectKey.setText(projectList.get(position).getKey());
        Amplify.Storage.getUrl(
                projectList.get(position).getAvatarKey(),
                result -> {
                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get().load(result.getUrl().toString()).into(holder.imvProjectImage);
                        }
                    });
                },
                error -> {
                    Log.e("GetProjectImageError", "Error", error);
                }
        );
    }

    @Override
    public int getItemCount() {

        return projectList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

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
