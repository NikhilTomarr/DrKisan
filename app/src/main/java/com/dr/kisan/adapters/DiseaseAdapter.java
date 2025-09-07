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
import com.dr.kisan.models.Treatment;
import java.util.List;

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.DiseaseViewHolder> {
    private Context context;
    private List<Treatment> treatments;
    private OnTreatmentClickListener onTreatmentClickListener;

    public interface OnTreatmentClickListener {
        void onTreatmentClick(Treatment treatment);
    }

    public DiseaseAdapter(Context context, List<Treatment> treatments) {
        this.context = context;
        this.treatments = treatments;
    }

    public void setOnTreatmentClickListener(OnTreatmentClickListener listener) {
        this.onTreatmentClickListener = listener;
    }

    @NonNull
    @Override
    public DiseaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_treatment, parent, false);
        return new DiseaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiseaseViewHolder holder, int position) {
        Treatment treatment = treatments.get(position);

        holder.textPesticide.setText(treatment.getPesticide());
        holder.textDosage.setText("Dosage: " + treatment.getDosage());
        holder.textMethod.setText("Method: " + treatment.getApplicationMethod());
        holder.textPrecautions.setText(treatment.getPrecautions());

        // Set organic indicator
        if (treatment.isOrganic()) {
            holder.imageOrganic.setVisibility(View.VISIBLE);
            holder.textOrganic.setVisibility(View.VISIBLE);
        } else {
            holder.imageOrganic.setVisibility(View.GONE);
            holder.textOrganic.setVisibility(View.GONE);
        }

        holder.cardTreatment.setOnClickListener(v -> {
            if (onTreatmentClickListener != null) {
                onTreatmentClickListener.onTreatmentClick(treatment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return treatments.size();
    }

    static class DiseaseViewHolder extends RecyclerView.ViewHolder {
        CardView cardTreatment;
        TextView textPesticide;
        TextView textDosage;
        TextView textMethod;
        TextView textPrecautions;
        TextView textOrganic;
        ImageView imageOrganic;

        DiseaseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTreatment = itemView.findViewById(R.id.cardTreatment);
            textPesticide = itemView.findViewById(R.id.textPesticide);
            textDosage = itemView.findViewById(R.id.textDosage);
            textMethod = itemView.findViewById(R.id.textMethod);
            textPrecautions = itemView.findViewById(R.id.textPrecautions);
            textOrganic = itemView.findViewById(R.id.textOrganic);
            imageOrganic = itemView.findViewById(R.id.imageOrganic);
        }
    }
}
