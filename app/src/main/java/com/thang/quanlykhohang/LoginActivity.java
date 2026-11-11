package com.thang.quanlykhohang;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private TextView tvError;
    private Button btnLogin;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tvError = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);

        dbHelper = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            tvError.setText("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu");
            tvError.setVisibility(View.VISIBLE);
            return;
        }

        boolean isValid = dbHelper.checkLogin(username, password);

        if (isValid) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            tvError.setText("Sai tên đăng nhập hoặc mật khẩu");
            tvError.setVisibility(View.VISIBLE);
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
