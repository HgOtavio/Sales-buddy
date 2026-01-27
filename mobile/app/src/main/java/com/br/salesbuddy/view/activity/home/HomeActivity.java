package com.br.salesbuddy.view.activity.home;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.home.HomeContract;
import com.br.salesbuddy.network.service.AuthService;
import com.br.salesbuddy.presenter.home.HomePresenter;
import com.br.salesbuddy.utils.validation.NetworkUtils;
import com.br.salesbuddy.view.activity.common.ConnectionErrorActivity;
import com.br.salesbuddy.view.activity.menu.MenuBottomSheetActivity;
import com.br.salesbuddy.view.activity.reprocessing.ReprocessingActivity;
import com.br.salesbuddy.view.activity.sales.RegisterSalesActivity;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {

    private Button btnRegisterSales, btnReprocess;
    private ImageView ivTopIcon;
    private HomePresenter presenter;
    private AuthService authService;

    // Variáveis para o monitoramento em tempo real
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        authService = new AuthService();

        setupWindowInsets();
        initViews();

        presenter = new HomePresenter(this);

        if (getIntent().getExtras() != null) {
            int userId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);
            presenter.setUserId(userId);
        }

        setupListeners();
        setupNetworkListener();
    }

    // Registra o vigia quando a tela está visível
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

    // Desliga o vigia quando o usuário sai do app (economiza bateria)
    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    private void initViews() {
        btnRegisterSales = findViewById(R.id.btn_register_sales);
        btnReprocess = findViewById(R.id.btn_reprocess);
        ivTopIcon = findViewById(R.id.iv_top_icon);
    }

    private void setupListeners() {
        btnRegisterSales.setOnClickListener(v -> presenter.onRegisterSalesClicked());
        btnReprocess.setOnClickListener(v -> presenter.onReprocessClicked());
        ivTopIcon.setOnClickListener(v -> presenter.onMenuClicked());
    }

    private void setupNetworkListener() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                // Se a rede for perdida, muda de tela na hora (usando a Thread principal da UI)
                runOnUiThread(() -> navigateToConnectionError());
            }
        };
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void navigateToRegisterSales(int userId) {
        Intent intent = new Intent(HomeActivity.this, RegisterSalesActivity.class);
        intent.putExtra("ID_DO_LOJISTA", userId);
        startActivity(intent);
    }

    @Override
    public void navigateToReprocessing(int userId) {
        Intent intent = new Intent(HomeActivity.this, ReprocessingActivity.class);
        intent.putExtra("ID_DO_LOJISTA", userId);
        startActivity(intent);
    }

    @Override
    public void showMenu(int userId) {
        MenuBottomSheetActivity menu = MenuBottomSheetActivity.newInstance(userId, false);
        menu.show(getSupportFragmentManager(), "MenuBottomSheet");
    }

    @Override
    public void navigateToConnectionError() {
        Intent intent = new Intent(this, ConnectionErrorActivity.class);
        startActivity(intent);
    }
}