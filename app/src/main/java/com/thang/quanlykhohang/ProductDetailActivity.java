package com.thang.quanlykhohang;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.NumberFormat;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView tvDetailName;
    private TextView tvDetailCategory;
    private TextView tvDetailPrice;
    private TextView tvDetailQuantity;
    private TextView tvDetailInventoryValue;
    private TextView tvDetailLowStock;
    private TextView tvDetailDescription;

    private Button btnAddStock;
    private Button btnSellOne;
    private Button btnDetailBack;
    private Button btnDeleteProduct;

    private DatabaseHelper dbHelper;

    private int productId;
    private Product product;

    private NumberFormat formatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        tvDetailInventoryValue = findViewById(R.id.tvDetailInventoryValue);
        tvDetailLowStock = findViewById(R.id.tvDetailLowStock);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        btnAddStock = findViewById(R.id.btnAddStock);
        btnSellOne = findViewById(R.id.btnSellOne);
        btnDetailBack = findViewById(R.id.btnDetailBack);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);

        dbHelper = new DatabaseHelper(this);

        formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

        productId = getIntent().getIntExtra("PRODUCT_ID", -1);

        if (productId == -1) {
            Toast.makeText(this,
                    "Không tìm thấy mã sản phẩm",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadProduct();

        btnAddStock.setOnClickListener(v -> addStock());

        btnSellOne.setOnClickListener(v -> sellOne());

        btnDetailBack.setOnClickListener(v -> finish());

        btnDeleteProduct.setOnClickListener(v -> deleteProduct());
    }

    private void loadProduct() {

        product = dbHelper.getProductById(productId);

        if (product == null) {
            Toast.makeText(this,
                    "Sản phẩm không tồn tại",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvDetailName.setText(product.getName());

        tvDetailCategory.setText(
                "Loại sản phẩm: " + product.getCategory()
        );

        tvDetailPrice.setText(
                formatter.format(product.getPrice()) + " VNĐ"
        );

        tvDetailQuantity.setText(
                "Số lượng tồn: " + product.getQuantity()
        );

        long inventoryValue =
                (long) product.getPrice() * product.getQuantity();

        tvDetailInventoryValue.setText(
                "Giá trị tồn kho: " + formatter.format(inventoryValue) + " VNĐ"
        );

        if (product.getQuantity() < 5) {
            tvDetailLowStock.setVisibility(TextView.VISIBLE);
            tvDetailLowStock.setText("Sắp hết hàng");
        } else {
            tvDetailLowStock.setVisibility(TextView.GONE);
        }

        String description = product.getDescription();

        if (description == null || description.trim().isEmpty()) {
            tvDetailDescription.setText("Chưa có mô tả");
        } else {
            tvDetailDescription.setText(description);
        }
    }

    private void addStock() {

        int newQuantity = product.getQuantity() + 1;

        dbHelper.updateQuantity(productId, newQuantity);

        loadProduct();

        Toast.makeText(this,
                "Đã nhập thêm 1 sản phẩm",
                Toast.LENGTH_SHORT).show();
    }

    private void sellOne() {

        int currentQuantity = product.getQuantity();

        if (currentQuantity == 0) {
            Toast.makeText(this,
                    "Hết hàng",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int newQuantity = currentQuantity - 1;

        dbHelper.updateQuantity(productId, newQuantity);

        loadProduct();

        Toast.makeText(this,
                "Đã bán 1 sản phẩm",
                Toast.LENGTH_SHORT).show();
    }

    private void deleteProduct() {

        dbHelper.deleteProduct(productId);

        Toast.makeText(this,
                "Đã xóa sản phẩm",
                Toast.LENGTH_SHORT).show();

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
