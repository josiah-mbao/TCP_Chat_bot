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

        initializeVariables();
        setupUI();
        loadMessages();
    }

    private void initializeVariables() {
        userId = getIntent().getStringExtra("userId");
        userName = getIntent().getStringExtra("userName");
        databaseReference = FirebaseDatabase.getInstance().getReference("messages");
        messageList = new ArrayList<>();
    }

    private void setupUI() {
        TextView chatWith = findViewById(R.id.chatWith);
        chatWith.setText(getString(R.string.chat_with, userName));

        recyclerView = findViewById(R.id.recyclerView);
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        EditText messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> sendMessage(messageInput));
    }

    private void sendMessage(EditText messageInput) {
        String messageText = messageInput.getText().toString().trim();
        if (!messageText.isEmpty()) {
            String messageId = databaseReference.push().getKey();
            String senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Message message = new Message(messageId, messageText, senderId, userId);
            databaseReference.child(messageId).setValue(message)
                .addOnSuccessListener(aVoid -> messageInput.setText(""))
                .addOnFailureListener(e -> {
                    // Handle failure (e.g., show a toast message)
                });
        }
    }

    private void loadMessages() {
        Query query = databaseReference.orderByChild("timestamp"); // Add timestamp for ordering
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Message message = snapshot.getValue(Message.class);
                    if (message != null && isMessageRelevant(message)) {
                        messageList.add(message);
                    }
                }
                messageAdapter.notifyDataSetChanged();
                if (!messageList.isEmpty()) {
                    recyclerView.scrollToPosition(messageList.size() - 1); // Scroll to the last message
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error (e.g., show a toast message)
            }
        });
    }

    private boolean isMessageRelevant(Message message) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return (message.getSenderId().equals(userId) && message.getReceiverId().equals(currentUserId)) ||
               (message.getSenderId().equals(currentUserId) && message.getReceiverId().equals(userId));
    }
}
