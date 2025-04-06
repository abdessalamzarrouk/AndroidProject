package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.TextView;
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

public class AgentDemandesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DemandeUtil adapter;
    private List<Demande> demandesList;
    private FirebaseFirestore db;
    private String agentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_demandes);

        recyclerView = findViewById(R.id.recyclerViewDemandes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        demandesList = new ArrayList<>();
        adapter = new DemandeUtil(this, demandesList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        agentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        fetchDemandes();
    }

    private void fetchDemandes() {
        db.collection("demandes")
                .whereEqualTo("agentEmail", agentEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    demandesList.clear();
                    for (var document : queryDocumentSnapshots.getDocuments()) {
                        // Fetch the Demande object and set its id to the Firestore document ID
                        Demande demande = document.toObject(Demande.class);
                        demande.setId(document.getId());  // Set the document ID here
                        demandesList.add(demande);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(AgentDemandesActivity.this, "Erreur de récupération des demandes", Toast.LENGTH_SHORT).show());
    }

}

