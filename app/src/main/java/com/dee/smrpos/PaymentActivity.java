package com.dee.smrpos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dee.smrpos.util.DatabaseHelper;
import com.dee.smrpos.util.GoogleSheetUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private double totalAmount = 0;
    ImageButton btnCash, btnEwallet, btnBank;
    ImageButton selectedButton = null;
    Button btn20, btn50, btn100, btn200, btn500, btn1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        TextView txtTotalAmount = findViewById(R.id.txtTotalAmount);
        EditText edtAmountReceived = findViewById(R.id.edtAmountReceived);
        TextView txtChange = findViewById(R.id.txtChange);
        Button btnCompletePayment = findViewById(R.id.btnCompletePayment);
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0.0);
        txtTotalAmount.setText("Total Amount: ₱" + totalAmount);
        btnCash = findViewById(R.id.btnCash);
        btnEwallet = findViewById(R.id.btnEwallet);
        btnBank = findViewById(R.id.btnBank);
        btn20 = findViewById(R.id.btn20);
        btn50 = findViewById(R.id.btn50);
        btn100 = findViewById(R.id.btn100);
        btn200 = findViewById(R.id.btn200);
        btn500 = findViewById(R.id.btn500);
        btn1000 = findViewById(R.id.btn1000);
        btn20.setOnClickListener(v -> {
            edtAmountReceived.setText("20");
        });
        btn50.setOnClickListener(v -> {
            edtAmountReceived.setText("50");
        });
        btn100.setOnClickListener(v -> {
            edtAmountReceived.setText("100");
        });
        btn200.setOnClickListener(v -> {
            edtAmountReceived.setText("200");
        });
        btn500.setOnClickListener(v -> {
            edtAmountReceived.setText("500");
        });
        btn1000.setOnClickListener(v -> {
            edtAmountReceived.setText("1000");
        });
        View.OnClickListener listener = v -> {
            resetButtonStyles(); // clear previous selection

            selectedButton = (ImageButton) v;
            selectedButton.setBackgroundColor(Color.parseColor("#4CAF50")); // highlight selected

            String paymentMethod = selectedButton.getContentDescription().toString();
            Toast.makeText(this, "Selected: " + paymentMethod, Toast.LENGTH_SHORT).show();
        };

        btnCash.setOnClickListener(listener);
        btnEwallet.setOnClickListener(listener);
        btnBank.setOnClickListener(listener);
        btnCompletePayment.setOnClickListener(v -> {
            String input = edtAmountReceived.getText().toString();
            double received = 0.0;

            if (!input.isEmpty()) {
                try {
                    received = Double.parseDouble(input);
                } catch (NumberFormatException e) {
                    txtChange.setText("Invalid input!");
                    return;
                }
            }
            if (selectedButton == null) {
                btnCash.setSelected(true);
                btnEwallet.setSelected(true);
                btnBank.setSelected(true);
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }
            double change = received - totalAmount;

            if (change >= 0) {
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                txtChange.setText("Change: ₱" + change);
                String selectedMethod = selectedButton.getContentDescription().toString(); // from your radio-like ImageButtons
                String checkoutDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(new Date());
                long checkoutId = dbHelper.saveCheckout(totalAmount, selectedMethod, checkoutDate);
                if (checkoutId == -1) {
                    Toast.makeText(this, "Unable to save checkout", Toast.LENGTH_SHORT).show();
                    return;
                }
                try{

                    GoogleSheetUtil.googleSheetInitialization(this, checkoutId, totalAmount, selectedMethod, checkoutDate);
                    dbHelper.clearCart();
                    Intent intent = new Intent(PaymentActivity.this, OrderMenuActivity.class);
                    startActivity(intent);
                }catch (Exception e){

                }
            } else {
                txtChange.setText("Insufficient amount!");
                return;
            }
        });
        edtAmountReceived.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used, but required to override
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Runs every time the text changes
                String input = s.toString();

                if (!input.isEmpty()) {
                    try {
                        // Remove peso symbol if user types it
                        input = input.replaceAll("[^0-9.]", "");
                        double received = Double.parseDouble(input);

                        double change = received - totalAmount;

                        if (change >= 0) {
                            txtChange.setText("Change: ₱" + change);
                        } else {
                            txtChange.setText("Insufficient amount!");
                        }
                    } catch (NumberFormatException e) {
                        txtChange.setText("Invalid input!");
                    }
                } else {
                    txtChange.setText("Change: ₱0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used, but required to override
            }
        });
    }
    private void resetButtonStyles() {
        btnCash.setBackgroundColor(Color.TRANSPARENT);
        btnEwallet.setBackgroundColor(Color.TRANSPARENT);
        btnBank.setBackgroundColor(Color.TRANSPARENT);
    }


}
