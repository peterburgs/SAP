package com.example.sap;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Project;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

public class ProjectListAdapter extends RecyclerView.Adapter<ProjectListAdapter.ViewHolder> {
    List<Project> projectList;
    Context context;

    public ProjectListAdapter(Context ct, List<Project> projectList) {
        context = ct;
        this.projectList = projectList;
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
        holder.tvProjectName.setText(projectList.get(position).getName());
        holder.tvProjectKey.setText(projectList.get(position).getKey());

        ContextWrapper cw = new ContextWrapper(context.getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File path =new File(directory,projectList.get(position).getAvatarKey());
        try {
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(path));
            holder.imvProjectImage.setImageBitmap(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return projectList.size();
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
