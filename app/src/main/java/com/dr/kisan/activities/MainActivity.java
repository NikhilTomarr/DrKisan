package com.dr.kisan.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dr.kisan.R;
import com.dr.kisan.adapters.RecentActivityAdapter;
import com.dr.kisan.models.RecentActivity;
import com.dr.kisan.utils.ModelValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CardView cardDiseaseDetection;
    private CardView cardTreatments;
    private CardView cardCommunity;
    private CardView cardProducts;
    private RecyclerView recyclerViewRecentActivity;
    private RecentActivityAdapter recentActivityAdapter;
    private List<RecentActivity> recentActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        setupToolbar();
        initializeViews();
        setupClickListeners();
        setupRecentActivity();
        validateModelFiles();
    }


    private void validateModelFiles() {
        Log.d("MainActivity", "Starting model validation...");

        // List all assets for debugging
        try {
            String[] assets = getAssets().list("");
            Log.d("MainActivity", "All assets: " + Arrays.toString(assets));

            // Check specific files
            boolean modelExists = Arrays.asList(assets).contains("disease_model.tflite");
            boolean labelsExist = Arrays.asList(assets).contains("labels.txt");

            Log.d("MainActivity", "Model exists: " + modelExists);
            Log.d("MainActivity", "Labels exist: " + labelsExist);

        } catch (IOException e) {
            Log.e("MainActivity", "Error listing assets", e);
        }

        if (!ModelValidator.validateModelFiles(this)) {
            showModelErrorDialog();
        } else {
            Log.d("MainActivity", "Model files validated successfully");
            Toast.makeText(this, "Disease detection model ready", Toast.LENGTH_SHORT).show();
        }
    }

    private void showModelErrorDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Model Setup Required")
                .setMessage("To use disease detection:\n\n" +
                        "1. Create assets folder: app/src/main/assets/\n" +
                        "2. Place 'disease_model.tflite' in assets folder\n" +
                        "3. Place 'labels.txt' in assets folder\n" +
                        "4. Clean and rebuild project\n\n" +
                        "Current status: Model files missing or invalid")
                .setPositiveButton("OK", null)
                .setNegativeButton("Check LogCat", (dialog, which) -> {
                    Toast.makeText(this, "Check Android Studio LogCat for detailed info", Toast.LENGTH_LONG).show();
                })
                .show();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("DrKisan");
        }
    }

    private void initializeViews() {
        cardDiseaseDetection = findViewById(R.id.cardDiseaseDetection);
        cardTreatments = findViewById(R.id.cardTreatments);
        cardCommunity = findViewById(R.id.cardCommunity);
        cardProducts = findViewById(R.id.cardProducts);
        recyclerViewRecentActivity = findViewById(R.id.recyclerViewRecentActivity);
    }

    private void setupClickListeners() {
        cardDiseaseDetection.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        });

        cardTreatments.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TreatmentsActivity.class);
            startActivity(intent);
        });

        cardCommunity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
            startActivity(intent);
        });

        cardProducts.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductsActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecentActivity() {
        recentActivities = new ArrayList<>();

        // Add sample recent activities
        recentActivities.add(new RecentActivity("Disease Detection", "Tomato Late Blight detected", System.currentTimeMillis() - 3600000, R.drawable.ic_camera));
        recentActivities.add(new RecentActivity("Community Post", "New question about potato farming", System.currentTimeMillis() - 7200000, R.drawable.ic_community));
        recentActivities.add(new RecentActivity("Treatment Applied", "Copper sulfate treatment recommended", System.currentTimeMillis() - 10800000, R.drawable.ic_treatment));

        recentActivityAdapter = new RecentActivityAdapter(this, recentActivities);
        recyclerViewRecentActivity.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecentActivity.setAdapter(recentActivityAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
