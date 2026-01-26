package com.br.salesbuddy.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

    @Override
    public void showLoading() {
        btnLogin.setEnabled(false);
        btnLogin.setText("ENTRANDO...");
    }

    @Override
    public void hideLoading() {
        btnLogin.setEnabled(true);
        btnLogin.setText("ENTRAR");
    }

    @Override
    public void showLoginError(String message) {
        showCustomToast(message, false);
    }

    @Override
    public void onLoginSuccess(int userId, String userName) {
        showCustomToast("Bem-vindo(a), " + userName + "!", true);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("ID_DO_LOJISTA", userId);
        startActivity(intent);
        finish();
    }

    @Override
    public void navigateToConnectionError() {
        Intent intent = new Intent(this, ConnectionErrorActivity.class);
        startActivity(intent);
    }
}