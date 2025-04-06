package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AgentHomeActivity extends AppCompatActivity {

    private TextView userTextView;
    private Button offresButton, demandesButton, profilButton, deconnexionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_home); // Assuming this is your layout file name

        // Initialize views
        userTextView = findViewById(R.id.userTextView);
        offresButton = findViewById(R.id.offresButton);
        demandesButton = findViewById(R.id.demandesButton);
        profilButton = findViewById(R.id.profilButton);
        deconnexionButton = findViewById(R.id.deconnexionButton);

        // Set user name or information if needed
        userTextView.setText("Welcome, Agent!");  // You can set the username dynamically if needed

        // Set click listeners for each button
        offresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Offers Management activity
                openOffersManagement();
            }
        });

        demandesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Requests Management activity
                openRequestsManagement();
            }
        });

        profilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Profile Management activity
                openProfileManagement();
            }
        });

        deconnexionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform logout action
                logout();
            }
        });
    }

    private void openOffersManagement() {
        // Example of opening a new activity for "Gestion des Offres"
        Intent intent = new Intent(AgentHomeActivity.this, ListeOffresActivity.class);
        startActivity(intent);
    }

    private void openRequestsManagement() {
        // Example of opening a new activity for "Gestion des demandes"
        Intent intent = new Intent(AgentHomeActivity.this, AgentDemandesActivity.class);
        startActivity(intent);
    }

    private void openProfileManagement() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        db.collection("agents").document(currentUserEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Intent intent = new Intent(AgentHomeActivity.this, EditProfileActivity.class);
                        intent.putExtra("name", documentSnapshot.getString("nom"));
                        intent.putExtra("firstName", documentSnapshot.getString("prenom"));
                        intent.putExtra("email", documentSnapshot.getString("email"));
                        intent.putExtra("phone", documentSnapshot.getString("telephone"));
                        intent.putExtra("city", documentSnapshot.getString("ville"));
                        intent.putExtra("country", documentSnapshot.getString("pays"));
                        startActivity(intent);
                    } else {
                        Toast.makeText(AgentHomeActivity.this, "Profil introuvable", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AgentHomeActivity.this, "Erreur lors de la récupération du profil", Toast.LENGTH_SHORT).show();
                });
    }


    private void logout() {
        // Perform logout logic (clear session, navigate to login screen, etc.)
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mAuth.signOut();
        Intent intent = new Intent(AgentHomeActivity.this, AuthentificationActivity.class);
        startActivity(intent);
        finish(); // Close current activity to prevent back navigation to this activity
    }
}
