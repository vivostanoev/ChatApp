package com.example.pmuchatproject.findfriends;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pmuchatproject.R;
import com.example.pmuchatproject.commons.Constant;
import com.example.pmuchatproject.commons.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.FindFriendViewHolder> {

    private Context context;
    private List<FindFriendModel> findFriendModelList;

    private DatabaseReference friendRequestDatabase;
    private FirebaseUser currentUser;
    private String userId;

    public FindFriendAdapter(Context context, List<FindFriendModel> findFriendModelList) {
        this.context = context;
        this.findFriendModelList = findFriendModelList;
    }

    @NonNull
    @Override
    public FindFriendAdapter.FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.find_friends_layout, parent, false);
        return new FindFriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FindFriendAdapter.FindFriendViewHolder holder, int position) {
        final FindFriendModel findFriendModel = findFriendModelList.get(position);

        holder.tvFullName.setText(findFriendModel.getUsername());
        StorageReference filRef = FirebaseStorage.getInstance().getReference().child(Constant.IMAGES_FOLDER +"/" + findFriendModel.getPhotoName());
        filRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.profil_users).error(R.drawable.profil_users)
                        .into(holder.ivProfile);
            }
        });


        friendRequestDatabase = FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUEST);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (findFriendModel.isRequestSent())
        {
            holder.btnSendRequest.setVisibility(View.GONE);
            holder.btnCancelRequest.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.btnSendRequest.setVisibility(View.VISIBLE);
            holder.btnCancelRequest.setVisibility(View.GONE);
        }

        holder.btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnCancelRequest.setVisibility(View.GONE);

                holder.progressBar.setVisibility(View.VISIBLE);

                userId = findFriendModel.getUserId();

                friendRequestDatabase.child(currentUser.getUid()).child(userId).child(NodeNames.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            friendRequestDatabase.child(userId).child(currentUser.getUid()).child(NodeNames.REQUEST_TYPE).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(context,R.string.request_cancel_successfully, Toast.LENGTH_SHORT).show();
                                        holder.btnSendRequest.setVisibility(View.VISIBLE);
                                        holder.progressBar.setVisibility(View.GONE);
                                        holder.btnCancelRequest.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        Toast.makeText(context, context.getString(R.string.request_cancel_failed, task.getException()), Toast.LENGTH_SHORT).show();
                                        holder.btnSendRequest.setVisibility(View.GONE);
                                        holder.progressBar.setVisibility(View.GONE);
                                        holder.btnCancelRequest.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        holder.btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.btnSendRequest.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);

                userId = findFriendModel.getUserId();

                friendRequestDatabase.child(currentUser.getUid()).child(userId).child(NodeNames.REQUEST_TYPE).setValue(Constant.REQUEST_DATA_SENT).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            friendRequestDatabase.child(userId).child(currentUser.getUid()).child(NodeNames.REQUEST_TYPE).setValue(Constant.REQUEST_DATA_RECEIVED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(context,R.string.request_sent_successfully, Toast.LENGTH_SHORT).show();
                                        holder.btnSendRequest.setVisibility(View.GONE);
                                        holder.progressBar.setVisibility(View.GONE);
                                        holder.btnCancelRequest.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        Toast.makeText(context, context.getString(R.string.request_sent_failed, task.getException()), Toast.LENGTH_SHORT).show();
                                        holder.btnSendRequest.setVisibility(View.VISIBLE);
                                        holder.progressBar.setVisibility(View.GONE);
                                        holder.btnCancelRequest.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }



    @Override
    public int getItemCount() {
        return findFriendModelList.size();
    }

    public class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView ivProfile;
        private TextView tvFullName;
        private Button btnSendRequest, btnCancelRequest;
        private ProgressBar progressBar;
        public FindFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvFullName = itemView.findViewById(R.id.fullName);
            btnSendRequest = itemView.findViewById(R.id.btnSandRequest);
            btnCancelRequest = itemView.findViewById(R.id.btnCancelRequest);
            progressBar = itemView.findViewById(R.id.pbRequest);
        }
    }
}
