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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pmuchatproject.R;
import com.example.pmuchatproject.commons.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.FindFriendViewHolder> {

    private Context context;
    private List<FindFriendModel> findFriendModelList;

    private DatabaseReference friendRequestDatabase;
    private FirebaseUser currentUser;

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
        FindFriendModel findFriendModel = findFriendModelList.get(position);

        holder.tvFullName.setText(findFriendModel.getUsername());
        StorageReference filRef = FirebaseStorage.getInstance().getReference().child(Constant.IMAGES_FOLDER +"/" + findFriendModel.getPhotoName());
        filRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.profil_users).error(R.drawable.profil_users)
                        .into(holder.ivProfile);
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
