package com.example.pmuchatproject.chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmuchatproject.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private Context context;
    private List<MessageModel> messageModelList;
    private FirebaseAuth firebaseAuth;

    public MessagesAdapter(Context context, List<MessageModel> messageModelList) {
        this.context = context;
        this.messageModelList = messageModelList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_layout, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

         MessageModel messageModel = messageModelList.get(position);

         firebaseAuth = FirebaseAuth.getInstance();

         String currentUserId = firebaseAuth.getCurrentUser().getUid();

         String fromUserId = messageModel.getMessageFrom();


        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String dataTime = sfd.format(new Date(messageModel.getMessageTime()));

        String [] splitString = dataTime.split(" ");
        String messageTime = splitString[1];

        if (fromUserId.equals(currentUserId))
        {
            holder.llSend.setVisibility(View.VISIBLE);
            holder.llReceived.setVisibility(View.GONE);

            holder.tvSendMessage.setText(messageModel.getMessage());
            holder.tvSendMessageTime.setText(messageTime);
        }
        else
        {
            holder.llSend.setVisibility(View.GONE);
            holder.llReceived.setVisibility(View.VISIBLE);

            holder.tvReceivedMessage.setText(messageModel.getMessage());
            holder.tvReceivedMessageTime.setText(messageTime);
        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout llReceived, llSend;
        private TextView tvSendMessage, tvSendMessageTime, tvReceivedMessage, tvReceivedMessageTime;
        private ConstraintLayout clMessage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            llSend = itemView.findViewById(R.id.llSend);
            llReceived = itemView.findViewById(R.id.llReceived);

            tvSendMessage = itemView.findViewById(R.id.tvSendMessage);
            tvSendMessageTime = itemView.findViewById(R.id.svSendMessageTime);

            tvReceivedMessage = itemView.findViewById(R.id.tvReceivedMessage);
            tvReceivedMessageTime = itemView.findViewById(R.id.svReceivedMessageTime);

            clMessage = itemView.findViewById(R.id.clMessage);
        }
    }
}
