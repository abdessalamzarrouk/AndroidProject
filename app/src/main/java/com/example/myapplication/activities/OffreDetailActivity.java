package com.example.myapplication.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Comment;
import com.example.myapplication.model.Offre;
import com.example.myapplication.utile.CommentAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OffreDetailActivity extends AppCompatActivity {

    private TextView titre, description, loyer, superficie, pieces, sdb;
    private FirebaseFirestore db;

    private EditText etDemandeDescription, etComment;
    private Button btnFaireDemande, btnAddComment;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    private String offreId, agentEmail, clientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offre_detail);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        titre = findViewById(R.id.titre);
        description = findViewById(R.id.description);
        loyer = findViewById(R.id.loyer);
        superficie = findViewById(R.id.superficie);
        pieces = findViewById(R.id.pieces);
        sdb = findViewById(R.id.sdb);
        btnFaireDemande = findViewById(R.id.btnFaireDemande);
        etDemandeDescription = findViewById(R.id.etDemandeDescription);
        etComment = findViewById(R.id.etComment);
        btnAddComment = findViewById(R.id.btnSubmitComment);
        recyclerViewComments = findViewById(R.id.recyclerViewComments);

        recyclerViewComments.setLayoutManager(new LinearLayoutManager(this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(commentList);
        recyclerViewComments.setAdapter(commentAdapter);

        clientEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        agentEmail = getIntent().getStringExtra("agentEmail");
        offreId = getIntent().getStringExtra("offreId");

        if (agentEmail == null || offreId == null) {
            Log.e("OffreDetailActivity", "Agent Email or Offer ID is missing");
            return;
        }

        btnFaireDemande.setOnClickListener(v -> makeDemand());
        btnAddComment.setOnClickListener(v -> addComment());

        fetchOfferDetails();
        loadComments(offreId);
    }

    private void fetchOfferDetails() {
        db.collection("agents").whereEqualTo("email", agentEmail)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot agentDoc = task.getResult().getDocuments().get(0);
                        db.collection("agents").document(agentDoc.getId()).collection("offres")
                                .document(offreId).get().addOnCompleteListener(offerTask -> {
                                    if (offerTask.isSuccessful() && offerTask.getResult().exists()) {
                                        Offre offre = offerTask.getResult().toObject(Offre.class);
                                        if (offre != null) {
                                            titre.setText(offre.getTitre());
                                            description.setText(offre.getDescription());
                                            loyer.setText("Loyer: " + offre.getLoyer());
                                            superficie.setText("Superficie: " + offre.getSuperficie() + " m²");
                                            pieces.setText("Pièces: " + offre.getPieces());
                                            sdb.setText("Salle de bains: " + offre.getSdb());
                                        }
                                    }
                                });
                    }
                });
    }

    private void makeDemand() {
        if (clientEmail == null) {
            Toast.makeText(this, "Vous devez être connecté pour faire une demande", Toast.LENGTH_SHORT).show();
            return;
        }

        String demandeDetails = etDemandeDescription.getText().toString().trim();
        if (demandeDetails.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer une description pour votre demande", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> demande = new HashMap<>();
        demande.put("offreId", offreId);
        demande.put("agentEmail", agentEmail);
        demande.put("clientEmail", clientEmail);
        demande.put("details", demandeDetails);
        demande.put("timestamp", System.currentTimeMillis());

        db.collection("demandes").add(demande)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Demande envoyée avec succès", Toast.LENGTH_SHORT).show();
                    etDemandeDescription.setText("");
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur lors de l'envoi de la demande", Toast.LENGTH_SHORT).show());
    }

    private void addComment() {
        String commentText = etComment.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Veuillez entrer un commentaire", Toast.LENGTH_SHORT).show();
            return;
        }

        Comment comment = new Comment(clientEmail, commentText, offreId, System.currentTimeMillis());
        db.collection("comments").add(comment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Commentaire ajouté", Toast.LENGTH_SHORT).show();
                    etComment.setText("");
                    loadComments(offreId);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur lors de l'ajout du commentaire", Toast.LENGTH_SHORT).show());
    }

    private void loadComments(String offerId) {
        db.collection("comments")
                .whereEqualTo("offerId", offerId) // Get only comments related to this offer
                .orderBy("timestamp") // Order by latest first
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Error loading comments", e);
                        return;
                    }

                    if (querySnapshot != null) {
                        commentList.clear(); // Clear old comments
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            Comment comment = doc.toObject(Comment.class);
                            if (comment != null) {
                                commentList.add(comment);
                                Log.d("Firestore", "Comment Loaded: " + comment.getCommentText());
                            }
                        }
                        commentAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                        Log.d("Firestore", "Total Comments Loaded: " + commentList.size());
                    } else {
                        Log.d("Firestore", "No comments found");
                    }
                });
    }

}