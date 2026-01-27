package com.br.salesbuddy.view.activity.reprocessing;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.reprocessing.ReprocessingContract;
import com.br.salesbuddy.model.body.ReprocessSaleData;
import com.br.salesbuddy.network.service.AuthService;
import com.br.salesbuddy.presenter.reprocessing.ReprocessingPresenter;
import com.br.salesbuddy.utils.validation.NetworkUtils;
import com.br.salesbuddy.view.activity.common.ConnectionErrorActivity;
import com.br.salesbuddy.view.activity.menu.MenuBottomSheetActivity;
import com.br.salesbuddy.view.adapter.ReprocessingAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReprocessingActivity extends AppCompatActivity implements ReprocessingContract.View {

    // ... (Mantenha as variáveis globais: presenter, adapter, etc. iguais ao anterior) ...
    private ReprocessingPresenter presenter;
    private RecyclerView recyclerView;
    private ReprocessingAdapter adapter;
    private ProgressBar progressBar;
    private Button btnReprocessar;
    private SwipeRefreshLayout swipeRefresh;
    private AuthService authService;
    private int userId;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reprocessing);

        // ... (Mantenha todo o código do onCreate igual) ...
        authService = new AuthService();
        recyclerView = findViewById(R.id.rv_reprocessing_list);
        ImageView btnBack = findViewById(R.id.btn_back);
        ImageView btnMenu = findViewById(R.id.btn_menu);
        btnReprocessar = findViewById(R.id.btnFinalizar);
        progressBar = findViewById(R.id.progressBar);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        if (swipeRefresh != null) swipeRefresh.setColorSchemeResources(R.color.black);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReprocessingAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        presenter = new ReprocessingPresenter(this, this);
        userId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);

        btnBack.setOnClickListener(v -> presenter.onBackClicked());
        btnMenu.setOnClickListener(v -> {
            MenuBottomSheetActivity menu = MenuBottomSheetActivity.newInstance(userId, false);
            menu.show(getSupportFragmentManager(), "MenuBottomSheet");
        });

        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(() -> {
                swipeRefresh.setRefreshing(false);
                if (authService != null) authService.validateSession(this);
                presenter.loadPendingSales(userId);
            });
        }

        btnReprocessar.setOnClickListener(v -> presenter.onReprocessAllClicked());
        presenter.loadPendingSales(userId);
        setupNetworkListener();
    }

    // ... (showLoading, hideLoading, showList, markItemAsProcessed... mantenha iguais) ...
    @Override
    public void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        btnReprocessar.setEnabled(false);
        btnReprocessar.setText("PROCESSANDO...");
    }

    @Override
    public void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        btnReprocessar.setEnabled(true);
        btnReprocessar.setText("REPROCESSAR");
        if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
    }

    @Override
    public void showList(List<ReprocessSaleData> list) {
        adapter = new ReprocessingAdapter(list);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void markItemAsProcessed(int index) {
        if (adapter != null) adapter.markAsProcessed(index);
    }

    @Override
    public void clearList() { if (adapter != null) adapter.clearList(); }

    @Override
    public void removeItemFromList(int id) {}

    @Override
    public void showEmptyState() {
        showCustomToast("Nenhuma venda pendente.", true);
    }

    @Override
    public void navigateToConnectionError() {
        Intent intent = new Intent(this, ConnectionErrorActivity.class);
        startActivity(intent);
    }


    @Override
    public void showMessage(String message) {
        // Se a mensagem for "Processamento finalizado!", exibimos o Dialog de Sucesso
        showResultDialog(true, null);
    }


    @Override
    public void showErrorDialog(String title, String message) {

        showResultDialog(false, message);
    }

    private void showResultDialog(boolean isSuccess, String errorMessage) {
        runOnUiThread(() -> {
            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_reprocess_success);

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            TextView tvMessage = dialog.findViewById(R.id.tv_dialog_message);

            if (isSuccess) {

                tvMessage.setText("Reprocessamento efetuado com sucesso");

            } else {

                tvMessage.setText(errorMessage != null ? errorMessage : "Erro ao processar.");

            }

            dialog.setCancelable(true); // Clica fora para fechar
            dialog.show();
        });
    }

    // Toast de fallback
    @Override
    public void showError(String error) {
        showCustomToast(error, false);
    }


    @Override
    public void navigateBack() { finish(); }

    @Override
    protected void onResume() {
        super.onResume();
        if (!NetworkUtils.isConnected(this)) {
            navigateToConnectionError();
            return;
        }
        if (authService != null) authService.validateSession(this);
    }

    private void setupNetworkListener() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLost(@NonNull Network network) {
                runOnUiThread(() -> navigateToConnectionError());
            }
        };
        if (connectivityManager != null) connectivityManager.registerDefaultNetworkCallback(networkCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    private void showCustomToast(String message, boolean isSuccess) {
        runOnUiThread(() -> {
            try {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
                TextView text = layout.findViewById(R.id.tvToastMessage);
                text.setText(message);
                layout.setBackgroundResource(isSuccess ? R.drawable.bg_toast_sucesso : R.drawable.bg_toast_erro);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            } catch (Exception e) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}