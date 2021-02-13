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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pmuchatproject.R;
import com.example.pmuchatproject.commons.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private Context context;
    private List<RequestModel> requestModelList;

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
        RequestModel requestModel = requestModelList.get(position);

        holder.tvFullName.setText(requestModel.getUsername());

        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child(Constant.IMAGES_FOLDER + "/" + requestModel.getPhotoName());

        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri).placeholder(R.drawable.profil_users).error(R.drawable.profil_users).into(holder.ivProfile);
            }
        });
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
