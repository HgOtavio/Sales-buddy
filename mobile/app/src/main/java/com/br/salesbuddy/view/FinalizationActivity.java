package com.br.salesbuddy.view;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.FinalizationContract;
import com.br.salesbuddy.network.AuthService;
import com.br.salesbuddy.presenter.FinalizationPresenter;
import com.br.salesbuddy.utils.NetworkUtils;
import com.br.salesbuddy.view.adapter.ReceiptItemsAdapter;

import java.util.List;

public class FinalizationActivity extends AppCompatActivity implements FinalizationContract.View {

    private TextView tvNome, tvCpf, tvEmail, tvTotal, tvPago, tvTroco, tvVendaId;
    private RecyclerView rvReceiptItems;
    private Button btnSim, btnNao;
    private ImageView btnMenu, btnBack;
    private SwipeRefreshLayout swipeRefresh;

    private FinalizationPresenter presenter;
    private AuthService authService;
    private int currentUserId;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finalization);

        authService = new AuthService();

        setupWindowInsets();
        initViews();

        presenter = new FinalizationPresenter(this, this);

        if (getIntent().getExtras() != null) {
            currentUserId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);
            presenter.loadData(getIntent().getExtras());
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

    private void initViews() {
        tvNome = findViewById(R.id.tvResumoNome);
        tvCpf = findViewById(R.id.tvResumoCpf);
        tvEmail = findViewById(R.id.tvResumoEmail);

        rvReceiptItems = findViewById(R.id.rvReceiptItems);
        rvReceiptItems.setLayoutManager(new LinearLayoutManager(this));

        tvTotal = findViewById(R.id.tvResumoValor);
        tvPago = findViewById(R.id.tvResumoRecebido);
        tvTroco = findViewById(R.id.tvResumoTroco);
        tvVendaId = findViewById(R.id.tvVendaId);

        btnSim = findViewById(R.id.btnSim);
        btnNao = findViewById(R.id.btnNao);

        btnMenu = findViewById(R.id.btn_menu);
        btnBack = findViewById(R.id.btn_back);

        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(R.color.black);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> presenter.onBackClicked());

        btnMenu.setOnClickListener(v -> {
            MenuBottomSheetActivity menu = MenuBottomSheetActivity.newInstance(currentUserId, false);
            menu.show(getSupportFragmentManager(), "MenuBottomSheet");
        });

        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
            if (authService != null) {
                authService.validateSession(this);
            }
        });

        btnSim.setOnClickListener(v -> presenter.onSendEmailClicked());

        btnNao.setOnClickListener(v -> presenter.onNewSaleClicked());
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

    @Override
    public void showReceiptData(String name, String cpf, String email, List<String> items,
                                String total, String paid, String change, String saleId) {
        tvNome.setText(name);
        tvCpf.setText(cpf);
        tvEmail.setText(email);

        ReceiptItemsAdapter adapter = new ReceiptItemsAdapter(items);
        rvReceiptItems.setAdapter(adapter);

        tvTotal.setText(total);
        tvPago.setText(paid);
        tvTroco.setText(change);
        tvVendaId.setText(saleId);
    }


    private void showCustomToast(String message, boolean isSuccess) {
        runOnUiThread(() -> {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

            TextView text = layout.findViewById(R.id.tvToastMessage);
            text.setText(message);

            // Troca o fundo dependendo se é sucesso ou erro
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
    public void showLoading(String message) {
        showCustomToast(message, true);
        btnSim.setEnabled(false);
        btnNao.setEnabled(false);
    }

    @Override
    public void hideLoading() {
        btnSim.setEnabled(true);
        btnNao.setEnabled(true);
    }

    @Override
    public void showMessage(String message) {
        showCustomToast(message, true);
    }

    @Override
    public void showError(String error) {
        showCustomToast(error, false);
    }

    @Override
    public void navigateToNewSale(int userId) {
        Intent intent = new Intent(FinalizationActivity.this, RegisterSalesActivity.class);
        intent.putExtra("ID_DO_LOJISTA", userId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void closeActivity() {
        finish();
    }

    @Override
    public void showEmailSuccessDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String mensagemHtml = "<font color='#074A82'> COMPROVANTE ENVIADO COM SUCESSO PARA O E-MAIL:</font> <br><br>" +
                "<b><font color='#074A82'>" + email.toUpperCase() + "</font></b>";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setMessage(Html.fromHtml(mensagemHtml, Html.FROM_HTML_MODE_LEGACY));
        } else {
            builder.setMessage(Html.fromHtml(mensagemHtml));
        }

        AlertDialog dialog = builder.create();

        dialog.setOnDismissListener(d -> presenter.onNewSaleClicked());

        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.85f;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                window.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
                lp.setBlurBehindRadius(30);
            }

            window.setAttributes(lp);
        }

        TextView messageView = dialog.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setOnClickListener(v -> dialog.dismiss());
            messageView.setTextSize(18);
        }
    }

    @Override
    public void navigateToConnectionError() {
        Intent intent = new Intent(this, ConnectionErrorActivity.class);
        startActivity(intent);
    }
}