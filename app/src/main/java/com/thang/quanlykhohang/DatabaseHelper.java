package com.thang.quanlykhohang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRODUCTS = "products";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createUsersTable =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "username TEXT NOT NULL UNIQUE, " +
                        "password TEXT NOT NULL)";

        db.execSQL(createUsersTable);

        String createProductsTable =
                "CREATE TABLE " + TABLE_PRODUCTS + " (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "price INTEGER NOT NULL CHECK(price > 0), " +
                        "quantity INTEGER NOT NULL CHECK(quantity >= 0), " +
                        "category TEXT NOT NULL, " +
                        "active INTEGER NOT NULL, " +
                        "description TEXT)";

        db.execSQL(createProductsTable);

        insertUser(db, "admin", "123456");

        insertProduct(db, "Chuột không dây", 150000,
                20, "Điện tử", true, "Chuột không dây");

        insertProduct(db, "Bàn phím cơ", 450000,
                3, "Điện tử", true, "Bàn phím cơ");

        insertProduct(db, "Sổ tay A5", 25000,
                40, "Văn phòng phẩm", true, "Sổ tay A5");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {

    }

    private long insertUser(SQLiteDatabase db,
                            String username, String password) {

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        return db.insert(TABLE_USERS, null, values);
    }

    public long insertUser(String username, String password) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        return db.insert(TABLE_USERS, null, values);
    }

    private long insertProduct(SQLiteDatabase db,
                               String name, int price, int quantity,
                               String category, boolean active,
                               String description) {

        ContentValues values = new ContentValues();

        values.put("name", name);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("category", category);
        values.put("active", active ? 1 : 0);
        values.put("description", description);

        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public long insertProduct(Product p) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", p.getName());
        values.put("price", p.getPrice());
        values.put("quantity", p.getQuantity());
        values.put("category", p.getCategory());
        values.put("active", p.isActive() ? 1 : 0);
        values.put("description", p.getDescription());

        return db.insert(TABLE_PRODUCTS, null, values);
    }

    public boolean checkLogin(String username, String password) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM " + TABLE_USERS +
                        " WHERE username = ? AND password = ?",
                new String[]{username, password}
        );

        try {
            return cursor.moveToFirst();
        } finally {
            cursor.close();
        }
    }

    public ArrayList<Product> getAllProducts() {

        ArrayList<Product> products = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_PRODUCTS +
                        " ORDER BY id ASC",
                null
        );

        try {
            while (cursor.moveToNext()) {

                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));

                int price = cursor.getInt(
                        cursor.getColumnIndexOrThrow("price"));

                int quantity = cursor.getInt(
                        cursor.getColumnIndexOrThrow("quantity"));

                String category = cursor.getString(
                        cursor.getColumnIndexOrThrow("category"));

                boolean active = cursor.getInt(
                        cursor.getColumnIndexOrThrow("active")) == 1;

                String description = cursor.getString(
                        cursor.getColumnIndexOrThrow("description"));

                Product p = new Product(
                        id, name, price, quantity,
                        category, active, description
                );

                products.add(p);
            }
        } finally {
            cursor.close();
        }

        return products;
    }

    public Product getProductById(int id) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_PRODUCTS,
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        try {
            if (cursor.moveToFirst()) {

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));

                int price = cursor.getInt(
                        cursor.getColumnIndexOrThrow("price"));

                int quantity = cursor.getInt(
                        cursor.getColumnIndexOrThrow("quantity"));

                String category = cursor.getString(
                        cursor.getColumnIndexOrThrow("category"));

                boolean active = cursor.getInt(
                        cursor.getColumnIndexOrThrow("active")) == 1;

                String description = cursor.getString(
                        cursor.getColumnIndexOrThrow("description"));

                return new Product(
                        id, name, price, quantity,
                        category, active, description
                );
            }
        } finally {
            cursor.close();
        }

        return null;
    }

    public int updateQuantity(int id, int quantity) {

        if (quantity < 0) {
            return 0;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("quantity", quantity);

        return db.update(
                TABLE_PRODUCTS,
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int deleteProduct(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_PRODUCTS,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public long getTotalValue() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COALESCE(SUM(price * quantity), 0) " +
                        "FROM " + TABLE_PRODUCTS,
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }

            return 0;
        } finally {
            cursor.close();
        }
    }

    public int countLowStock() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_PRODUCTS +
                        " WHERE quantity < 5",
                null
        );

        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }

            return 0;
        } finally {
            cursor.close();
        }
    }
}
