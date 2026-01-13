package com.br.salesbuddy;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class registersalesActivity extends AppCompatActivity {

    private EditText etNome, etCpf, etEmail, etValorVenda, etValorRecebido;
    private Button btnFinalizar;
    private ImageView btnMenu, btnBack;
    private LinearLayout containerItems;
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
        etValorVenda = findViewById(R.id.etValorVenda);
        etValorRecebido = findViewById(R.id.etValorRecebido);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        containerItems = findViewById(R.id.container_items);

        btnMenu = findViewById(R.id.btn_menu);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());

        btnMenu.setOnClickListener(v -> {
            MenuBottomSheet menu = MenuBottomSheet.newInstance(usuarioId, true);
            menu.show(getSupportFragmentManager(), "MenuTopSheet");
        });

        addNewItem();

        btnFinalizar.setOnClickListener(v -> {
            String nome = etNome.getText().toString();
            String cpf = etCpf.getText().toString();
            String email = etEmail.getText().toString();
            String valVenda = etValorVenda.getText().toString();
            String valRecebido = etValorRecebido.getText().toString();

            StringBuilder itemsBuilder = new StringBuilder();
            for (int i = 0; i < containerItems.getChildCount(); i++) {
                View view = containerItems.getChildAt(i);
                EditText etItem = view.findViewById(R.id.etItem);
                if (etItem != null && !etItem.getText().toString().isEmpty()) {
                    if (itemsBuilder.length() > 0) {
                        itemsBuilder.append(", ");
                    }
                    itemsBuilder.append(etItem.getText().toString());
                }
            }
            String item = itemsBuilder.toString();

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

    private void addNewItem() {
        if (containerItems.getChildCount() > 0) {
            View lastView = containerItems.getChildAt(containerItems.getChildCount() - 1);
            Button lastBtn = lastView.findViewById(R.id.btn_action);

            lastBtn.setBackgroundResource(R.drawable.ic_minus);
            lastBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#074A82")));
            lastBtn.setOnClickListener(v -> containerItems.removeView(lastView));
        }

        View view = getLayoutInflater().inflate(R.layout.item_produto, containerItems, false);
        Button btn = view.findViewById(R.id.btn_action);

        btn.setOnClickListener(v -> addNewItem());

        containerItems.addView(view);
    }
}