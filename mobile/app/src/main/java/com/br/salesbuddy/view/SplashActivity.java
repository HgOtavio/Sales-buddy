package com.br.salesbuddy.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.SplashContract;
import com.br.salesbuddy.presenter.SplashPresenter;
import com.br.salesbuddy.view.AuthenticationActivity;

public class SplashActivity extends AppCompatActivity implements SplashContract.View {

    private SplashContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        presenter = new SplashPresenter(this);

        presenter.startSplashLogic();
    }

    @Override
    public void navigateToLogin() {
        runOnUiThread(() -> {
            Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void navigateToConnectionError() {
        Intent intent = new Intent(this, ConnectionErrorActivity.class);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        presenter.destroy();
        super.onDestroy();
    }
}