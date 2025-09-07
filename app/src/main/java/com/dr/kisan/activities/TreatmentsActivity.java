package com.dr.kisan.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dr.kisan.R;
import com.dr.kisan.adapters.TreatmentsAdapter;
import com.dr.kisan.models.TreatmentItem;
import java.util.ArrayList;
import java.util.List;

public class TreatmentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TreatmentsAdapter adapter;
    private List<TreatmentItem> treatmentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatments);

        setupToolbar();
        initializeViews();
        loadDummyTreatments();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Treatments & Pesticides");
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewTreatments);

        // Use GridLayout for better presentation
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void loadDummyTreatments() {
        treatmentsList = new ArrayList<>();

        // Add dummy treatment data
        treatmentsList.add(new TreatmentItem(
                "Copper Oxychloride",
                "Effective fungicide for late blight and bacterial diseases",
                "₹485",
                "Fungicide",
                true,
                "BioCrop Solutions"
        ));

        treatmentsList.add(new TreatmentItem(
                "Mancozeb 75% WP",
                "Broad spectrum fungicide for multiple crop diseases",
                "₹520",
                "Fungicide",
                false,
                "AgroTech Ltd"
        ));

        treatmentsList.add(new TreatmentItem(
                "Neem Oil Extract",
                "Organic pesticide for aphids and whiteflies",
                "₹350",
                "Insecticide",
                true,
                "Green Guard"
        ));

        treatmentsList.add(new TreatmentItem(
                "Imidacloprid 17.8%",
                "Systemic insecticide for sucking pests",
                "₹680",
                "Insecticide",
                false,
                "CropCare Industries"
        ));

        treatmentsList.add(new TreatmentItem(
                "Propiconazole 25%",
                "Systemic fungicide for rust and powdery mildew",
                "₹750",
                "Fungicide",
                false,
                "FarmTech Solutions"
        ));

        treatmentsList.add(new TreatmentItem(
                "Bacillus thuringiensis",
                "Biological insecticide for caterpillars",
                "₹420",
                "Bio-Insecticide",
                true,
                "BioAgri Products"
        ));

        // Setup adapter
        adapter = new TreatmentsAdapter(this, treatmentsList, new TreatmentsAdapter.OnTreatmentClickListener() {
            @Override
            public void onBuyClick(TreatmentItem treatment) {
                Toast.makeText(TreatmentsActivity.this,
                        "Demo: " + treatment.getName() + " - Buying feature coming soon!",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemClick(TreatmentItem treatment) {
                showTreatmentDetails(treatment);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void showTreatmentDetails(TreatmentItem treatment) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(treatment.getName())
                .setMessage("Description: " + treatment.getDescription() + "\n\n" +
                        "Price: " + treatment.getPrice() + "\n" +
                        "Category: " + treatment.getCategory() + "\n" +
                        "Manufacturer: " + treatment.getManufacturer() + "\n" +
                        "Organic: " + (treatment.isOrganic() ? "Yes" : "No"))
                .setPositiveButton("Close", null)
                .setNeutralButton("Buy Now", (dialog, which) -> {
                    Toast.makeText(this, "Demo: Redirecting to purchase...", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
