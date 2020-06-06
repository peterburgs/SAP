package com.example.sap.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Comment;
import com.amplifyframework.datastore.generated.model.Project;
import com.amplifyframework.datastore.generated.model.User;
import com.example.sap.R;
import com.example.sap.activities.BacklogEditTaskActivity;
import com.example.sap.activities.LoadingDialog;
import com.example.sap.activities.SignupActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    List<Comment> commentList;
    private OnItemClickListener mListener;
    private LoadingDialog loadingDialog;

    //Interface to Handle Clicking a specific item
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    //Constructor
    public CommentListAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.comment_row, parent, false);

        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUserName.setText(commentList.get(position).getAuthor().getUsername());
        holder.tvCommentContent.setText(commentList.get(position).getContent());
        Amplify.Storage.getUrl(
                commentList.get(position).getAuthor().getAvatarKey(),
                result -> {
                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.get().load(result.getUrl().toString()).into(holder.imvAvatar);
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

        return commentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imvAvatar;
        TextView tvUserName;
        TextView tvCommentContent;
        ImageButton imbRemoveComment;
        protected CardView cvComment;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            cvComment = itemView.findViewById(R.id.cvComment);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
            imbRemoveComment = itemView.findViewById(R.id.imbRemoveComment);

            imbRemoveComment.setOnClickListener(new View.OnClickListener() {
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
