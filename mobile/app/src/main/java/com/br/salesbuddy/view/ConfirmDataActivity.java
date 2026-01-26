package com.br.salesbuddy.view;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.ConfirmDataContract;
import com.br.salesbuddy.presenter.ConfirmDataPresenter;
import com.br.salesbuddy.utils.NetworkUtils;
import com.br.salesbuddy.view.adapter.ConfirmItemsAdapter;

import java.util.List;

public class ConfirmDataActivity extends AppCompatActivity implements ConfirmDataContract.View {

    private TextView tvNome, tvCpf, tvEmail, tvValor, tvRecebido, tvTroco;
    private RecyclerView rvItens;
    private Button btnConfirmar, btnAlterar;
    private ImageView btnBack, btnMenu;

    private ConfirmDataPresenter presenter;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

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
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (connectivityManager != null && networkCallback != null) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    private void initViews() {
        tvNome = findViewById(R.id.tvResumoNome);
        tvCpf = findViewById(R.id.tvResumoCpf);
        tvEmail = findViewById(R.id.tvResumoEmail);

        rvItens = findViewById(R.id.rvResumoItens);
        rvItens.setLayoutManager(new LinearLayoutManager(this));

        tvValor = findViewById(R.id.tvResumoValor);
        tvRecebido = findViewById(R.id.tvResumoRecebido);
        tvTroco = findViewById(R.id.tvResumoTroco);

        btnConfirmar = findViewById(R.id.btnConfirmarEnvio);
        btnAlterar = findViewById(R.id.btnConfirmarEnvio2);
        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnAlterar.setOnClickListener(v -> finish());

        btnConfirmar.setOnClickListener(v -> {
            // Segue o fluxo para o Presenter finalizar a venda
            presenter.confirmSale();
        });

        btnMenu.setOnClickListener(v -> {
            int userId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);
            MenuBottomSheetActivity menu = MenuBottomSheetActivity.newInstance(userId, false);
            menu.show(getSupportFragmentManager(), "MenuBottomSheet");
        });
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

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showCustomToast(String message, boolean isSuccess) {
        runOnUiThread(() -> {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

            TextView text = layout.findViewById(R.id.tvToastMessage);
            text.setText(message);

            if (isSuccess) {
                layout.setBackgroundResource(R.drawable.bg_toast_sucesso);
            } else {
                layout.setBackgroundResource(R.drawable.bg_toast_erro);
            }

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
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

        ConfirmItemsAdapter adapter = new ConfirmItemsAdapter(items);
        rvItens.setAdapter(adapter);
    }

    @Override
    public void showMessage(String message) {
        showCustomToast(message, true);
    }

    @Override
    public void navigateToConnectionError() {
        Intent intent = new Intent(this, ConnectionErrorActivity.class);
        startActivity(intent);
    }

    @Override
    public void showError(String error) {
        showCustomToast(error, false);
        Log.e("SalesBuddy", "Erro na API: " + error); // Mantido o log só para registro do dev
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