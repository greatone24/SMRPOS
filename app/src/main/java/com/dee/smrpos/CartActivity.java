package com.dee.smrpos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dee.smrpos.entity.CartItem;
import com.dee.smrpos.util.DatabaseHelper;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartChangeListener {
    private DatabaseHelper dbHelper;
    private CartAdapter adapter;
    private List<CartItem> cartItems;

    private TextView txtSubtotal, txtTax, txtTotal;
    private Button btnClearCart, btnCheckout;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new DatabaseHelper(this);
        cartItems = dbHelper.getCartItems();

        adapter = new CartAdapter(cartItems, this);
        RecyclerView recyclerView = findViewById(R.id.recyclerCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        txtSubtotal = findViewById(R.id.txtSubtotal);
//        txtTax = findViewById(R.id.txtTax);
        txtTotal = findViewById(R.id.txtTotal);

        btnClearCart = findViewById(R.id.btnClearCart);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);
        // Register a back press callback
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {

                Log.d("CartActivity", "Back button pressed");
                for(CartItem item : cartItems){
                    dbHelper.saveCartItem(item);
                }
                backToOrderMenu();
            }
        });
        btnBack.setOnClickListener(v -> {
            backToOrderMenu();
        });
        btnClearCart.setOnClickListener(v -> {
            dbHelper.clearCart();
            cartItems.clear();
            adapter.notifyDataSetChanged();
            updateSummary();
        });

        btnCheckout.setOnClickListener(v -> {
            String amountStr = txtTotal.getText().toString();

            amountStr = amountStr.replace("₱", "").trim();

            double amount = Double.parseDouble(amountStr);
            if(amount>0){
                System.out.println("Parsed amount: " + amount); // Output: 200.0
                Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
                intent.putExtra("totalAmount", amount);
                startActivity(intent);
                Toast.makeText(this, "Proceeding to checkout...", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "There is No Item in the Cart", Toast.LENGTH_SHORT).show();
            }
          });

        updateSummary();
    }
    @Override
    public void onCartUpdated(List<CartItem> cartItems) {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        txtTotal.setText(String.format("₱%.2f", total));
        txtSubtotal.setText(String.format("₱%.2f", total));
    }

    @Override
    public void removeCartItem(int cartId) {
        dbHelper.removeCartItem(cartId);
    }

    private void updateSummary() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getPrice() * item.getQuantity();
        }
        double tax = subtotal * 0.09;
//        double total = subtotal + tax;
        double total = subtotal;
        txtSubtotal.setText("₱" + subtotal);
//        txtTax.setText("Tax: ₱" + tax);
        txtTotal.setText("₱" + total);
    }
    private void backToOrderMenu() {
        Intent intent = new Intent(CartActivity.this, OrderMenuActivity.class);
        startActivity(intent);
    }

}
