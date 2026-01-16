package com.br.salesbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.br.salesbuddy.view.RegisterSalesActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnRegisterSales, btnReprocess;
    private ImageView ivTopIcon;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);

        btnRegisterSales = findViewById(R.id.btn_register_sales);
        btnReprocess = findViewById(R.id.btn_reprocess);
        ivTopIcon = findViewById(R.id.iv_top_icon);

        btnRegisterSales.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RegisterSalesActivity.class);
            intent.putExtra("ID_DO_LOJISTA", userId);
            startActivity(intent);
        });

        btnReprocess.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, reprocessingAcitivity.class);
            intent.putExtra("ID_DO_LOJISTA", userId);
            startActivity(intent);
        });

        ivTopIcon.setOnClickListener(v -> {
            MenuBottomSheet menu = MenuBottomSheet.newInstance(userId, false);
            menu.show(getSupportFragmentManager(), "MenuBottomSheet");
        });
    }
}