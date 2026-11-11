package com.thang.quanlykhohang;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Product> products;

    public ProductAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_product, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvProductName);
        TextView tvPrice = convertView.findViewById(R.id.tvProductPrice);
        TextView tvCategory = convertView.findViewById(R.id.tvProductCategory);
        TextView tvQuantity = convertView.findViewById(R.id.tvProductQuantity);
        TextView tvWarning = convertView.findViewById(R.id.tvLowStockWarning);

        Product product = products.get(position);

        NumberFormat formatter = NumberFormat.getInstance(
                new Locale("vi", "VN")
        );

        tvName.setText(product.getName());

        tvPrice.setText("Giá bán: "
                + formatter.format(product.getPrice()) + " VNĐ");

        tvCategory.setText("Loại: " + product.getCategory());

        tvQuantity.setText("Số lượng tồn: " + product.getQuantity());

        if (product.getQuantity() < 5) {
            tvQuantity.setTextColor(Color.RED);
            tvWarning.setVisibility(View.VISIBLE);
        } else {
            tvQuantity.setTextColor(Color.DKGRAY);
            tvWarning.setVisibility(View.GONE);
        }

        return convertView;
    }
}
