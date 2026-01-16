package com.br.salesbuddy.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.br.salesbuddy.MenuBottomSheet;
import com.br.salesbuddy.R;
import com.br.salesbuddy.contract.RegisterSalesContract;
import com.br.salesbuddy.presenter.RegisterSalesPresenter;
import com.br.salesbuddy.utils.MaskUtils;

public class RegisterSalesActivity extends AppCompatActivity implements RegisterSalesContract.View {

    // Componentes de UI
    private EditText etNome, etCpf, etEmail, etValorVenda, etValorRecebido;
    private Button btnFinalizar;
    private ImageView btnMenu, btnBack;
    private LinearLayout containerItems;

    // Dados e Lógica
    private int usuarioId;
    private RegisterSalesPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registersales);

        setupWindowInsets();

        // 1. Recuperar ID do Usuário Logado
        usuarioId = getIntent().getIntExtra("ID_DO_LOJISTA", -1);

        // 2. Inicializar Presenter
        presenter = new RegisterSalesPresenter(this);

        // 3. Inicializar Views e Máscaras
        initViews();
        setupMasks(); // <--- AQUI A MÁGICA ACONTECE

        // 4. Adicionar primeiro item dinâmico (para não ficar vazio)
        addDynamicItemRow();

        // 5. Configurar Botões (Listeners)
        btnBack.setOnClickListener(v -> finish());

        btnMenu.setOnClickListener(v -> {
            MenuBottomSheet menu = MenuBottomSheet.newInstance(usuarioId, true);
            menu.show(getSupportFragmentManager(), "MenuTopSheet");
        });

        btnFinalizar.setOnClickListener(v -> {
            // Coleta os dados da tela
            String nome = etNome.getText().toString();
            String cpf = etCpf.getText().toString();
            String email = etEmail.getText().toString();
            String valVenda = etValorVenda.getText().toString();     // Ex: "R$ 1.200,00"
            String valRecebido = etValorRecebido.getText().toString(); // Ex: "R$ 1.200,00"

            // Coleta os itens da lista dinâmica
            String itensConcatenados = getItemsFromContainer();

            // Manda para o Presenter validar e limpar (tirar o R$)
            presenter.validateAndAdvance(usuarioId, nome, cpf, email, valVenda, valRecebido, itensConcatenados);
        });
    }

    private void initViews() {
        etNome = findViewById(R.id.etNomeCliente);
        etCpf = findViewById(R.id.etCpf);
        etEmail = findViewById(R.id.etEmailCliente);
        etValorVenda = findViewById(R.id.etValorVenda);
        etValorRecebido = findViewById(R.id.etValorRecebido);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        containerItems = findViewById(R.id.container_items);
        btnMenu = findViewById(R.id.btn_menu);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // --- Métodos do Contrato MVP (Implementação da View) ---

    @Override
    public void setupMasks() {
        // Aplica as máscaras de CPF e Dinheiro automaticamente
        etCpf.addTextChangedListener(MaskUtils.cpfMask(etCpf));
        etValorVenda.addTextChangedListener(MaskUtils.moneyMask(etValorVenda));
        etValorRecebido.addTextChangedListener(MaskUtils.moneyMask(etValorRecebido));
    }

    @Override
    public void showInputError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToConfirm(Bundle bundle) {
        // Vai para a tela de confirmação levando os dados já limpos pelo Presenter
        Intent intent = new Intent(RegisterSalesActivity.this, ConfirmDataActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // --- Lógica de Itens Dinâmicos (UI) ---

    private String getItemsFromContainer() {
        StringBuilder itemsBuilder = new StringBuilder();
        for (int i = 0; i < containerItems.getChildCount(); i++) {
            View view = containerItems.getChildAt(i);
            EditText etItem = view.findViewById(R.id.etItem);

            if (etItem != null && !etItem.getText().toString().trim().isEmpty()) {
                if (itemsBuilder.length() > 0) {
                    itemsBuilder.append(", ");
                }
                itemsBuilder.append(etItem.getText().toString().trim());
            }
        }
        return itemsBuilder.toString();
    }

    @Override
    public void addDynamicItemRow() {
        // Se já existem itens, o último botão vira "Remover" (-)
        if (containerItems.getChildCount() > 0) {
            View lastView = containerItems.getChildAt(containerItems.getChildCount() - 1);
            convertLastButtonToMinus(lastView);
        }

        // Infla o layout do item (item_produto.xml)
        View view = getLayoutInflater().inflate(R.layout.item_produto, containerItems, false);
        Button btn = view.findViewById(R.id.btn_action);

        // O novo botão sempre é "Adicionar" (+)
        btn.setOnClickListener(v -> addDynamicItemRow());

        containerItems.addView(view);
    }

    @Override
    public void convertLastButtonToMinus(View lastView) {
        Button lastBtn = lastView.findViewById(R.id.btn_action);

        // Troca visual para remover
        lastBtn.setBackgroundResource(R.drawable.ic_minus);
        lastBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#074A82")));

        // Troca ação para remover a linha
        lastBtn.setOnClickListener(v -> removeDynamicItemRow(lastView));
    }

    @Override
    public void removeDynamicItemRow(View view) {
        containerItems.removeView(view);
    }
}