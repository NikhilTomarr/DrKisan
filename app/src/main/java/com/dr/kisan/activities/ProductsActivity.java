package com.dr.kisan.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.dr.kisan.R;
import com.dr.kisan.adapters.ProductsAdapter;
import com.dr.kisan.models.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductsAdapter adapter;
    private List<Product> productsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        setupToolbar();
        initializeViews();
        loadDummyProducts();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Farm Products");
        }
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadDummyProducts() {
        productsList = new ArrayList<>();

        // Farming Machines - Added as requested
        productsList.add(new Product(
                "Heavy Duty Tractor",
                "50 HP tractor with advanced hydraulics. Perfect for large scale farming operations.",
                "₹8,50,000",
                false,
                "MahindraAg Solutions"
        ));

        productsList.add(new Product(
                "Combine Harvester",
                "Fully automated combine harvester for wheat, rice and other grains. High efficiency.",
                "₹15,50,000",
                false,
                "John Deere India"
        ));

        productsList.add(new Product(
                "Rotary Tiller Machine",
                "Heavy-duty rotary tiller for soil preparation. Robust build quality.",
                "₹1,85,000",
                false,
                "Fieldking Agrotec"
        ));

        productsList.add(new Product(
                "Multi Crop Seeder",
                "Precision seeding machine for multiple crop varieties. GPS enabled.",
                "₹2,75,000",
                false,
                "Swaraj Mazda"
        ));

        // Fertilizers and Organic Products
        productsList.add(new Product(
                "Premium Organic Fertilizer",
                "Rich NPK blend for healthy crop growth. 100% organic and eco-friendly.",
                "₹850",
                true,
                "EcoGrow Fertilizers"
        ));

        productsList.add(new Product(
                "Bio Compost Enricher",
                "Accelerates composting process. Converts waste to nutrient-rich fertilizer.",
                "₹320",
                true,
                "Green Composters"
        ));

        productsList.add(new Product(
                "Organic Soil Conditioner",
                "Improves soil structure and water retention. 100% natural ingredients.",
                "₹420",
                true,
                "SoilCare Solutions"
        ));

        // Technology and Equipment
        productsList.add(new Product(
                "Advanced Drip Irrigation Kit",
                "Complete irrigation system for water-efficient farming. Easy installation.",
                "₹45,450",
                false,
                "AquaFarm Technologies"
        ));

        productsList.add(new Product(
                "Smart Weather Monitor",
                "Digital weather station for precision farming. Real-time data monitoring.",
                "₹18,500",
                false,
                "FarmTech Instruments"
        ));

        // Seeds
        productsList.add(new Product(
                "Hybrid Tomato Seeds",
                "High-yield disease-resistant tomato variety. Perfect for commercial farming.",
                "₹1,250",
                false,
                "AgriSeeds Corporation"
        ));

        // More Farming Equipment
        productsList.add(new Product(
                "Disc Harrow Machine",
                "Heavy duty disc harrow for field preparation. Durable construction.",
                "₹1,25,000",
                false,
                "Lemken India"
        ));

        productsList.add(new Product(
                "Power Weeder",
                "Efficient weed removal machine. Reduces manual labor significantly.",
                "₹55,000",
                false,
                "VST Tillers"
        ));

        // Setup adapter
        adapter = new ProductsAdapter(this, productsList, new ProductsAdapter.OnProductClickListener() {
            @Override
            public void onBuyClick(Product product) {
                Toast.makeText(ProductsActivity.this,
                        "Demo: " + product.getName() + " - Purchase feature coming soon!\nPrice: " + product.getPrice(),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onItemClick(Product product) {
                showProductDetails(product);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void showProductDetails(Product product) {
        String category = getCategoryFromPrice(product.getPrice());

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(product.getName())
                .setMessage("Description: " + product.getDescription() + "\n\n" +
                        "Price: " + product.getPrice() + "\n" +
                        "Category: " + category + "\n" +
                        "Manufacturer: " + product.getManufacturer() + "\n" +
                        "Organic: " + (product.isOrganic() ? "Yes" : "No") + "\n\n" +
                        "Demo Version - Actual purchase will be available in production.")
                .setPositiveButton("Close", null)
                .setNeutralButton("Add to Cart", (dialog, which) -> {
                    Toast.makeText(this, "Demo: " + product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Share", (dialog, which) -> {
                    Toast.makeText(this, "Demo: Sharing " + product.getName(), Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private String getCategoryFromPrice(String price) {
        // Extract numeric value from price string
        String numericPrice = price.replaceAll("[^0-9,]", "");
        try {
            int priceValue = Integer.parseInt(numericPrice.replace(",", ""));

            if (priceValue >= 500000) {
                return "Heavy Machinery";
            } else if (priceValue >= 50000) {
                return "Farm Equipment";
            } else if (priceValue >= 5000) {
                return "Tools & Accessories";
            } else {
                return "Seeds & Fertilizers";
            }
        } catch (NumberFormatException e) {
            return "Farm Product";
        }
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
