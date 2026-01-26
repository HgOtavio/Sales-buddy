package com.br.salesbuddy.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.ReprocessingContract;
import com.br.salesbuddy.model.ReprocessSaleData;
import com.br.salesbuddy.network.AuthService;
import com.br.salesbuddy.presenter.ReprocessingPresenter;
import com.br.salesbuddy.utils.NetworkUtils;
import com.br.salesbuddy.view.adapter.ReprocessingAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReprocessingActivity extends AppCompatActivity implements ReprocessingContract.View {

    private ReprocessingContract.Presenter presenter;
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

        authService = new AuthService();

        recyclerView = findViewById(R.id.rv_reprocessing_list);
        ImageView btnBack = findViewById(R.id.btn_back);
        ImageView btnMenu = findViewById(R.id.btn_menu);
        btnReprocessar = findViewById(R.id.btnFinalizar);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(R.color.black);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReprocessingAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        presenter = new ReprocessingPresenter(this);

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
                if (presenter != null) presenter.loadPendingSales(userId);
            });
        }

        btnReprocessar.setOnClickListener(v -> presenter.onReprocessAllClicked());

        presenter.loadPendingSales(userId);

        setupNetworkListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!NetworkUtils.isConnected(this)) {
            navigateToConnectionError();
            return;
        }

        if (authService != null) {
            authService.validateSession(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    private void setupNetworkListener() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                runOnUiThread(() -> navigateToConnectionError());
            }
        };
    }

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

    public void clearList() {
        if (adapter != null) {
            adapter.clearList();
        }
    }

    @Override
    public void removeItemFromList(int id) {
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