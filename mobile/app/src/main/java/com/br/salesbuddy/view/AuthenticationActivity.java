package com.br.salesbuddy.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.LoginContract;
import com.br.salesbuddy.presenter.LoginPresenter;

public class AuthenticationActivity extends AppCompatActivity implements LoginContract.View {

    private EditText txUser, txPassword;
    private Button btnLogin;
    private LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        txUser = findViewById(R.id.tx_email);
        txPassword = findViewById(R.id.tx_password);
        btnLogin = findViewById(R.id.btn_login);

        presenter = new LoginPresenter(this, this);

        btnLogin.setOnClickListener(v -> {
            String user = txUser.getText().toString();
            String pass = txPassword.getText().toString();
            presenter.performLogin(user, pass);
        });
    }

    @Override
    public void showLoading() {

        btnLogin.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        btnLogin.setEnabled(true);
    }

    @Override
    public void showLoginError(String message) {

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLoginSuccess(int userId, String userName) {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("ID_DO_LOJISTA", userId);
        startActivity(intent);
        finish();
    }
}