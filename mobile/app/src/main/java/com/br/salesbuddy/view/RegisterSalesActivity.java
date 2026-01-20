package com.br.salesbuddy.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.RegisterSalesContract;
import com.br.salesbuddy.presenter.RegisterSalesPresenter;
import com.br.salesbuddy.utils.MaskUtils;
import com.br.salesbuddy.view.adapter.ProductAdapter;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterSalesActivity extends AppCompatActivity implements RegisterSalesContract.View {

    private EditText etNome, etCpf, etEmail, etValorVenda, etValorRecebido;
    private TextInputLayout layoutEmail;
    private Button btnFinalizar;
    private ImageView btnMenu, btnBack;

    private RecyclerView rvItems;
    private ProductAdapter productAdapter;

    private int usuarioId;
    private RegisterSalesPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registersales);

        setupWindowInsets();

        usuarioId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);

        presenter = new RegisterSalesPresenter(this);

        initViews();
        setupRecyclerView();
        setupMasks();

        btnBack.setOnClickListener(v -> finish());

        btnMenu.setOnClickListener(v -> {
            MenuBottomSheetActivity menu = MenuBottomSheetActivity.newInstance(usuarioId, true);
            menu.show(getSupportFragmentManager(), "MenuTopSheet");
        });

        // --- AQUI ESTÁ A LÓGICA DE VALIDAÇÃO ---
        btnFinalizar.setOnClickListener(v -> {
            // 1. Pegar os textos removendo espaços em branco nas pontas
            String nome = etNome.getText().toString().trim();
            String cpf = etCpf.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String valVenda = etValorVenda.getText().toString().trim();
            String valRecebido = etValorRecebido.getText().toString().trim();

            // 2. Validação: Nome Obrigatório
            if (nome.isEmpty()) {
                etNome.setError("O nome do cliente é obrigatório");
                etNome.requestFocus();
                return; // Para a execução aqui
            }

            // 3. Validação: Valor da Venda (não pode ser vazio ou R$ 0,00)
            if (valVenda.isEmpty() || valVenda.equals("R$ 0,00")) {
                etValorVenda.setError("Informe o valor da venda");
                etValorVenda.requestFocus();
                return;
            }

            // 4. Validação: Valor Recebido
            if (valRecebido.isEmpty() || valRecebido.equals("R$ 0,00")) {
                etValorRecebido.setError("Informe o valor recebido");
                etValorRecebido.requestFocus();
                return;
            }

            // 5. Validação: Email (Apenas se preenchido, verifica formato)
            if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                layoutEmail.setError("Digite um e-mail válido");
                etEmail.requestFocus();
                return;
            } else {
                layoutEmail.setError(null);
            }

            // 6. Validação: Lista de Produtos
            String itensConcatenados = productAdapter.getItemsConcatenated();
            if (itensConcatenados.isEmpty()) {
                Toast.makeText(this, "Adicione pelo menos um item à venda!", Toast.LENGTH_LONG).show();
                return;
            }

            // Se passou por tudo, envia para o Presenter
            presenter.validateAndAdvance(usuarioId, nome, cpf, email, valVenda, valRecebido, itensConcatenados);
        });
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
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(this);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(productAdapter);
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
                if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    layoutEmail.setError("E-mail inválido");
                } else {
                    layoutEmail.setError(null);
                }
            }
        });
    }

    @Override
    public void showInputError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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