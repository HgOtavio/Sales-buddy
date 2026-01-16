package com.br.salesbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

import com.br.salesbuddy.view.AuthenticationActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Espera 3 segundos
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            // 1. Cria a intenção de ir para o Login (authenticationActivity)
            Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
            startActivity(intent);

            finish();

        }, 3000);
    }
}