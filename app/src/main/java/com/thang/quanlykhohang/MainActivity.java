package com.thang.quanlykhohang;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStock;

    private ListView lvProducts;
    private Button btnAddProduct;

    private DatabaseHelper dbHelper;
    private ProductAdapter adapter;
    private ArrayList<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStock = findViewById(R.id.tvLowStock);

        lvProducts = findViewById(R.id.lvProducts);
        btnAddProduct = findViewById(R.id.btnAddProduct);

        dbHelper = new DatabaseHelper(this);

        products = new ArrayList<>();

        adapter = new ProductAdapter(this, products);
        lvProducts.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddProductActivity.class
            );

            startActivity(intent);
        });

        lvProducts.setOnItemClickListener((parent, view, position, id) -> {

            Product selectedProduct = products.get(position);

            Intent intent = new Intent(
                    MainActivity.this,
                    ProductDetailActivity.class
            );

            intent.putExtra("PRODUCT_ID", selectedProduct.getId());

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
        loadStatistics();
    }

    private void loadProducts() {
        products.clear();
        products.addAll(dbHelper.getAllProducts());

        adapter.notifyDataSetChanged();
    }


    private void loadStatistics() {

        int totalProducts = products.size();

        long totalValue = dbHelper.getTotalValue();

        int lowStock = dbHelper.countLowStock();

        NumberFormat formatter = NumberFormat.getInstance(
                new Locale("vi", "VN")
        );

        tvTotalProducts.setText(
                String.valueOf(totalProducts)
        );

        tvTotalValue.setText(
                formatter.format(totalValue) + " VNĐ"
        );

        tvLowStock.setText(
                String.valueOf(lowStock)
        );
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
