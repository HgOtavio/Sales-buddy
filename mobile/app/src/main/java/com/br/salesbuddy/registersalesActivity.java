package com.br.salesbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class registersalesActivity extends AppCompatActivity {

    private EditText etNome, etCpf, etEmail, etItem, etValorVenda, etValorRecebido;
    private Button btnFinalizar;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registersales);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuarioId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);

        etNome = findViewById(R.id.etNomeCliente);
        etCpf = findViewById(R.id.etCpf);
        etEmail = findViewById(R.id.etEmailCliente);
        etItem = findViewById(R.id.etItem);
        etValorVenda = findViewById(R.id.etValorVenda);
        etValorRecebido = findViewById(R.id.etValorRecebido);
        btnFinalizar = findViewById(R.id.btnFinalizar);

        btnFinalizar.setOnClickListener(v -> {
            String nome = etNome.getText().toString();
            String cpf = etCpf.getText().toString();
            String email = etEmail.getText().toString();
            String item = etItem.getText().toString();
            String valVenda = etValorVenda.getText().toString();
            String valRecebido = etValorRecebido.getText().toString();

            if (valVenda.isEmpty()) {
                Toast.makeText(this, "O Valor da venda é obrigatório!", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(registersalesActivity.this, ConfirmDataActivity.class);
            intent.putExtra("ID_DO_LOJISTA", usuarioId);
            intent.putExtra("NOME", nome);
            intent.putExtra("CPF", cpf);
            intent.putExtra("EMAIL", email);
            intent.putExtra("ITEM", item);
            intent.putExtra("VALOR_VENDA", valVenda);
            intent.putExtra("VALOR_RECEBIDO", valRecebido);

            startActivity(intent);
        });
    }
}