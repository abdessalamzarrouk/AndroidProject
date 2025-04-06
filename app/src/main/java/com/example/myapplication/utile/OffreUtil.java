package com.example.myapplication.utile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.activities.OffreDetailActivity;
import com.example.myapplication.model.Offre;
import java.util.List;

public class OffreUtil extends RecyclerView.Adapter<OffreUtil.OffreViewHolder> {

    private Context context;
    private List<Offre> offres;

    public OffreUtil(Context context, List<Offre> offres) {
        this.context = context;
        this.offres = offres;
    }

    @Override
    public OffreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offre, parent, false);
        return new OffreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OffreViewHolder holder, int position) {
        Offre offre = offres.get(position);
        holder.titreOffre.setText(offre.getTitre());
        holder.descriptionOffre.setText(offre.getDescription());
        holder.loyerOffre.setText("Loyer: " + offre.getLoyer());

        // Set click listener for the "Inspect" button
        holder.btnInspect.setOnClickListener(v -> {
            // Pass the selected offer details to the next activity
            Intent intent = new Intent(context, OffreDetailActivity.class);
            intent.putExtra("agentEmail", offre.getAgentEmail()); // Pass agent email
            intent.putExtra("offreId", offre.getId()); // Pass offer ID
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return offres.size();
    }

    public class OffreViewHolder extends RecyclerView.ViewHolder {
        TextView titreOffre, descriptionOffre, loyerOffre;
        Button btnInspect;

        public OffreViewHolder(View itemView) {
            super(itemView);
            titreOffre = itemView.findViewById(R.id.titreOffre);
            descriptionOffre = itemView.findViewById(R.id.descriptionOffre);
            loyerOffre = itemView.findViewById(R.id.loyerOffre);
            btnInspect = itemView.findViewById(R.id.btnInspect);
        }
    }
}
