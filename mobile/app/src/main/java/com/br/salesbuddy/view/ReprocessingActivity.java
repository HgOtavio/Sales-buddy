package com.br.salesbuddy.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button; // Import do Button
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.ReprocessingContract;
import com.br.salesbuddy.model.ReprocessSaleData;
import com.br.salesbuddy.presenter.ReprocessingPresenter;
import com.br.salesbuddy.view.adapter.ReprocessingAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReprocessingActivity extends AppCompatActivity implements ReprocessingContract.View {

    private ReprocessingContract.Presenter presenter;
    private RecyclerView recyclerView;
    private ReprocessingAdapter adapter;
    private ProgressBar progressBar;
    private Button btnReprocessar; // Botão do rodapé

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reprocessing);

        // 1. Bind Views
        recyclerView = findViewById(R.id.rv_reprocessing_list);
        ImageView btnBack = findViewById(R.id.btn_back);
        ImageView btnMenu = findViewById(R.id.btn_menu);

        // --- AQUI ESTÁ O BOTÃO DA TELA PAI ---
        btnReprocessar = findViewById(R.id.btnFinalizar);

        // progressBar = findViewById(R.id.progressBar); // Descomente se tiver

        // 2. Configura Adapter (Sem listener de clique, só visualização)
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReprocessingAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // 3. Inicializa Presenter
        presenter = new ReprocessingPresenter(this);

        int userId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);

        // 4. Configura Cliques
        btnBack.setOnClickListener(v -> presenter.onBackClicked());

        btnMenu.setOnClickListener(v -> {
            MenuBottomSheetActivity menu = MenuBottomSheetActivity.newInstance(userId, false);
            menu.show(getSupportFragmentManager(), "MenuBottomSheet");
        });

        // --- CLIQUE DO BOTÃO GRANDE ---
        // Quando clicar aqui, o Presenter processa tudo e mostra o Dialog
        btnReprocessar.setOnClickListener(v -> {
            presenter.onReprocessAllClicked();
        });

        // 5. Carrega dados
        presenter.loadPendingSales(userId);
    }

    @Override
    public void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        // Opcional: Desabilitar o botão para não clicar 2x
        btnReprocessar.setEnabled(false);
        btnReprocessar.setText("PROCESSANDO...");
    }

    @Override
    public void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        btnReprocessar.setEnabled(true);
        btnReprocessar.setText("REPROCESSAR");
    }

    @Override
    public void showList(List<ReprocessSaleData> list) {
        // Atualiza o adapter com a nova lista
        adapter = new ReprocessingAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    // Método para limpar a tela após sucesso
    public void clearList() {
        if (adapter != null) {
            adapter.clearList();
        }
    }

    @Override
    public void removeItemFromList(int id) {
        // Não usado mais nessa lógica de lote, mas mantido pelo contrato
    }

    @Override
    public void showEmptyState() {
        Toast.makeText(this, "Lista vazia ou finalizada.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToConnectionError() {
        Intent intent = new Intent(this, ConnectionErrorActivity.class);
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        // SEU DIALOG PERSONALIZADO
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_reprocess_success);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        dialog.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }, 2000);
    }

    @Override
    public void navigateBack() {
        finish();
    }
}