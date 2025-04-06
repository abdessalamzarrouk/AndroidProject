package com.example.myapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AjouterOffreActivity extends AppCompatActivity {

    private EditText offreTitre, offreDescription, offreSuperficie, offrePieces, offreSdb, offreLoyer;
    private Button btnSaveOffre;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_offre);

        // Initialize views
        offreTitre = findViewById(R.id.offre_titre);
        offreDescription = findViewById(R.id.offre_description);
        offreSuperficie = findViewById(R.id.offre_superficie);
        offrePieces = findViewById(R.id.offre_pieces);
        offreSdb = findViewById(R.id.offre_sdb);
        offreLoyer = findViewById(R.id.offre_loyer);
        btnSaveOffre = findViewById(R.id.btn_save_offre);

        db = FirebaseFirestore.getInstance();

        // Set click listener for the Save button
        btnSaveOffre.setOnClickListener(view -> saveOffer());
    }

    private void saveOffer() {
        // Get the input data
        String titre = offreTitre.getText().toString().trim();
        String description = offreDescription.getText().toString().trim();
        String superficieStr = offreSuperficie.getText().toString().trim();
        String piecesStr = offrePieces.getText().toString().trim();
        String sdbStr = offreSdb.getText().toString().trim();
        String loyerStr = offreLoyer.getText().toString().trim();

        // Validate input
        if (titre.isEmpty() || description.isEmpty() || superficieStr.isEmpty() ||
                piecesStr.isEmpty() || sdbStr.isEmpty() || loyerStr.isEmpty()) {
            Toast.makeText(this, "Tous les champs sont obligatoires", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert inputs to correct types
        float superficie = Float.parseFloat(superficieStr);
        int pieces = Integer.parseInt(piecesStr);
        int sdb = Integer.parseInt(sdbStr);
        float loyer = Float.parseFloat(loyerStr);

        // Create a new offer map to save in Firestore
        Map<String, Object> offerData = new HashMap<>();
        offerData.put("titre", titre);
        offerData.put("description", description);
        offerData.put("superficie", superficie);
        offerData.put("pieces", pieces);
        offerData.put("sdb", sdb);
        offerData.put("loyer", loyer);

        // Get the current user's email (assuming the user is logged in)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();

            // Add the offer to Firestore under the user's "offres" subcollection
            db.collection("agents")
                    .document(email)
                    .collection("offres")
                    .add(offerData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(AjouterOffreActivity.this, "Offre ajoutée avec succès", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after saving
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AjouterOffreActivity.this, "Erreur lors de l'ajout de l'offre", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
