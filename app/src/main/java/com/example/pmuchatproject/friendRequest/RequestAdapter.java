package com.example.pmuchatproject.friendRequest;

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
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<RequestModel> requestModelList;
    private DatabaseReference databaseReferenceFriendRequest, databaseReferenceChat;
    private FirebaseUser firebaseUser;

    public RequestAdapter(Context context, List<RequestModel> requestModelList) {
        this.context = context;
        this.requestModelList = requestModelList;
    }

    @NonNull
    @Override
    public RequestAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friend_request_layout, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestAdapter.RequestViewHolder holder, int position) {
        final RequestModel requestModel = requestModelList.get(position);

        holder.tvFullName.setText(requestModel.getUsername());

        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(Constant.IMAGES_FOLDER + "/" + requestModel.getPhotoName());

        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.profil_users).error(R.drawable.profil_users).into(holder.ivProfile);
            }
        });


        databaseReferenceFriendRequest = FirebaseDatabase.getInstance().getReference().child(NodeNames.FRIEND_REQUEST);
        databaseReferenceChat = FirebaseDatabase.getInstance().getReference().child(NodeNames.CHATS);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        holder.btnAcceptRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.pbDecision.setVisibility(View.VISIBLE);
                holder.btnDeclineRequest.setVisibility(View.GONE);
                holder.btnAcceptRequest.setVisibility(View.GONE);

                final String userID = requestModel.getUserId();

                databaseReferenceChat.child(firebaseUser.getUid()).child(userID)
                        .child(NodeNames.TIME_STAMP)
                        .setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            databaseReferenceChat.child(userID).child(firebaseUser.getUid())
                                    .child(NodeNames.TIME_STAMP)
                                    .setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        databaseReferenceFriendRequest.child(firebaseUser.getUid()).child(userID)
                                                .child(NodeNames.REQUEST_TYPE)
                                                .setValue(Constant.REQUEST_DATA_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful())
                                                {
                                                    databaseReferenceFriendRequest.child(userID).child(firebaseUser.getUid())
                                                            .child(NodeNames.REQUEST_TYPE)
                                                            .setValue(Constant.REQUEST_DATA_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful())
                                                            {
                                                                holder.pbDecision.setVisibility(View.GONE);
                                                                holder.btnDeclineRequest.setVisibility(View.VISIBLE);
                                                                holder.btnAcceptRequest.setVisibility(View.VISIBLE);
                                                            }
                                                            else
                                                            {
                                                                handleException(holder, task.getException());
                                                            }
                                                        }
                                                    });
                                                }
                                                else
                                                {
                                                    handleException(holder,task.getException());
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        handleException(holder, task.getException());
                                    }
                                }
                            });
                        }
                        else
                        {
                            handleException(holder, task.getException());
                        }
                    }
                });
            }
        });

        holder.btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.pbDecision.setVisibility(View.VISIBLE);
                holder.btnDeclineRequest.setVisibility(View.GONE);
                holder.btnAcceptRequest.setVisibility(View.GONE);

                final String userID = requestModel.getUserId();

                databaseReferenceFriendRequest.child(firebaseUser.getUid())
                        .child(userID).child(NodeNames.REQUEST_TYPE)
                        .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            databaseReferenceFriendRequest.child(userID)
                                    .child(firebaseUser.getUid()).child(NodeNames.REQUEST_TYPE)
                                    .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        holder.pbDecision.setVisibility(View.GONE);
                                        holder.btnDeclineRequest.setVisibility(View.VISIBLE);
                                        holder.btnAcceptRequest.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        Toast.makeText(context, context.getString(R.string.failed_decline_request, task.getException()), Toast.LENGTH_SHORT).show();
                                        holder.pbDecision.setVisibility(View.GONE);
                                        holder.btnDeclineRequest.setVisibility(View.VISIBLE);
                                        holder.btnAcceptRequest.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(context, context.getString(R.string.failed_decline_request, task.getException()), Toast.LENGTH_SHORT).show();
                            holder.pbDecision.setVisibility(View.GONE);
                            holder.btnDeclineRequest.setVisibility(View.VISIBLE);
                            holder.btnAcceptRequest.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    private void handleException(RequestViewHolder holder, Exception exception) {
        Toast.makeText(context, context.getString(R.string.failed_accepted_request, exception), Toast.LENGTH_SHORT).show();
        holder.pbDecision.setVisibility(View.GONE);
        holder.btnDeclineRequest.setVisibility(View.VISIBLE);
        holder.btnAcceptRequest.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return requestModelList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder{

        private TextView tvFullName;
        private ImageView ivProfile;
        private Button btnAcceptRequest, btnDeclineRequest;
        private ProgressBar pbDecision;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFullName = itemView.findViewById(R.id.tvFullname);
            ivProfile = itemView.findViewById(R.id.ivProfil);
            btnAcceptRequest = itemView.findViewById(R.id.btnAccept);
            btnDeclineRequest = itemView.findViewById(R.id.btnDecline);
            pbDecision = itemView.findViewById(R.id.pbDecision);
        }

    }
}
