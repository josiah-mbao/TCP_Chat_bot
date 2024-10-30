package com.example.tcp_chatbot;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private DatabaseReference databaseReference;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");

        TextView chatWith = findViewById(R.id.chatWith);
        chatWith.setText("Chat with " + userName);

        recyclerView = findViewById(R.id.recyclerView);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        EditText messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("messages");

        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                String messageId = databaseReference.push().getKey();
                String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Message message = new Message(messageId, messageText, senderId, userId);
                databaseReference.child(messageId).setValue(message);
                messageInput.setText("");
            }
        });

        loadMessages();
    }

    private void loadMessages() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if ((message.getSenderId().equals(userId) && message.getReceiverId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) ||
                            (message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && message.getReceiverId().equals(userId))) {
                        messageList.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the last message
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}
