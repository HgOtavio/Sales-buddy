package com.br.salesbuddy;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VendaActivity extends AppCompatActivity {

    // Componentes da tela
    EditText etNome, etCpf, etEmail, etValorVenda, etValorRecebido;
    Button btnFinalizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venda); // Ou activity_main se usou o main

        // 1. Vincular com o XML
        etNome = findViewById(R.id.etNomeCliente);
        etCpf = findViewById(R.id.etCpf);
        etEmail = findViewById(R.id.etEmailCliente);
        etValorVenda = findViewById(R.id.etValorVenda);
        etValorRecebido = findViewById(R.id.etValorRecebido);
        btnFinalizar = findViewById(R.id.btnFinalizar);

        // 2. Ação do Botão
        btnFinalizar.setOnClickListener(v -> {
            // Pega os textos digitados
            String nome = etNome.getText().toString();
            String cpf = etCpf.getText().toString();
            String email = etEmail.getText().toString();
            String valVenda = etValorVenda.getText().toString();
            String valRecebido = etValorRecebido.getText().toString();

            if(nome.isEmpty() || valVenda.isEmpty()) {
                Toast.makeText(this, "Nome e Valor são obrigatórios!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Inicia o envio em segundo plano
            new Thread(() -> registrarVendaNoBackend(nome, cpf, email, valVenda, valRecebido)).start();
        });
    }

    private void registrarVendaNoBackend(String nome, String cpf, String email, String valVenda, String valRecebido) {
        try {
            // SEU ENDEREÇO LOCALHOST PARA O ANDROID
            URL url = new URL("http://10.0.2.2:3001/vendas");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setDoOutput(true);

            // --- MONTANDO O JSON DA VENDA ---
            JSONObject jsonVenda = new JSONObject();
            jsonVenda.put("usuarioId", 1); // ID do Lojista (Hugo) fixo por enquanto
            jsonVenda.put("nome_cliente", nome);
            jsonVenda.put("cpf_cliente", cpf);
            jsonVenda.put("email_cliente", email);

            // Converte String para Double (para o banco aceitar números)
            jsonVenda.put("valor_venda", Double.parseDouble(valVenda.replace(",", ".")));
            jsonVenda.put("valor_recebido", Double.parseDouble(valRecebido.replace(",", ".")));

            // --- SIMULANDO ITENS (Já que não temos tela de carrinho ainda) ---
            JSONArray arrayItens = new JSONArray();

            JSONObject item1 = new JSONObject();
            item1.put("nome", "Produto Genérico");
            item1.put("quantidade", 1);
            item1.put("valor", Double.parseDouble(valVenda.replace(",", ".")));

            arrayItens.put(item1);
            jsonVenda.put("itens", arrayItens);

            // --- ENVIANDO ---
            Log.d("SalesBuddy", "Enviando JSON: " + jsonVenda.toString());

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonVenda.toString());
            os.flush();
            os.close();

            // --- RESPOSTA ---
            int codigo = conn.getResponseCode();
            if (codigo == 200) {
                runOnUiThread(() -> {
                    Toast.makeText(VendaActivity.this, "✅ VENDA REGISTRADA!", Toast.LENGTH_LONG).show();
                    limparCampos();
                });
            } else {
                Log.e("SalesBuddy", "Erro Backend: " + codigo);
                runOnUiThread(() -> Toast.makeText(VendaActivity.this, "Erro ao registrar venda.", Toast.LENGTH_LONG).show());
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SalesBuddy", "Erro App: " + e.getMessage());
        }
    }

    private void limparCampos() {
        etNome.setText("");
        etCpf.setText("");
        etValorVenda.setText("");
        etValorRecebido.setText("");
    }
}