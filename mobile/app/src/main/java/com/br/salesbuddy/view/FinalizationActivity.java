package com.br.salesbuddy.view;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.br.salesbuddy.MenuBottomSheet;
import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.FinalizationContract;
import com.br.salesbuddy.presenter.FinalizationPresenter;

public class FinalizationActivity extends AppCompatActivity implements FinalizationContract.View {

    private TextView tvNome, tvCpf, tvEmail, tvItem, tvTotal, tvPago, tvTroco, tvVendaId;
    private Button btnSim, btnNao;
    private ImageView btnMenu, btnBack;

    private FinalizationPresenter presenter;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_finalization);

        setupWindowInsets();
        initViews();

        presenter = new FinalizationPresenter(this, this);

        if (getIntent().getExtras() != null) {
            currentUserId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);
            presenter.loadData(getIntent().getExtras());
        }

        setupListeners();
    }

    private void initViews() {
        tvNome = findViewById(R.id.tvResumoNome);
        tvCpf = findViewById(R.id.tvResumoCpf);
        tvEmail = findViewById(R.id.tvResumoEmail);
        tvItem = findViewById(R.id.tvResumoItem);
        tvTotal = findViewById(R.id.tvResumoValor);
        tvPago = findViewById(R.id.tvResumoRecebido);
        tvTroco = findViewById(R.id.tvResumoTroco);
        tvVendaId = findViewById(R.id.tvVendaId);

        btnSim = findViewById(R.id.btnSim);
        btnNao = findViewById(R.id.btnNao);

        btnMenu = findViewById(R.id.btn_menu);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> presenter.onBackClicked());

        btnMenu.setOnClickListener(v -> {
            MenuBottomSheet menu = MenuBottomSheet.newInstance(currentUserId, false);
            menu.show(getSupportFragmentManager(), "MenuBottomSheet");
        });

        btnSim.setOnClickListener(v -> presenter.onSendEmailClicked());

        btnNao.setOnClickListener(v -> presenter.onNewSaleClicked());
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void showReceiptData(String name, String cpf, String email, String item,
                                String total, String paid, String change, String saleId) {
        tvNome.setText(name);
        tvCpf.setText(cpf);
        tvEmail.setText(email);
        tvItem.setText(item);
        tvTotal.setText(total);
        tvPago.setText(paid);
        tvTroco.setText(change);
        tvVendaId.setText(saleId);
    }

    @Override
    public void showLoading(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
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
}