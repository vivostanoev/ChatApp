package com.example.pmuchatproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pmuchatproject.commons.Constant;
import com.example.pmuchatproject.commons.Extras;
import com.example.pmuchatproject.commons.NodeNames;
import com.example.pmuchatproject.commons.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.core.utilities.Utilities;

import org.w3c.dom.Node;

import java.util.HashMap;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{


    private ImageView ivSend;
    private EditText etMessage;

    private DatabaseReference mRootRef;
    private FirebaseAuth firebaseAuth;
    private String currentUserId, chatUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ivSend = findViewById(R.id.ivSent);
        etMessage = findViewById(R.id.etMessage);

        ivSend.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        mRootRef = FirebaseDatabase.getInstance().getReference();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        if (getIntent().hasExtra(Extras.USER_KEY))
        {
            chatUserId = getIntent().getStringExtra(Extras.USER_KEY);
        }
    }

    private void sendMessage(String msg, String msgType, String pushId)
    {
        try {
            if (!msg.equals(""))
            {
                HashMap messageMap = new HashMap();
                messageMap.put(NodeNames.MESSAGE_ID, pushId);
                messageMap.put(NodeNames.MESSAGE, msg);
                messageMap.put(NodeNames.MESSAGE_TYPE, msgType);
                messageMap.put(NodeNames.MESSAGE_FROM, currentUserId);
                messageMap.put(NodeNames.MESSAGE_TIME, ServerValue.TIMESTAMP);

                String currentUserRef = NodeNames.MESSAGES + "/" + currentUserId + "/" + chatUserId;
                String chatUserRef = NodeNames.MESSAGES + "/" + chatUserId + "/" + currentUserId;


                HashMap messageUserMap = new HashMap();
                messageUserMap.put(currentUserRef + "/" + pushId, messageMap);
                messageUserMap.put(chatUserRef + "/" + pushId, messageMap);


                etMessage.setText("");

                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                        if (error != null)
                        {
                            Toast.makeText(ChatActivity.this, getString(R.string.failed_to_send_message, error.getMessage()), Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(ChatActivity.this, R.string.message_send_successful, Toast.LENGTH_SHORT).show();


                        }
                    }
                });
            }
        }
        catch (Exception e)
        {
            Toast.makeText(ChatActivity.this, getString(R.string.failed_to_send_message, e.getMessage()), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivSent :
                if(Util.connectionAvailable(this)) {
                    DatabaseReference userMessagePush = mRootRef.child(NodeNames.MESSAGES).child(currentUserId).child(chatUserId).push();
                    String pushId = userMessagePush.getKey();
                    sendMessage(etMessage.getText().toString().trim(), Constant.MESSAGE_TYPE_TEXT, pushId);
                }
                else
                {
                    Toast.makeText(this, R.string.no_internet, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}