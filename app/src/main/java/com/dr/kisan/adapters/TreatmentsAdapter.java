package com.dr.kisan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.dr.kisan.R;
import com.dr.kisan.models.TreatmentItem;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class TreatmentsAdapter extends RecyclerView.Adapter<TreatmentsAdapter.TreatmentViewHolder> {
    private Context context;
    private List<TreatmentItem> treatments;
    private OnTreatmentClickListener listener;

    public interface OnTreatmentClickListener {
        void onBuyClick(TreatmentItem treatment);
        void onItemClick(TreatmentItem treatment);
    }

    public TreatmentsAdapter(Context context, List<TreatmentItem> treatments, OnTreatmentClickListener listener) {
        this.context = context;
        this.treatments = treatments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TreatmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_treatment_card, parent, false);
        return new TreatmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TreatmentViewHolder holder, int position) {
        TreatmentItem treatment = treatments.get(position);

        holder.textName.setText(treatment.getName());
        holder.textDescription.setText(treatment.getDescription());
        holder.textPrice.setText(treatment.getPrice());
        holder.textCategory.setText(treatment.getCategory());
        holder.textManufacturer.setText("By " + treatment.getManufacturer());

        // Show organic badge
        if (treatment.isOrganic()) {
            holder.imageOrganic.setVisibility(View.VISIBLE);
            holder.textOrganic.setVisibility(View.VISIBLE);
        } else {
            holder.imageOrganic.setVisibility(View.GONE);
            holder.textOrganic.setVisibility(View.GONE);
        }

        // Click listeners
        holder.cardTreatment.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(treatment);
            }
        });

        holder.btnBuy.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBuyClick(treatment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return treatments.size();
    }

    static class TreatmentViewHolder extends RecyclerView.ViewHolder {
        CardView cardTreatment;
        TextView textName;
        TextView textDescription;
        TextView textPrice;
        TextView textCategory;
        TextView textManufacturer;
        TextView textOrganic;
        ImageView imageOrganic;
        MaterialButton btnBuy;

        TreatmentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTreatment = itemView.findViewById(R.id.cardTreatment);
            textName = itemView.findViewById(R.id.textName);
            textDescription = itemView.findViewById(R.id.textDescription);
            textPrice = itemView.findViewById(R.id.textPrice);
            textCategory = itemView.findViewById(R.id.textCategory);
            textManufacturer = itemView.findViewById(R.id.textManufacturer);
            textOrganic = itemView.findViewById(R.id.textOrganic);
            imageOrganic = itemView.findViewById(R.id.imageOrganic);
            btnBuy = itemView.findViewById(R.id.btnBuy);
        }
    }
}
