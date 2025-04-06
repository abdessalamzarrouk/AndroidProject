package com.example.myapplication.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ModifierOffreActivity extends AppCompatActivity {

    private TextView titreTextView, descriptionTextView;
    private EditText superficieEditText, piecesEditText, sdbEditText, loyerEditText;
    private Button saveButton, removeButton;
    private FirebaseFirestore db;
    private String offreId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_offre);

        // Initialize UI components
        titreTextView = findViewById(R.id.titre_text_view);
        descriptionTextView = findViewById(R.id.description_text_view);
        superficieEditText = findViewById(R.id.superficie_edit_text);
        piecesEditText = findViewById(R.id.pieces_edit_text);
        sdbEditText = findViewById(R.id.sdb_edit_text);
        loyerEditText = findViewById(R.id.loyer_edit_text);
        saveButton = findViewById(R.id.save_button);
        removeButton = findViewById(R.id.remove_button);

        db = FirebaseFirestore.getInstance();

        // Get the offerId passed from the previous activity
        offreId = getIntent().getStringExtra("offreId");

        // Fetch the offer details from Firestore
        fetchOfferDetails();

        // Save button click listener
        saveButton.setOnClickListener(v -> {
            saveOfferDetails();
        });

        // Remove button click listener
        removeButton.setOnClickListener(v -> {
            removeOffer();
        });
    }

    private void fetchOfferDetails() {
        db.collection("agents")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail()) // Use the email of the logged-in user
                .collection("offres")
                .document(offreId)  // Use the offer ID to fetch the specific offer
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Fill the fields with the offer details
                        titreTextView.setText(documentSnapshot.getString("titre"));
                        descriptionTextView.setText(documentSnapshot.getString("description"));
                        superficieEditText.setText(String.valueOf(documentSnapshot.getDouble("superficie")));
                        piecesEditText.setText(String.valueOf(documentSnapshot.getLong("pieces")));
                        sdbEditText.setText(String.valueOf(documentSnapshot.getLong("sdb")));
                        loyerEditText.setText(String.valueOf(documentSnapshot.getDouble("loyer")));
                    }
                })
                .addOnFailureListener(e -> Log.w("ModifierOffreActivity", "Error getting offer details", e));
    }

    private void saveOfferDetails() {
        // Get the modified offer details from the input fields
        String titre = titreTextView.getText().toString();
        String description = descriptionTextView.getText().toString();
        float superficie = Float.parseFloat(superficieEditText.getText().toString());
        int pieces = Integer.parseInt(piecesEditText.getText().toString());
        int sdb = Integer.parseInt(sdbEditText.getText().toString());
        float loyer = Float.parseFloat(loyerEditText.getText().toString());

        // Create a map with the modified details
        Map<String, Object> updatedOffer = new HashMap<>();
        updatedOffer.put("titre", titre);
        updatedOffer.put("description", description);
        updatedOffer.put("superficie", superficie);
        updatedOffer.put("pieces", pieces);
        updatedOffer.put("sdb", sdb);
        updatedOffer.put("loyer", loyer);

        // Save the updated offer back to Firestore
        db.collection("agents")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("offres")
                .document(offreId)
                .set(updatedOffer)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ModifierOffreActivity", "Offer updated successfully!");
                    Toast.makeText(ModifierOffreActivity.this, "Offer updated!", Toast.LENGTH_SHORT).show();
                    // Return to the offer list activity
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w("ModifierOffreActivity", "Error updating offer", e);
                    Toast.makeText(ModifierOffreActivity.this, "Failed to update offer", Toast.LENGTH_SHORT).show();
                });
    }

    private void removeOffer() {
        // Remove the offer from Firestore
        db.collection("agents")
                .document(FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .collection("offres")
                .document(offreId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("ModifierOffreActivity", "Offer removed successfully!");
                    Toast.makeText(ModifierOffreActivity.this, "Offer removed!", Toast.LENGTH_SHORT).show();
                    // Return to the offer list activity
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w("ModifierOffreActivity", "Error removing offer", e);
                    Toast.makeText(ModifierOffreActivity.this, "Failed to remove offer", Toast.LENGTH_SHORT).show();
                });
    }
}
