package com.example.myapplication.utile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.model.Demande;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DemandeUtil extends RecyclerView.Adapter<DemandeUtil.DemandeViewHolder> {

    private Context context;
    private List<Demande> demandes;
    private String currentUserEmail;

    public DemandeUtil(Context context, List<Demande> demandes) {
        this.context = context;
        this.demandes = demandes;
        this.currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail(); // Get current user email
    }

    @Override
    public DemandeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_demande, parent, false);
        return new DemandeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DemandeViewHolder holder, int position) {
        Demande demande = demandes.get(position);

        // Set Demande details
        holder.tvOffreId.setText("Offre ID: " + demande.getOffreId());
        holder.tvAgentEmail.setText("Agent: " + demande.getAgentEmail());
        holder.tvDetails.setText("Détails: " + demande.getDetails());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        holder.tvTimestamp.setText("Date: " + sdf.format(demande.getTimestamp()));

        // Show status
        holder.tvStatus.setText("Status: " + demande.getStatus());

        // Conditionally show Accept/Deny buttons if the current user is the agent
        if (currentUserEmail != null && currentUserEmail.equals(demande.getAgentEmail())) {
            holder.btnAccept.setVisibility(View.VISIBLE); // Show Accept button if the current user is the agent
            holder.btnDeny.setVisibility(View.VISIBLE);   // Show Deny button if the current user is the agent

            // Set listeners for Accept/Deny buttons
            holder.btnAccept.setOnClickListener(v -> updateDemandeStatus(demande, "Accepted"));
            holder.btnDeny.setOnClickListener(v -> updateDemandeStatus(demande, "Denied"));
        } else {
            holder.btnAccept.setVisibility(View.GONE); // Hide buttons for the client
            holder.btnDeny.setVisibility(View.GONE);    // Hide buttons for the client
        }
    }

    @Override
    public int getItemCount() {
        return demandes.size();
    }

    // Update the status of the demande in Firestore
    private void updateDemandeStatus(Demande demande, String newStatus) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("demandes").document(demande.getId())  // Use the document ID
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    // Update successful
                    Toast.makeText(context, "Demande status updated to " + newStatus, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Update failed
                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }

    public class DemandeViewHolder extends RecyclerView.ViewHolder {
        TextView tvOffreId, tvAgentEmail, tvDetails, tvTimestamp, tvStatus;
        Button btnAccept, btnDeny;

        public DemandeViewHolder(View itemView) {
            super(itemView);
            tvOffreId = itemView.findViewById(R.id.tvOffreId);
            tvAgentEmail = itemView.findViewById(R.id.tvAgentEmail);
            tvDetails = itemView.findViewById(R.id.tvDemandeDetails);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnDeny = itemView.findViewById(R.id.btnDeny);
        }
    }
}

