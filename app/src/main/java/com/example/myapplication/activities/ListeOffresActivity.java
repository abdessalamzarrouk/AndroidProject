package com.example.myapplication.activities;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.utile.MyAdapter;
import com.example.myapplication.model.Offre;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.LinkedList;

public class ListeOffresActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FirebaseFirestore db;
    LinkedList<Offre> offres;
    RecyclerView myRecycler;
    boolean terminated;
    ProgressDialog progdiag;
    FloatingActionButton ajouterOffre;
    private AdapterView.OnItemClickListener onItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_offres);

        myRecycler = findViewById(R.id.listeOffres);
        ajouterOffre = findViewById(R.id.ajouterOffre);

        // Adding padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Floating button to add new offers
        ajouterOffre.setOnClickListener(v -> {
            Intent intent = new Intent(ListeOffresActivity.this, AjouterOffreActivity.class);
            startActivity(intent);
        });
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    @Override
    public void onStart() {
        super.onStart();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        offres = new LinkedList<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        getOffres(currentUser);
    }

    // Fetch offers from the agent's subcollection
    @SuppressLint("StaticFieldLeak")
    void getOffres(FirebaseUser currentUser) {
        showDialog();

        db.collection("agents")
                .document(currentUser.getEmail())
                .collection("offres")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        offres.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());

                            Offre offre = new Offre(
                                    document.getId(),  // Set the offer ID
                                    document.getString("titre"),
                                    document.getString("description"),
                                    document.get("superficie", Float.class),
                                    document.get("pieces", Integer.class),
                                    document.get("sdb", Integer.class),
                                    document.get("loyer", Float.class)
                            );
                            offres.add(offre);
                        }

                        myRecycler.setHasFixedSize(true);
                        myRecycler.setLayoutManager(new LinearLayoutManager(ListeOffresActivity.this));

                        // Create the adapter and set it to the RecyclerView
                        MyAdapter myAdapter = new MyAdapter(offres, ListeOffresActivity.this);

                        // Set the adapter directly to RecyclerView
                        myRecycler.setAdapter(myAdapter);

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                    hideDialog();
                });
    }



    // Show progress dialog
    void showDialog() {
        progdiag = new ProgressDialog(this);
        progdiag.setMessage("Veuillez patienter, les donnees sont en cours de chargement ... ");
        progdiag.setIndeterminate(true);
        progdiag.show();
    }

    // Hide progress dialog
    void hideDialog() {
        if (progdiag != null && progdiag.isShowing()) {
            progdiag.dismiss();
        }
    }
}


