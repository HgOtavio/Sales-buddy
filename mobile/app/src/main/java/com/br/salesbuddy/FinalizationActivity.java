package com.br.salesbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class FinalizationActivity extends AppCompatActivity {

    private TextView tvNome, tvCpf, tvEmail, tvItem, tvTotal, tvPago, tvTroco, tvVendaId;
    private Button btnSim, btnNao;

    // Dados para compartilhamento
    private String nome, cpf, email, item;
    private double valorVenda, valorRecebido, troco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finalization);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Vínculo com os IDs novos do XML
        tvNome = findViewById(R.id.tvResumoNome);
        tvCpf = findViewById(R.id.tvResumoCpf);
        tvEmail = findViewById(R.id.tvResumoEmail);
        tvItem = findViewById(R.id.tvResumoItem);
        tvTotal = findViewById(R.id.tvResumoValor);
        tvPago = findViewById(R.id.tvResumoRecebido);
        tvTroco = findViewById(R.id.tvResumoTroco);
        tvVendaId = findViewById(R.id.tvVendaId);
        btnSim = findViewById(R.id.btnSim);
        btnNao = findViewById(R.id.btnNao);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            nome = extras.getString("NOME", "-");
            cpf = extras.getString("CPF", "-");
            email = extras.getString("EMAIL", "-");
            item = extras.getString("ITEM", "-");
            valorVenda = extras.getDouble("VALOR_VENDA", 0.0);
            valorRecebido = extras.getDouble("VALOR_RECEBIDO", 0.0);

            troco = valorRecebido - valorVenda;
            if(troco < 0) troco = 0;

            tvNome.setText(nome);
            tvCpf.setText(cpf.isEmpty() ? "-" : cpf);
            tvEmail.setText(email.isEmpty() ? "-" : email);
            tvItem.setText(item);

            tvTotal.setText(String.format("R$ %.2f", valorVenda));
            tvPago.setText(String.format("R$ %.2f", valorRecebido));
            tvTroco.setText(String.format("R$ %.2f", troco));

            int numeroVenda = new Random().nextInt(900000) + 100000;
            tvVendaId.setText("Venda n° " + numeroVenda);
        }

        btnNao.setOnClickListener(v -> {
            Intent intent = new Intent(FinalizationActivity.this, registersalesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnSim.setOnClickListener(v -> compartilharComprovante());
    }

    private void compartilharComprovante() {
        String reciboTexto =
                " *COMPROVANTE DE VENDA - SALESBUDDY*\n" +
                        "--------------------------------\n" +
                        " Cliente: " + nome + "\n" +
                        " CPF: " + cpf + "\n" +
                        " Item: " + item + "\n" +
                        "--------------------------------\n" +
                        " TOTAL: R$ " + String.format("%.2f", valorVenda) + "\n" +
                        " Pago: R$ " + String.format("%.2f", valorRecebido) + "\n" +
                        " Troco: R$ " + String.format("%.2f", troco) + "\n" +
                        "--------------------------------\n" +
                        "Obrigado pela preferência!";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Comprovante de Venda");
        shareIntent.putExtra(Intent.EXTRA_TEXT, reciboTexto);

        startActivity(Intent.createChooser(shareIntent, "Enviar comprovante via:"));
    }
}