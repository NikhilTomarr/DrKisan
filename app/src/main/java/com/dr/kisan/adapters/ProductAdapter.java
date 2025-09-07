package com.dr.kisan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.dr.kisan.R;
import com.dr.kisan.models.Product;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private Context context;
    private List<Product> products;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);

        holder.textProductName.setText(product.getName());
        holder.textDescription.setText(product.getDescription());
        holder.textPrice.setText(product.getPrice());
        holder.textManufacturer.setText("By " + product.getManufacturer());

        // Show organic badge
        if (product.isOrganic()) {
            holder.imageOrganic.setVisibility(View.VISIBLE);
            holder.textOrganic.setVisibility(View.VISIBLE);
        } else {
            holder.imageOrganic.setVisibility(View.GONE);
            holder.textOrganic.setVisibility(View.GONE);
        }

        // Buy button click
        holder.btnBuy.setOnClickListener(v -> {
            Toast.makeText(context, "This is demo. Buying feature coming soon!", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        CardView cardProduct;
        TextView textProductName;
        TextView textDescription;
        TextView textPrice;
        TextView textManufacturer;
        TextView textOrganic;
        ImageView imageOrganic;
        MaterialButton btnBuy;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProduct = itemView.findViewById(R.id.cardProduct);
            textProductName = itemView.findViewById(R.id.textProductName);
            textDescription = itemView.findViewById(R.id.textDescription);
            textPrice = itemView.findViewById(R.id.textPrice);
            textManufacturer = itemView.findViewById(R.id.textManufacturer);
            textOrganic = itemView.findViewById(R.id.textOrganic);
            imageOrganic = itemView.findViewById(R.id.imageOrganic);
            btnBuy = itemView.findViewById(R.id.btnBuy);
        }
    }
}
