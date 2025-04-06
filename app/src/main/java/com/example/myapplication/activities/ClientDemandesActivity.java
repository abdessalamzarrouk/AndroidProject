package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Demande;
import com.example.myapplication.utile.DemandeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class ClientDemandesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DemandeUtil adapter;
    private List<Demande> demandesList;
    private FirebaseFirestore db;
    private String clientEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_demandes);

        recyclerView = findViewById(R.id.recyclerViewClientDemandes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        demandesList = new ArrayList<>();
        adapter = new DemandeUtil(this, demandesList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        clientEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        fetchClientDemandes();
    }

    private void fetchClientDemandes() {
        db.collection("demandes")
                .whereEqualTo("clientEmail", clientEmail)  // Get only the user's demandes
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    demandesList.clear();
                    for (var document : queryDocumentSnapshots.getDocuments()) {
                        Demande demande = document.toObject(Demande.class);
                        demandesList.add(demande);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(ClientDemandesActivity.this, "Erreur de récupération des demandes", Toast.LENGTH_SHORT).show());
    }
}
