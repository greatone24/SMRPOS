package com.dee.smrpos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dee.smrpos.entity.CartItem;
import com.dee.smrpos.entity.Product;
import com.dee.smrpos.util.DatabaseHelper;
import com.dee.smrpos.util.UserUtil;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class OrderMenuActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_order_menu);
    Button btnCart = findViewById(R.id.btnCart);
    TextView toolbarPrice = findViewById(R.id.toolbarPrice);
    RecyclerView recyclerView = findViewById(R.id.recyclerMenu);
    recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 drinks per row
    TextView welcomeMessage = findViewById(R.id.welcomeMessage);

    drawerLayout = findViewById(R.id.drawerLayout);
    navigationView = findViewById(R.id.navigationView);
    View headerView = navigationView.getHeaderView(0);
    TextView txtUserName = headerView.findViewById(R.id.txtUserName);
    String welcomeMssge = "Welcome, " + UserUtil.getUsername(this) + "!";
    welcomeMessage.setText(welcomeMssge);
    txtUserName.setText(welcomeMssge);
    DatabaseHelper dbHelper = new DatabaseHelper(this);
    btnCart.setText("View Cart ₱" + dbHelper.getTotalCartPrice() + "0");
    toolbarPrice.setText(dbHelper.getTotalCartPrice() + "0");

    List<Product> products = dbHelper.getProducts();
    Log.d("OrderMenuActivity", "Products count: " + products.size());
    List<CartItem> cartItems = dbHelper.getCartItems();
    Log.d("OrderMenuActivity", "CartItem count: " + cartItems.size());
    recyclerView.setAdapter(new MenuAdapter(this,products));

    btnCart.setOnClickListener(v -> {
        Intent intent = new Intent(OrderMenuActivity.this, CartActivity.class);
        startActivity(intent);
    });

    MaterialToolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle(""); // removes project name


    toggle = new ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.open, R.string.close);

    drawerLayout.addDrawerListener(toggle);
    toggle.syncState();
    // Handle navigation clicks
    navigationView.setNavigationItemSelectedListener(item -> {
        if(item.getItemId() == R.id.nav_home){

        }
        if(item.getItemId() == R.id.nav_product_maintenance){
            Intent intent = new Intent(OrderMenuActivity.this, ProductMaintenanceActivity.class);
            startActivity(intent);
        }
        if(item.getItemId() == R.id.nav_reports) {

        }
        if(item.getItemId() == R.id.nav_settings){

        }
        drawerLayout.closeDrawers();
        return true;
    });
}

}
