package com.example.tcp_chatbot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AuthActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Button signInButton;
    private Button signOutButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signInButton = findViewById(R.id.signInButton);
        signOutButton = findViewById(R.id.signOutButton);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        checkUser();
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(AuthActivity.this, MainActivity.class));
            finish();
        }
    }

    private void signIn() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // User signed in successfully, save name to database
                        String userId = firebaseAuth.getCurrentUser().getUid();
                        databaseReference.child(userId).child("name").setValue(name);
                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(AuthActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signOut() {
        firebaseAuth.signOut();
        signOutButton.setVisibility(View.GONE);
        signInButton.setVisibility(View.VISIBLE);
    }
}
