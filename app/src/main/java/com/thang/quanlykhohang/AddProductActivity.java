package com.thang.quanlykhohang;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddProductActivity extends AppCompatActivity {

    private EditText edtProductName;
    private EditText edtProductPrice;
    private EditText edtProductQuantity;
    private EditText edtProductDescription;
    private Spinner spProductCategory;
    private Switch swProductActive;
    private Button btnSaveProduct;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        edtProductName = findViewById(R.id.edtProductName);
        edtProductPrice = findViewById(R.id.edtProductPrice);
        edtProductQuantity = findViewById(R.id.edtProductQuantity);
        edtProductDescription = findViewById(R.id.edtProductDescription);

        spProductCategory = findViewById(R.id.spProductCategory);
        swProductActive = findViewById(R.id.swProductActive);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);

        dbHelper = new DatabaseHelper(this);

        setupCategorySpinner();

        btnSaveProduct.setOnClickListener(v -> saveProduct());
    }

    private void setupCategorySpinner() {

        String[] categories = {
                "Điện tử",
                "Thực phẩm",
                "Văn phòng phẩm",
                "Gia dụng",
                "Khác"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spProductCategory.setAdapter(adapter);
    }

    private void saveProduct() {

        String name = edtProductName.getText().toString().trim();
        String priceText = edtProductPrice.getText().toString().trim();
        String quantityText = edtProductQuantity.getText().toString().trim();
        String description = edtProductDescription.getText().toString().trim();

        if (name.isEmpty()) {
            edtProductName.setError("Vui lòng nhập tên sản phẩm");
            edtProductName.requestFocus();
            return;
        }

        if (priceText.isEmpty()) {
            edtProductPrice.setError("Vui lòng nhập giá bán");
            edtProductPrice.requestFocus();
            return;
        }

        if (quantityText.isEmpty()) {
            edtProductQuantity.setError("Vui lòng nhập số lượng");
            edtProductQuantity.requestFocus();
            return;
        }

        int price;
        int quantity;

        try {
            price = Integer.parseInt(priceText);

            if (price <= 0) {
                edtProductPrice.setError("Giá bán phải lớn hơn 0");
                edtProductPrice.requestFocus();
                return;
            }

        } catch (NumberFormatException e) {
            edtProductPrice.setError("Giá bán không hợp lệ");
            edtProductPrice.requestFocus();
            return;
        }

        try {
            quantity = Integer.parseInt(quantityText);

            if (quantity < 0) {
                edtProductQuantity.setError("Số lượng không được âm");
                edtProductQuantity.requestFocus();
                return;
            }

        } catch (NumberFormatException e) {
            edtProductQuantity.setError("Số lượng không hợp lệ");
            edtProductQuantity.requestFocus();
            return;
        }

        String category = spProductCategory.getSelectedItem().toString();

        boolean active = swProductActive.isChecked();

        Product product = new Product(
                0,
                name,
                price,
                quantity,
                category,
                active,
                description
        );

        long result = dbHelper.insertProduct(product);

        if (result != -1) {

            Toast.makeText(
                    this,
                    "Thêm sản phẩm thành công",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

        } else {

            Toast.makeText(
                    this,
                    "Không thể thêm sản phẩm",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
