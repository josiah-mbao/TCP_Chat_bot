package com.example.tcp_chatbot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(MainActivity.this, AuthActivity.class));
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerView);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this::onUserClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        loadUsers();
    }

    private void loadUsers() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userId = snapshot.getKey();
                    String name = snapshot.child("name").getValue(String.class);
                    userList.add(new User(userId, name));
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onUserClick(User user) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("userId", user.getId());
        intent.putExtra("userName", user.getName());
        startActivity(intent);
    }
}
