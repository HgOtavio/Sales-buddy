package com.br.salesbuddy.view;

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

import com.br.salesbuddy.MenuBottomSheet;
import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.ConfirmDataContract;
import com.br.salesbuddy.presenter.ConfirmDataPresenter;

public class ConfirmDataActivity extends AppCompatActivity implements ConfirmDataContract.View {

    // Componentes de UI
    private TextView tvNome, tvCpf, tvEmail, tvItem, tvValor, tvRecebido, tvTroco;
    private Button btnConfirmar;
    private ImageView btnBack, btnMenu;

    // Presenter
    private ConfirmDataPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_data);

        // 1. Setup UI
        setupWindowInsets();
        initViews();

        // 2. Inicializa Presenter
        presenter = new ConfirmDataPresenter(this, this);

        // 3. Carrega Dados
        if (getIntent().getExtras() != null) {
            presenter.loadInitialData(getIntent().getExtras());
        }

        // 4. Listeners
        btnBack.setOnClickListener(v -> finish());

        // O Presenter vai chamar o serviço e depois navegar
        btnConfirmar.setOnClickListener(v -> presenter.confirmSale());

        btnMenu.setOnClickListener(v -> {
            int userId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);
            MenuBottomSheet menu = MenuBottomSheet.newInstance(userId, false);
            menu.show(getSupportFragmentManager(), "MenuBottomSheet");
        });
    }

    private void initViews() {
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
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // --- MVP Implementation ---

    @Override
    public void showLoading() {
        btnConfirmar.setEnabled(false);
        btnConfirmar.setText("Enviando...");
    }

    @Override
    public void hideLoading() {
        btnConfirmar.setEnabled(true);
        btnConfirmar.setText("Confirmar Venda");
    }

    @Override
    public void displayData(String nome, String cpf, String email, String item,
                            String valorVenda, String valorRecebido, String troco) {
        tvNome.setText(nome);
        tvCpf.setText(cpf);
        tvEmail.setText(email);
        tvItem.setText(item);
        tvValor.setText(valorVenda);
        tvRecebido.setText(valorRecebido);
        tvTroco.setText(troco);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError(String error) {
        runOnUiThread(() ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        );
        Log.e("SalesBuddy", "❌ ERRO CAPTURADO NA VIEW: " + error);
    }

    @Override
    public void navigateToFinalization(Bundle finalData) {
        Intent intent = new Intent(this, FinalizationActivity.class);
        intent.putExtras(finalData);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}