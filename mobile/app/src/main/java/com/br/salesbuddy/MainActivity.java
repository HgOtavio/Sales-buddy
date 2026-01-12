package com.br.salesbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etEmail = findViewById(R.id.etEmail);
        EditText etSenha = findViewById(R.id.etSenha);
        Button btnEntrar = findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String senha = etSenha.getText().toString();

                if(email.isEmpty() || senha.isEmpty()){
                    Toast.makeText(MainActivity.this, "Preencha tudo!", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> fazerLogin(email, senha)).start();
            }
        });
    }

    private void fazerLogin(String email, String senha) {
        try {
            // Conecta no Backend (10.0.2.2 é o localhost do PC)
            URL url = new URL("http://10.0.2.2:3001/login");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setDoOutput(true);

            // Envia Email e Senha
            JSONObject json = new JSONObject();
            json.put("email", email);
            json.put("senha", senha);

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(json.toString());
            os.flush();
            os.close();

            int codigo = conn.getResponseCode();

            if (codigo == 200) {
                // SUCESSO! Agora precisamos ler o ID que o servidor mandou
                String respostaTexto = lerStream(conn.getInputStream());
                JSONObject jsonResposta = new JSONObject(respostaTexto);
                int idUsuario = jsonResposta.getInt("usuarioId");

                Log.d("SalesBuddy", "✅ Login OK! Usuário ID: " + idUsuario);

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Bem-vindo!", Toast.LENGTH_SHORT).show();

                    // --- AQUI FAZ A LIGAÇÃO COM A TELA DE VENDA ---
                    Intent intent = new Intent(MainActivity.this, VendaActivity.class);
                    intent.putExtra("ID_DO_LOJISTA", idUsuario); // Leva o ID pra próxima tela
                    startActivity(intent);
                    finish(); // Fecha a tela de login
                });

            } else {
                Log.e("SalesBuddy", "❌ Erro login: " + codigo);
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Email ou senha errados", Toast.LENGTH_LONG).show()
                );
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(MainActivity.this, "Erro de conexão", Toast.LENGTH_LONG).show()
            );
        }
    }

    // Função auxiliar obrigatória para ler a resposta do servidor
    private String lerStream(java.io.InputStream in) throws java.io.IOException {
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