package com.br.salesbuddy.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.br.salesbuddy.R;

public class ConnectionErrorActivity extends AppCompatActivity {

    private Button btnRetry, btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_connection_error);

        setupWindowInsets();

        btnRetry = findViewById(R.id.btn_retry);
        btnClose = findViewById(R.id.btn_close);

        // Ação do botão Tentar Novamente
        btnRetry.setOnClickListener(v -> {
            if (isNetworkAvailable()) {

                Toast.makeText(this, "Conexão restabelecida!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Ainda sem conexão. Verifique sua internet.", Toast.LENGTH_SHORT).show();
            }
        });

        btnClose.setOnClickListener(v -> finish());
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}