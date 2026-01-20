package com.br.salesbuddy.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.HomeContract;
import com.br.salesbuddy.presenter.HomePresenter;

public class HomeActivity extends AppCompatActivity implements HomeContract.View {

    private Button btnRegisterSales, btnReprocess;
    private ImageView ivTopIcon;
    private HomePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        setupWindowInsets();
        initViews();


        presenter = new HomePresenter(this);

        // Pega o ID e passa para o Presenter
        if (getIntent().getExtras() != null) {
            int userId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);
            presenter.setUserId(userId);
        }

        setupListeners();
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