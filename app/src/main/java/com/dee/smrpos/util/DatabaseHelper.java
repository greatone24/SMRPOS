package com.dee.smrpos.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dee.smrpos.entity.CartItem;
import com.dee.smrpos.entity.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "POS_DB";
    private static final int DB_VERSION = 4;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE cartItem(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price REAL, quantity TEXT)");
        createCheckoutTable(db);
        db.execSQL("CREATE TABLE products(id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, price REAL, imageName INTEGER)");
        db.execSQL("INSERT INTO products(name, price, imageName) VALUES('Latte', 90, 'ic_latte')");
        db.execSQL("INSERT INTO products(name, price, imageName) VALUES('Cappuccino', 100, 'ic_cappuccino')");
        db.execSQL("INSERT INTO products(name, price, imageName) VALUES('Muffin', 120, 'ic_muffin')");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            createCheckoutTable(db);
        }
    }

    private void createCheckoutTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS checkout (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "date TEXT, " +
                "total REAL, " +
                "paymentMethod TEXT)");
    }
    public void saveCartItem(CartItem cartItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", cartItem.getName());
        values.put("price", cartItem.getPrice());
        values.put("quantity", cartItem.getQuantity());

        // Try update first
        int rows = db.update("cartItem", values, "name = ?", new String[]{cartItem.getName()});

        if (rows == 0) {
            // If no rows updated, insert new
            db.insert("cartItem", null, values);
        }

        db.close();
    }

    public List<CartItem> getCartItems() {
        List<CartItem> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM cartItem", null);
        if (cursor.moveToFirst()) {
            do {
                CartItem cartItem = new CartItem();
                cartItem.setId(cursor.getInt(0));
                cartItem.setName(cursor.getString(1));
                cartItem.setPrice(cursor.getDouble(2));
                cartItem.setQuantity(cursor.getInt(3));
                list.add(cartItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Product getProductByName(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Product product = new Product();
        Cursor cursor = db.rawQuery("SELECT * FROM products WHERE name = ?", new String[]{name});
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int priceIndex = cursor.getColumnIndex("price");
            int imageResIdIndex = cursor.getColumnIndex("imageName");

            product.setId(cursor.getInt(idIndex));
            product.setName(cursor.getString(nameIndex));
            product.setPrice(cursor.getDouble(priceIndex));
            product.setImageName(cursor.getString(imageResIdIndex));
        }
        return product;
    }
    public List<Product> getProducts() {
        List<Product> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM products", null);

        if (cursor.moveToFirst()) {
            do {
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int priceIndex = cursor.getColumnIndex("price");
                int imageResIdIndex = cursor.getColumnIndex("imageName");

                Product product = new Product();
                product.setId(cursor.getInt(idIndex));
                product.setName(cursor.getString(nameIndex));
                product.setPrice(cursor.getDouble(priceIndex));
                product.setImageName(cursor.getString(imageResIdIndex));
                list.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
    // Remove single item
    public void removeCartItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("cartItem", "id" + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public long saveCheckout(double total, String paymentMethod, String checkoutDate) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date", checkoutDate);
        values.put("total", total);
        values.put("paymentMethod", paymentMethod);

        long id = db.insert("checkout", null, values);
        db.close();
        return  id;
    }


    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM cartItem");
        db.close();
    }

    public Double getTotalCartPrice(){
        Double total = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(price * quantity) AS total FROM cartItem", null);
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        return total;
    }

    public void saveOrUpdateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", product.getName());
        values.put("price", product.getPrice());
        values.put("imageName", product.getImageName()); // store drawable name or file path

        if (product.getId() > 0) {
            // Update existing product
            db.update("products", values, "id=?", new String[]{String.valueOf(product.getId())});
        } else {
            // Insert new product
            long newId = db.insert("products", null, values);
            product.setId((int) newId); // assign generated ID back to object
        }

        db.close();
    }

}
