package com.br.salesbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class authenticationActivity extends AppCompatActivity {

    private EditText txEmail, txPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authentication);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txEmail = findViewById(R.id.tx_email);
        txPassword = findViewById(R.id.tx_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String email = txEmail.getText().toString();
            String password = txPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(() -> performLogin(email, password)).start();
            }
        });
    }

    private void performLogin(String email, String password) {
        try {
            URL url = new URL("http://10.0.2.2:3001/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("senha", password);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(json.toString());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                String responseText = readStream(conn.getInputStream());
                JSONObject jsonResponse = new JSONObject(responseText);
                int userId = jsonResponse.getInt("usuarioId");

                Log.d("SalesBuddy", "Login Success! User ID: " + userId);

                runOnUiThread(() -> {
                    Toast.makeText(authenticationActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();


                    Intent intent = new Intent(authenticationActivity.this, HomeActivity.class);

                    intent.putExtra("ID_DO_LOJISTA", userId);

                    startActivity(intent);
                    finish(); // Fecha o login para não voltar
                });

            } else {
                Log.e("SalesBuddy", "Login Error: " + responseCode);
                runOnUiThread(() ->
                        Toast.makeText(authenticationActivity.this, "Invalid credentials", Toast.LENGTH_LONG).show()
                );
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(authenticationActivity.this, "Connection Error", Toast.LENGTH_LONG).show()
            );
        }
    }

    private String readStream(java.io.InputStream in) throws java.io.IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }
}