package com.br.salesbuddy.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.salesbuddy.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<String> items;
    private final Context context;

    public ProductAdapter(Context context) {
        this.context = context;
        this.items = new ArrayList<>();
        this.items.add(""); // Inicia com um item vazio
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_produto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 1. Remove listener antigo
        if (holder.textWatcher != null) {
            holder.etItem.removeTextChangedListener(holder.textWatcher);
        }

        // 2. Define texto
        holder.etItem.setText(items.get(position));

        // 3. Novo listener para salvar texto
        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentPos = holder.getAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    items.set(currentPos, s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        holder.etItem.addTextChangedListener(holder.textWatcher);

        // 4. Configura Botões
        setupButtonLogic(holder.btnAction, position, holder);
    }

    private void setupButtonLogic(MaterialButton btn, int position, ViewHolder holder) {
        // Proteção contra crash se o botão for nulo (caso o XML esteja errado)
        if (btn == null) return;

        boolean isLastItem = position == items.size() - 1;

        // Limpa listeners antigos
        btn.setOnClickListener(null);

        if (isLastItem) {
            // --- Botão ADICIONAR (+) (Último item) ---
            btn.setIconResource(R.drawable.ic_plus);
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A32C65"))); // Rosa

            btn.setOnClickListener(v -> {
                int posAntiga = items.size() - 1; // Pega a posição desse item antes de adicionar o novo

                items.add(""); // Adiciona nova linha vazia

                // 1. Avisa que entrou um novo item no final
                notifyItemInserted(items.size() - 1);

                notifyItemChanged(posAntiga);

                // (Opcional) Faz a lista rolar para o novo item
                // if (context instanceof RecyclerView) ((RecyclerView)context).smoothScrollToPosition(items.size() - 1);
            });
        } else {
            // --- Botão REMOVER (-) (Itens anteriores) ---
            btn.setIconResource(R.drawable.ic_minus);
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#074A82"))); // Azul

            btn.setOnClickListener(v -> {
                int currentPos = holder.getAdapterPosition();

                if (currentPos != RecyclerView.NO_POSITION && items.size() > currentPos) {
                    items.remove(currentPos);
                    notifyItemRemoved(currentPos);
                    notifyItemRangeChanged(currentPos, items.size());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public String getItemsConcatenated() {
        StringBuilder builder = new StringBuilder();
        for (String item : items) {
            if (item != null && !item.trim().isEmpty()) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(item.trim());
            }
        }
        return builder.toString();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextInputEditText etItem;
        MaterialButton btnAction;
        TextWatcher textWatcher;

        @SuppressLint("WrongViewCast")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            etItem = itemView.findViewById(R.id.etItem);

            // Verificação de segurança para evitar NullPointerException
            View tempBtn = itemView.findViewById(R.id.btn_action);
            if (tempBtn instanceof MaterialButton) {
                btnAction = (MaterialButton) tempBtn;
            } else {
                btnAction = null;
            }
        }
    }
}