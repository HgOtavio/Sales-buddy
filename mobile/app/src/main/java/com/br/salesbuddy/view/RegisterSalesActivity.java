package com.br.salesbuddy.view;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.RegisterSalesContract;
import com.br.salesbuddy.network.AuthService;
import com.br.salesbuddy.presenter.RegisterSalesPresenter;
import com.br.salesbuddy.utils.MaskUtils;
import com.br.salesbuddy.utils.NetworkUtils;
import com.br.salesbuddy.view.adapter.ProductAdapter;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterSalesActivity extends AppCompatActivity implements RegisterSalesContract.View {

    private EditText etNome, etCpf, etEmail, etValorVenda, etValorRecebido;
    private TextInputLayout layoutEmail;
    private Button btnFinalizar;
    private ImageView btnMenu, btnBack;
    private SwipeRefreshLayout swipeRefresh;

    private RecyclerView rvItems;
    private ProductAdapter productAdapter;

    private int usuarioId;
    private RegisterSalesPresenter presenter;
    private AuthService authService;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registersales);

        authService = new AuthService();

        setupWindowInsets();

        usuarioId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);

        presenter = new RegisterSalesPresenter(this);

        initViews();
        setupRecyclerView();
        setupMasks();
        setupListeners();
        setupNetworkListener(); // Inicia o vigia de rede
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

        // Checagem instantânea da rede
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
        etNome = findViewById(R.id.etNomeCliente);
        etCpf = findViewById(R.id.etCpf);
        etEmail = findViewById(R.id.etEmailCliente);
        layoutEmail = findViewById(R.id.layoutEmail);
        etValorVenda = findViewById(R.id.etValorVenda);
        etValorRecebido = findViewById(R.id.etValorRecebido);

        rvItems = findViewById(R.id.rv_items);

        btnFinalizar = findViewById(R.id.btnFinalizar);
        btnMenu = findViewById(R.id.btn_menu);
        btnBack = findViewById(R.id.btn_back);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(R.color.black);

        etNome.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etCpf.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        etEmail.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        etNome.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                etCpf.requestFocus();
                return true;
            }
            return false;
        });

        etCpf.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                etEmail.requestFocus();
                return true;
            }
            return false;
        });

        etNome.requestFocus();
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

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnMenu.setOnClickListener(v -> {
            MenuBottomSheetActivity menu = MenuBottomSheetActivity.newInstance(usuarioId, true);
            menu.show(getSupportFragmentManager(), "MenuTopSheet");
        });

        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
            layoutEmail.setError(null);
            if (authService != null) {
                authService.validateSession(this);
            }
        });

        btnFinalizar.setOnClickListener(v -> {
            String nome = etNome.getText().toString().trim();
            String cpf = etCpf.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String valVenda = etValorVenda.getText().toString().trim();
            String valRecebido = etValorRecebido.getText().toString().trim();

            if (nome.isEmpty()) {
                etNome.setError("O nome do cliente é obrigatório");
                etNome.requestFocus();
                return;
            }

            if (cpf.isEmpty()) {
                etCpf.setError("O CPF é obrigatório");
                etCpf.requestFocus();
                return;
            } else if (cpf.length() < 14) {
                etCpf.setError("Digite o CPF completo");
                etCpf.requestFocus();
                return;
            }

            if (email.isEmpty()) {
                layoutEmail.setError("O e-mail é obrigatório");
                etEmail.requestFocus();
                return;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                layoutEmail.setError("Digite um e-mail válido");
                etEmail.requestFocus();
                return;
            } else {
                layoutEmail.setError(null);
            }

            String itensConcatenados = productAdapter.getItemsConcatenated();
            if (itensConcatenados.isEmpty()) {
                View primeiraLinhaProduto = rvItems.getLayoutManager().findViewByPosition(0);
                if (primeiraLinhaProduto != null) {
                    EditText etPrimeiroProduto = primeiraLinhaProduto.findViewById(R.id.etItem);
                    if (etPrimeiroProduto != null) {
                        etPrimeiroProduto.setError("Informe o nome do produto");
                        etPrimeiroProduto.requestFocus();
                    }
                } else {
                    showCustomToast("Adicione o nome do produto!", false);
                }
                return;
            }

            double dValorVenda = parseMoneyToDouble(valVenda);
            double dValorRecebido = parseMoneyToDouble(valRecebido);

            if (dValorVenda <= 0) {
                etValorVenda.setError("O valor da venda não pode ser R$ 0,00");
                etValorVenda.requestFocus();
                return;
            }

            if (dValorRecebido < dValorVenda) {
                etValorRecebido.setError("O valor recebido não pode ser menor que o da venda!");
                etValorRecebido.requestFocus();
                return;
            }

            // O Presenter vai verificar no Back-end e, se der erro, chamará o 'showInputError'
            presenter.validateAndAdvance(usuarioId, nome, cpf, email, valVenda, valRecebido, itensConcatenados);
        });
    }

    private double parseMoneyToDouble(String moneyString) {
        if (moneyString == null || moneyString.isEmpty()) return 0.0;
        String cleanString = moneyString.replaceAll("[R$\\s.]", "");
        cleanString = cleanString.replace(",", ".");
        try {
            return Double.parseDouble(cleanString);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(productAdapter);
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
    public void setupMasks() {
        etCpf.addTextChangedListener(MaskUtils.cpfMask(etCpf));
        etValorVenda.addTextChangedListener(MaskUtils.moneyMask(etValorVenda));
        etValorRecebido.addTextChangedListener(MaskUtils.moneyMask(etValorRecebido));

        etEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String email = etEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    layoutEmail.setError("O e-mail é obrigatório");
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    layoutEmail.setError("E-mail inválido");
                } else {
                    layoutEmail.setError(null);
                }
            }
        });
    }

    @Override
    public void showInputError(String message) {
        showCustomToast(message, false);
    }

    @Override
    public void navigateToConnectionError() {
        Intent intent = new Intent(this, ConnectionErrorActivity.class);
        startActivity(intent);
    }

    @Override
    public void navigateToConfirm(Bundle bundle) {
        Intent intent = new Intent(RegisterSalesActivity.this, ConfirmDataActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override public void addDynamicItemRow() {}
    @Override public void convertLastButtonToMinus(View lastView) {}
    @Override public void removeDynamicItemRow(View view) {}
}