package com.example.sap.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Comment;
import com.amplifyframework.datastore.generated.model.ProjectParticipant;
import com.amplifyframework.datastore.generated.model.Task;
import com.example.sap.R;
import com.example.sap.activities.LoadingDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ParticipantListAdapter extends RecyclerView.Adapter<ParticipantListAdapter.ViewHolder> {

    private ArrayList<ProjectParticipant> participantList;
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
    public ParticipantListAdapter(Context context, ArrayList<ProjectParticipant> participantList) {
        this.context = context;
        this.participantList = participantList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.participant_row, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvUserName.setText(participantList.get(position).getMember().getUsername());
        holder.tvRole.setText(participantList.get(position).getRole().toString());
        holder.tvEmail.setText(participantList.get(position).getMember().getEmail());

        Amplify.Storage.getUrl(
                participantList.get(position).getMember().getAvatarKey(),
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
        return participantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imvAvatar;
        TextView tvUserName;
        TextView tvRole;
        TextView tvEmail;
        ImageButton imbRemoveParticipant;
        protected CardView cvParticipant;

        public ViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            cvParticipant = itemView.findViewById(R.id.cvParticipant);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            imvAvatar = itemView.findViewById(R.id.imvAvatar);
            imbRemoveParticipant = itemView.findViewById(R.id.imbRemoveParticipant);

            imbRemoveParticipant.setOnClickListener(new View.OnClickListener() {
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
