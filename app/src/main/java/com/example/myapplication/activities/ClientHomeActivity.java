package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activities.AgentHomeActivity;
import com.example.myapplication.activities.AuthentificationActivity;
import com.example.myapplication.activities.ChercherOffresActivity;
import com.example.myapplication.activities.MesDemandesActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ClientHomeActivity extends AppCompatActivity {

    private Button boutonChercherOffres;
    private Button boutonMesDemandes;
    private Button boutonLogout;

    private Button boutonProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_home);

        // Initialize buttons
        boutonChercherOffres = findViewById(R.id.boutonChercherOffres);
        boutonMesDemandes = findViewById(R.id.boutonMesDemandes);
        boutonLogout = findViewById(R.id.boutonLogout);
        boutonProfile = findViewById(R.id.boutonProfile);

        boutonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                db.collection("clients").document(currentUserEmail)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Intent intent = new Intent(ClientHomeActivity.this, EditProfileActivity.class);
                                intent.putExtra("name", documentSnapshot.getString("nom"));
                                intent.putExtra("firstName", documentSnapshot.getString("prenom"));
                                intent.putExtra("email", documentSnapshot.getString("email"));
                                intent.putExtra("phone", documentSnapshot.getString("telephone"));
                                intent.putExtra("city", documentSnapshot.getString("ville"));
                                intent.putExtra("country", documentSnapshot.getString("pays"));
                                startActivity(intent);
                            } else {
                                Toast.makeText(ClientHomeActivity.this, "Profil introuvable", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(ClientHomeActivity.this, "Erreur lors de la récupération du profil", Toast.LENGTH_SHORT).show();
                        });
            }
        });




        // Button click listeners
        boutonChercherOffres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the activity that handles offers search
                startActivity(new Intent(ClientHomeActivity.this, ChercherOffresActivity.class));
            }
        });

        boutonMesDemandes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the activity that shows the client's demands
                startActivity(new Intent(ClientHomeActivity.this, ClientDemandesActivity.class));
            }
        });

        boutonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                mAuth.signOut();
                Intent intent = new Intent(ClientHomeActivity.this, AuthentificationActivity.class);
                startActivity(intent);
                finish(); // Close current activity to prevent back navigation to this activity
            }
        });
    }
}
