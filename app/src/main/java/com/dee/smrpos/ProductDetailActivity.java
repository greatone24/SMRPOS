package com.dee.smrpos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dee.smrpos.entity.CartItem;
import com.dee.smrpos.util.DatabaseHelper;
import com.dee.smrpos.util.ProductImageLoader;

public class ProductDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        TextView name = findViewById(R.id.textProductName);
        TextView price = findViewById(R.id.textProductPrice);
        ImageView image = findViewById(R.id.imageProduct);
        Button btnMinus = findViewById(R.id.btnMinus);
        Button btnPlus = findViewById(R.id.btnPlus);
        TextView textQuantity = findViewById(R.id.textQuantity);
//        TextView textQuantity2 = findViewById(R.id.textQuantity2);
        Button btnConfirm = findViewById(R.id.btnConfirm);
        Button btnCancel = findViewById(R.id.btnCancel);
        TextView textTotal = findViewById(R.id.textTotal);
        TextView textPrice = findViewById(R.id.textPrice);
        // Get data from intent
        String productName = getIntent().getStringExtra("name");
        double productPrice = getIntent().getDoubleExtra("price", 0);
        String imageName = getIntent().getStringExtra("imageName");

        Log.d("ProductDetailActivity", "productPrice: " + productPrice);
        name.setText(productName);
        price.setText("₱" + productPrice);
        textPrice.setText("₱" + productPrice);
        ProductImageLoader.load(this, imageName, image);
        textQuantity.setText(String.valueOf(1));
//        textQuantity2.setText("x "+String.valueOf(1));
        textTotal.setText(String.format("₱%.2f", productPrice * 1));

        btnMinus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textQuantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                textQuantity.setText(String.valueOf(quantity));
//                textQuantity2.setText("x "+String.valueOf(quantity));
                textTotal.setText(String.format("₱%.2f", productPrice * quantity));
            }
        });
        btnPlus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textQuantity.getText().toString());
            quantity++;
            textQuantity.setText(String.valueOf(quantity));
//            textQuantity2.setText("x "+String.valueOf(quantity));
            textTotal.setText(String.format("₱%.2f", productPrice * quantity));
        });

        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, OrderMenuActivity.class);
            startActivity(intent);
        });

        btnConfirm.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
//            CartItem cartItem = new CartItem(0,productName, productPrice, Integer.parseInt(textQuantity.getText().toString()));
            CartItem cartItem = new CartItem();
            cartItem.setName(productName);
            cartItem.setPrice(productPrice);
            cartItem.setQuantity(Integer.parseInt(textQuantity.getText().toString()));
            dbHelper.saveCartItem(cartItem);
            Intent intent = new Intent(ProductDetailActivity.this, OrderMenuActivity.class);
            startActivity(intent);
        });
    }
}
