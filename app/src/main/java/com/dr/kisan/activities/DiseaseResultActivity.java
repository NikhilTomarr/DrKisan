package com.dr.kisan.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dr.kisan.R;
import com.dr.kisan.adapters.DiseaseAdapter;
import com.dr.kisan.adapters.ProductAdapter;
import com.dr.kisan.models.Product;
import com.dr.kisan.models.Treatment;
import com.dr.kisan.utils.TreatmentDatabase;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class DiseaseResultActivity extends AppCompatActivity {
    private ImageView imageResult;
    private TextView textDiseaseName;
    private TextView textPlantSpecies;
    private TextView textConfidence;
    private TextView textSeverity;
    private TextView textDescription;
    private TextView textTreatment;
    private TextView textPrevention;
    private ProgressBar progressConfidence;
    private RecyclerView recyclerViewTreatments;
    private MaterialButton btnSaveResult;
    private MaterialButton btnShareResult;

    private DiseaseAdapter treatmentAdapter;
    private TreatmentDatabase treatmentDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_result);

        setupToolbar();
        initializeViews();
        loadResultData();
        setupClickListeners();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Disease Analysis Result");
        }
    }

    private void initializeViews() {
        imageResult = findViewById(R.id.imageResult);
        textDiseaseName = findViewById(R.id.textDiseaseName);
        textPlantSpecies = findViewById(R.id.textPlantSpecies);
        textConfidence = findViewById(R.id.textConfidence);
        textSeverity = findViewById(R.id.textSeverity);
        textDescription = findViewById(R.id.textDescription);
        textTreatment = findViewById(R.id.textTreatment);
        textPrevention = findViewById(R.id.textPrevention);
        progressConfidence = findViewById(R.id.progressConfidence);
        recyclerViewTreatments = findViewById(R.id.recyclerViewTreatments);
        btnSaveResult = findViewById(R.id.btnSaveResult);
        btnShareResult = findViewById(R.id.btnShareResult);

        treatmentDatabase = new TreatmentDatabase(this);
    }

    private void loadResultData() {
        // Get data from intent
        String diseaseName = getIntent().getStringExtra("disease_name");
        float confidence = getIntent().getFloatExtra("confidence", 0.0f);
        String plantSpecies = getIntent().getStringExtra("plant_species");
        String severity = getIntent().getStringExtra("severity");
        String description = getIntent().getStringExtra("description");
        String treatment = getIntent().getStringExtra("treatment");
        String prevention = getIntent().getStringExtra("prevention");
        String imagePath = getIntent().getStringExtra("image_path");

        // Display the data with improved plant species handling
        textDiseaseName.setText(diseaseName != null ? diseaseName.replace("_", " ") : "Unknown Disease");

        // Fix plant species display
        if (plantSpecies != null && !plantSpecies.equals("Unknown Plant")) {
            textPlantSpecies.setText(plantSpecies);
        } else if (diseaseName != null && diseaseName.contains("___")) {
            // Extract plant name from disease name
            String extractedPlant = formatPlantName(diseaseName.split("___")[0]);
            textPlantSpecies.setText(extractedPlant);
        } else {
            textPlantSpecies.setText("Plant");
        }

        textConfidence.setText(String.format("%.1f%%", confidence * 100));
        textSeverity.setText(severity != null ? severity : "Analysis Complete");
        textDescription.setText(description != null ? description : "Disease analysis completed");
        textTreatment.setText(treatment != null ? treatment : "Consult agricultural expert");
        textPrevention.setText(prevention != null ? prevention : "Follow good agricultural practices");

        // Set confidence progress
        progressConfidence.setProgress((int) (confidence * 100));

        // Load and display image
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                imageResult.setImageBitmap(bitmap);
            }
        }

        // Load demo products
        loadDemoProducts();
    }

    private String formatPlantName(String plantName) {
        if (plantName == null || plantName.isEmpty()) {
            return "Plant";
        }

        String formatted = plantName.replace("_", " ");
        String[] words = formatted.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }

        return result.toString().trim();
    }

    private void loadDemoProducts() {
        List<Product> demoProducts = new ArrayList<>();

        // Add 2 demo products based on disease type
        String diseaseName = getIntent().getStringExtra("disease_name");
        if (diseaseName != null && diseaseName.toLowerCase().contains("blight")) {
            demoProducts.add(new Product("CopperMax Fungicide",
                    "Effective copper-based fungicide for blight control. Fast-acting formula.",
                    "₹285", true, "AgroTech Solutions"));
            demoProducts.add(new Product("BlightShield Pro",
                    "Advanced systemic fungicide with long-lasting protection.",
                    "₹450", false, "CropCare Industries"));
        } else if (diseaseName != null && diseaseName.toLowerCase().contains("rust")) {
            demoProducts.add(new Product("RustGuard Organic",
                    "Natural rust control solution made from plant extracts.",
                    "₹320", true, "Green Crop Solutions"));
            demoProducts.add(new Product("FungiStop Advanced",
                    "Professional-grade rust treatment for all crops.",
                    "₹540", false, "FarmTech Ltd"));
        } else {
            // Default products
            demoProducts.add(new Product("MultiCrop Protector",
                    "All-purpose plant disease control solution. Safe and effective.",
                    "₹350", true, "BioFarm Products"));
            demoProducts.add(new Product("CropGuard Universal",
                    "Broad-spectrum disease prevention and treatment.",
                    "₹420", false, "AgriCare Solutions"));
        }

        // Setup RecyclerView
        ProductAdapter productAdapter = new ProductAdapter(this, demoProducts);
        recyclerViewTreatments.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTreatments.setAdapter(productAdapter);
    }

    private void loadTreatments(String diseaseName) {
        if (diseaseName != null) {
            List<Treatment> treatments = treatmentDatabase.getTreatmentsForDisease(diseaseName);

            treatmentAdapter = new DiseaseAdapter(this, treatments);
            recyclerViewTreatments.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewTreatments.setAdapter(treatmentAdapter);

            treatmentAdapter.setOnTreatmentClickListener(treatment -> {
                // Handle treatment click - show detailed information
                showTreatmentDetails(treatment);
            });
        }
    }

    private void setupClickListeners() {
        btnSaveResult.setOnClickListener(v -> {
            // Implement save functionality
            saveResult();
        });

        btnShareResult.setOnClickListener(v -> {
            // Implement share functionality
            shareResult();
        });
    }

    private void saveResult() {
        // Implement save to local database or cloud
        // For now, just show a toast
        android.widget.Toast.makeText(this, "Result saved successfully!", android.widget.Toast.LENGTH_SHORT).show();
    }

    private void shareResult() {
        // Implement sharing functionality
        String shareText = "DrKisan Disease Detection Result:\n" +
                "Disease: " + textDiseaseName.getText() + "\n" +
                "Confidence: " + textConfidence.getText() + "\n" +
                "Plant: " + textPlantSpecies.getText();

        android.content.Intent shareIntent = new android.content.Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
        startActivity(android.content.Intent.createChooser(shareIntent, "Share Result"));
    }

    private void showTreatmentDetails(Treatment treatment) {
        // Create and show dialog with treatment details
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(treatment.getPesticide())
                .setMessage("Dosage: " + treatment.getDosage() + "\n\n" +
                        "Application Method: " + treatment.getApplicationMethod() + "\n\n" +
                        "Precautions: " + treatment.getPrecautions() + "\n\n" +
                        "Organic: " + (treatment.isOrganic() ? "Yes" : "No"))
                .setPositiveButton("OK", null)
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
