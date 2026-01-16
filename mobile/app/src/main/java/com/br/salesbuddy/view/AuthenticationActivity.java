package com.br.salesbuddy.view; // Note o package view

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.br.salesbuddy.HomeActivity; // Supondo que HomeActivity esteja na r  aiz ou em view
import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.LoginContract;
import com.br.salesbuddy.presenter.LoginPresenter;

public class AuthenticationActivity extends AppCompatActivity implements LoginContract.View {

    private EditText txUser, txPassword;
    private Button btnLogin;
    private ProgressBar loadingSpinner; // Adicione no XML
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // --- Bind Views ---
        txUser = findViewById(R.id.tx_email); // ID mantido do XML, mas logica é User
        txPassword = findViewById(R.id.tx_password);
        btnLogin = findViewById(R.id.btn_login);

        // DICA: Adicione um ProgressBar no seu XML com id 'loading_spinner' e visibility="gone"
        // loadingSpinner = findViewById(R.id.loading_spinner);

        // --- Init Presenter ---
        presenter = new LoginPresenter(this, this);

        // --- Actions ---
        btnLogin.setOnClickListener(v -> {
            String user = txUser.getText().toString();
            String pass = txPassword.getText().toString();
            presenter.performLogin(user, pass);
        });
    }

    // --- Implementação da Interface View ---

    @Override
    public void showLoading() {
        btnLogin.setEnabled(false);
        // if (loadingSpinner != null) loadingSpinner.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Conectando...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideLoading() {
        btnLogin.setEnabled(true);
        // if (loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
    }

    @Override
    public void showLoginError(String message) {
        // Mostra a mensagem exata que veio do backend (ex: "Senha incorreta")
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoginSuccess(int userId, String userName) {
        Toast.makeText(this, "Bem-vindo, " + userName, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("ID_DO_LOJISTA", userId);
        startActivity(intent);
        finish();
    }
}