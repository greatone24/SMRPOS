package com.dee.smrpos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.dee.smrpos.entity.Product;
import com.dee.smrpos.util.DatabaseHelper;
import com.dee.smrpos.util.ProductImageLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class ProductMaintenanceActivity extends AppCompatActivity {

    private EditText editName, editPrice;
    private Button btnSave, btnChooseImage;
    private ImageButton btnBack;
    private ImageView imagePreview;
    private DatabaseHelper dbHelper;
    private Product product;

    // Declare launcher as a field
    private ActivityResultLauncher<String> pickImageLauncher;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_maintenance);

        // Initialize views
        editName = findViewById(R.id.editName);
        editPrice = findViewById(R.id.editPrice);
        imagePreview = findViewById(R.id.imagePreview);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DatabaseHelper(this);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(ProductMaintenanceActivity.this, OrderMenuActivity.class);
            startActivity(intent);
        });
        // Register the launcher here
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        saveImageToInternalStorage(uri);
                    }
                }
        );

        // Attach click listener after registering launcher
        btnChooseImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        // Handle editing existing product
        if (getIntent().hasExtra("product")) {
            product = (Product) getIntent().getSerializableExtra("product");
            editName.setText(product.getName());
            editPrice.setText(String.valueOf(product.getPrice()));

            ProductImageLoader.load(this, product.getImageName(), imagePreview);
        } else {
            product = new Product();
        }

        btnSave.setOnClickListener(v -> {
            if(editName.getText().toString().isEmpty() || editPrice.getText().toString().isEmpty()){
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            product.setName(editName.getText().toString());
            product.setPrice(Double.parseDouble(editPrice.getText().toString()));

            dbHelper.saveOrUpdateProduct(product);
            Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show();
            finish();
        });
        editName.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Check if touch is within drawableStart bounds
                if (event.getRawX() <= (editName.getLeft() + editName.getCompoundDrawables()[0].getBounds().width() + editName.getPaddingStart())) {
                    // Trigger your search logic here
                    String query = editName.getText().toString().trim();
                    searchProduct(query);
                    return true;
                }
            }
            return false;
        });
    }
    private void searchProduct(String query) {
        if (!query.isEmpty()) {
            Product existingProduct = dbHelper.getProductByName(query);
            if(existingProduct != null){

                editName.setText(existingProduct.getName());
                editPrice.setText(String.valueOf(existingProduct.getPrice()));
                product.setId(existingProduct.getId());
                ProductImageLoader.load(this, product.getImageName(), imagePreview);
                editName.setEnabled(false);
                editName.setFocusable(false);
                editName.setClickable(false);
            }else{
                product.setId(0);
            }

            Toast.makeText(this, "Searching for: " + query, Toast.LENGTH_SHORT).show();
            // TODO: implement actual search logic
        }
    }
    private void saveImageToInternalStorage(Uri uri) {
        File imageDirectory = new File(getFilesDir(), "product_images");
        if (!imageDirectory.isDirectory() && !imageDirectory.mkdirs()) {
            Toast.makeText(this, "Could not save product image", Toast.LENGTH_SHORT).show();
            return;
        }

        File imageFile = new File(imageDirectory, UUID.randomUUID().toString());
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                throw new IOException("The selected image could not be opened");
            }

            try (InputStream source = inputStream;
                 FileOutputStream destination = new FileOutputStream(imageFile)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = source.read(buffer)) != -1) {
                    destination.write(buffer, 0, bytesRead);
                }
            }

            product.setImageName(imageFile.getAbsolutePath());
            ProductImageLoader.load(this, product.getImageName(), imagePreview);
        } catch (IOException e) {
            if (imageFile.exists() && !imageFile.delete()) {
                Log.w("ProductMaintenance", "Could not remove incomplete product image");
            }
            Log.e("ProductMaintenance", "Could not save selected product image", e);
            Toast.makeText(this, "Could not save product image", Toast.LENGTH_SHORT).show();
        }
    }
}
