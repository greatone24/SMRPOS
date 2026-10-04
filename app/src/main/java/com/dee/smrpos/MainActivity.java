package com.dee.smrpos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dee.smrpos.util.UserUtil;

public class MainActivity extends AppCompatActivity {
    private EditText username;
    private Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        username = findViewById(R.id.etUsername);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String userText = username.getText().toString().trim();
            if (userText.isEmpty()) {
                username.setError("Username cannot be empty");
                return;
            }
            UserUtil.saveUsername(this, userText);
            Intent intent = new Intent(MainActivity.this, OrderMenuActivity.class);
            intent.putExtra("username", userText);
            startActivity(intent);
        });
    }
}