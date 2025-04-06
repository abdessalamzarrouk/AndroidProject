package com.example.myapplication.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Offre;
import com.example.myapplication.utile.OffreUtil;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.CollectionReference;
import java.util.ArrayList;
import java.util.List;

public class ChercherOffresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OffreUtil offreUtil;  // The custom adapter for offers
    private FirebaseFirestore db;
    private List<Offre> allOffres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chercher_offres);

        recyclerView = findViewById(R.id.recyclerViewOffres);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        allOffres = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Instantiate the adapter
        offreUtil = new OffreUtil(this, allOffres);
        recyclerView.setAdapter(offreUtil);

        // Fetch offers for all agents
        fetchAllOffers();
    }

    private void fetchAllOffers() {
        CollectionReference agentsRef = db.collection("agents");
        agentsRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot agentDoc : querySnapshot) {
                                // Fetch offers from each agent's "offres" subcollection
                                CollectionReference offersRef = agentDoc.getReference().collection("offres");
                                fetchOffersForAgent(offersRef, agentDoc.getString("email"));  // Pass email
                            }
                        }
                    }
                });
    }

    private void fetchOffersForAgent(CollectionReference offersRef, String agentEmail) {
        offersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot offerDoc : querySnapshot) {
                                // The offer ID is the Firestore document ID
                                String offerId = offerDoc.getId();

                                // Create an offer object and set the agent's email and offer ID
                                Offre offre = offerDoc.toObject(Offre.class);
                                if (offre != null) {
                                    offre.setAgentEmail(agentEmail); // Add agentEmail to the offer
                                    offre.setId(offerId); // Add the Firestore document ID as offer ID
                                    allOffres.add(offre);
                                }
                            }
                            // Notify the adapter that new data is available
                            offreUtil.notifyDataSetChanged();
                        }
                    }
                });
    }

}


