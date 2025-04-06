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
import com.example.myapplication.activities.ModifierOffreActivity;
import com.example.myapplication.model.Offre;

import java.util.LinkedList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private LinkedList<Offre> offres;
    private Context context;

    public MyAdapter(LinkedList<Offre> offres, Context context) {
        this.offres = offres;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.offre_item_layout, parent, false);
        return new MyViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Offre offre = offres.get(position);
        holder.titre.setText(offre.getTitre());  // Set the offer title

        // Set the onClickListener for the offerButton to navigate to ModifierOffreActivity
        holder.offerButton.setOnClickListener(v -> {
            // Start the ModifierOffreActivity and pass the offerId for modification
            Intent intent = new Intent(context, ModifierOffreActivity.class);
            intent.putExtra("offreId", offre.getId());  // Pass the offerId to modify the specific offer
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return offres.size();
    }


    // ViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titre;
        public Button offerButton;

        public MyViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            titre = itemLayoutView.findViewById(R.id.offre_titre);
            offerButton = itemLayoutView.findViewById(R.id.offre_button);  // Find the offerButton
        }
    }
}
