package com.br.salesbuddy.view;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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


        btnRetry.setOnClickListener(v -> {
            if (isNetworkAvailable()) {

                showCustomToast("Conexão restabelecida!", true);
                finish();
            } else {

                showCustomToast("Ainda sem conexão. Verifique sua internet.", false);
            }
        });

        // Ação do botão Fechar
        btnClose.setOnClickListener(v -> finish());
    }


    private void showCustomToast(String message, boolean isSuccess) {
        runOnUiThread(() -> {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

            TextView text = layout.findViewById(R.id.tvToastMessage);
            text.setText(message);

            if (isSuccess) {
                layout.setBackgroundResource(R.drawable.bg_toast_sucesso);
            } else {
                layout.setBackgroundResource(R.drawable.bg_toast_erro);
            }

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        });
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