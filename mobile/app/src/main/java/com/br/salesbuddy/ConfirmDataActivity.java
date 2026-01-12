package com.br.salesbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConfirmDataActivity extends AppCompatActivity {

    // Componentes da Interface
    private TextView tvNome, tvCpf, tvEmail, tvItem, tvValor, tvRecebido, tvTroco;
    private Button btnConfirmar, btnMenu;
    private ImageView btnBack;

    // Variáveis para armazenar os dados recebidos
    private int usuarioId;
    private String nome, cpf, email, item;
    private double valorVenda, valorRecebido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_data);

        // Configuração de tela cheia (EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Vincular componentes do XML com o Java
        tvNome = findViewById(R.id.tvResumoNome);
        tvCpf = findViewById(R.id.tvResumoCpf);
        tvEmail = findViewById(R.id.tvResumoEmail);
        tvItem = findViewById(R.id.tvResumoItem);
        tvValor = findViewById(R.id.tvResumoValor);
        tvRecebido = findViewById(R.id.tvResumoRecebido);
        tvTroco = findViewById(R.id.tvResumoTroco);

        btnConfirmar = findViewById(R.id.btnConfirmarEnvio);
        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);


        recuperarDadosDaIntent();

        preencherTela();

        btnBack.setOnClickListener(v -> finish());

        btnConfirmar.setOnClickListener(v -> {
            new Thread(this::enviarParaBackend).start();
        });
        btnMenu.setOnClickListener(v -> {
            MenuBottomSheet menu = MenuBottomSheet.newInstance(usuarioId, false);
            menu.show(getSupportFragmentManager(), "MenuBottomSheet");
        });

    }

    private void recuperarDadosDaIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            usuarioId = extras.getInt("ID_DO_LOJISTA", -1);
            nome = extras.getString("NOME");
            cpf = extras.getString("CPF");
            email = extras.getString("EMAIL");
            item = extras.getString("ITEM");

            try {
                String strVenda = extras.getString("VALOR_VENDA", "0").replace(",", ".");
                String strReceb = extras.getString("VALOR_RECEBIDO", "0").replace(",", ".");

                if (strReceb.isEmpty()) strReceb = "0";

                valorVenda = Double.parseDouble(strVenda);
                valorRecebido = Double.parseDouble(strReceb);
            } catch (Exception e) {
                valorVenda = 0.0;
                valorRecebido = 0.0;
            }
        }
    }

    private void preencherTela() {
        tvNome.setText(nome == null || nome.isEmpty() ? "Não informado" : nome);
        tvCpf.setText(cpf == null || cpf.isEmpty() ? "-" : cpf);
        tvEmail.setText(email == null || email.isEmpty() ? "-" : email);
        tvItem.setText(item == null || item.isEmpty() ? "Item Diverso" : item);

        tvValor.setText(String.format("R$ %.2f", valorVenda));
        tvRecebido.setText(String.format("R$ %.2f", valorRecebido));



        double troco = valorRecebido - valorVenda;
        if (troco < 0) troco = 0.0;

        tvTroco.setText(String.format("R$ %.2f", troco));
    }

    private void enviarParaBackend() {
        try {
            URL url = new URL("http://10.0.2.2:3001/vendas");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setDoOutput(true);

            JSONObject jsonVenda = new JSONObject();
            jsonVenda.put("usuarioId", usuarioId);
            jsonVenda.put("nome_cliente", nome);
            jsonVenda.put("cpf_cliente", cpf);
            jsonVenda.put("email_cliente", email);
            jsonVenda.put("valor_venda", valorVenda);
            jsonVenda.put("valor_recebido", valorRecebido);

            JSONArray arrayItens = new JSONArray();
            JSONObject itemObj = new JSONObject();
            itemObj.put("nome", item == null || item.isEmpty() ? "Venda App" : item);
            itemObj.put("quantidade", 1);
            itemObj.put("valor", valorVenda);

            arrayItens.put(itemObj);
            jsonVenda.put("itens", arrayItens);

            Log.d("SalesBuddy", "Enviando JSON: " + jsonVenda.toString());

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.writeBytes(jsonVenda.toString());
            os.flush();
            os.close();

            int codigo = conn.getResponseCode();

            if (codigo == 200 || codigo == 201) {
                runOnUiThread(() -> {
                    Toast.makeText(ConfirmDataActivity.this, " Venda Finalizada com Sucesso!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(ConfirmDataActivity.this, FinalizationActivity.class);

                    intent.putExtra("NOME", nome);
                    intent.putExtra("CPF", cpf);
                    intent.putExtra("EMAIL", email);
                    intent.putExtra("ITEM", item);
                    intent.putExtra("VALOR_VENDA", valorVenda);
                    intent.putExtra("VALOR_RECEBIDO", valorRecebido);


                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
            } else {
                Log.e("SalesBuddy", "Erro Backend: " + codigo);
                runOnUiThread(() -> Toast.makeText(ConfirmDataActivity.this, "Erro no servidor: " + codigo, Toast.LENGTH_SHORT).show());
            }

            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("SalesBuddy", "Erro Conexão: " + e.getMessage());
            runOnUiThread(() -> Toast.makeText(ConfirmDataActivity.this, "Erro de Conexão com a Internet", Toast.LENGTH_SHORT).show());
        }
    }
}