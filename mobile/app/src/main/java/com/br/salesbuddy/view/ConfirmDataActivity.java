package com.br.salesbuddy.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.ConfirmDataContract;
import com.br.salesbuddy.presenter.ConfirmDataPresenter;
import com.br.salesbuddy.view.adapter.ConfirmItemsAdapter; // Import do novo Adapter

import java.util.List;

public class ConfirmDataActivity extends AppCompatActivity implements ConfirmDataContract.View {

    private TextView tvNome, tvCpf, tvEmail, tvValor, tvRecebido, tvTroco;
    private RecyclerView rvItens;
    private Button btnConfirmar, btnAlterar;
    private ImageView btnBack, btnMenu;

    private ConfirmDataPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_data);

        setupWindowInsets();
        initViews();

        presenter = new ConfirmDataPresenter(this, this);

        if (getIntent().getExtras() != null) {
            presenter.loadInitialData(getIntent().getExtras());
        }

        setupListeners();
    }

    private void initViews() {
        tvNome = findViewById(R.id.tvResumoNome);
        tvCpf = findViewById(R.id.tvResumoCpf);
        tvEmail = findViewById(R.id.tvResumoEmail);

        // Configuração do RecyclerView
        rvItens = findViewById(R.id.rvResumoItens);
        rvItens.setLayoutManager(new LinearLayoutManager(this));

        tvValor = findViewById(R.id.tvResumoValor);
        tvRecebido = findViewById(R.id.tvResumoRecebido);
        tvTroco = findViewById(R.id.tvResumoTroco);

        btnConfirmar = findViewById(R.id.btnConfirmarEnvio);
        btnAlterar = findViewById(R.id.btnConfirmarEnvio2); // Botão Alterar
        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnAlterar.setOnClickListener(v -> finish()); // Botão Alterar volta para editar

        btnConfirmar.setOnClickListener(v -> {
            int idDoUsuario = getIntent().getIntExtra("ID_DO_LOJISTA", -1);

            Log.d("DEBUG_VENDA", "CLICOU EM FINALIZAR! O ID DO USUÁRIO AQUI É: " + idDoUsuario);

            presenter.confirmSale();
        });

        btnMenu.setOnClickListener(v -> {
            int userId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);
            MenuBottomSheetActivity menu = MenuBottomSheetActivity.newInstance(userId, false);
            menu.show(getSupportFragmentManager(), "MenuBottomSheet");
        });
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    @Override
    public void showLoading() {
        btnConfirmar.setEnabled(false);
        btnConfirmar.setText("ENVIANDO...");
    }

    @Override
    public void hideLoading() {
        btnConfirmar.setEnabled(true);
        btnConfirmar.setText("FINALIZAR");

    }

    @Override
    public void displayData(String nome, String cpf, String email, List<String> items,
                            String valorVenda, String valorRecebido, String troco) {
        tvNome.setText(nome);
        tvCpf.setText(cpf);
        tvEmail.setText(email);
        tvValor.setText(valorVenda);
        tvRecebido.setText(valorRecebido);
        tvTroco.setText(troco);

        // Configura o Adapter com a lista já processada pelo Presenter
        ConfirmItemsAdapter adapter = new ConfirmItemsAdapter(items);
        rvItens.setAdapter(adapter);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void navigateToConnectionError() {
        Intent intent = new Intent(this, ConnectionErrorActivity.class);
        startActivity(intent);
    }

    @Override
    public void showError(String error) {
        runOnUiThread(() ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        );
        Log.e("SalesBuddy", "Erro: " + error);
    }

    @Override
    public void navigateToFinalization(Bundle finalData) {
        Intent intent = new Intent(this, FinalizationActivity.class);
        intent.putExtras(finalData);
        // Limpa a pilha para não voltar para o confirmar
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}