package com.br.salesbuddy.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log; // <-- IMPORT DO LOG ADICIONADO
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.salesbuddy.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

        String hintDinamico = String.format("ITEM %02d", position + 1);
        holder.etItem.setHint(hintDinamico);

        // Remove listener antigo
        if (holder.textWatcher != null) {
            holder.etItem.removeTextChangedListener(holder.textWatcher);
        }

        // Define texto
        holder.etItem.setText(items.get(position));

        // Novo listener para salvar texto
        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int currentPos = holder.getAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    items.set(currentPos, s.toString());

                    // 🔥 DEBUG: Vendo o que está sendo digitado em tempo real
                    Log.d("DEBUG_VENDA", "Item [" + (currentPos+1) + "] atualizado para: " + s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        holder.etItem.addTextChangedListener(holder.textWatcher);

        // Configura Botões
        setupButtonLogic(holder.btnAction, position, holder);
    }

    private void setupButtonLogic(MaterialButton btn, int position, ViewHolder holder) {
        if (btn == null) return;

        boolean isLastItem = position == items.size() - 1;

        btn.setOnClickListener(null);

        if (isLastItem) {
            btn.setIconResource(R.drawable.ic_plus);
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#A32C65"))); // Rosa

            btn.setOnClickListener(v -> {
                int posAntiga = items.size() - 1;
                items.add("");
                notifyItemInserted(items.size() - 1);

                notifyItemChanged(posAntiga);
            });
        } else {
            btn.setIconResource(R.drawable.ic_minus);
            btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#074A82"))); // Azul

            btn.setOnClickListener(v -> {
                int currentPos = holder.getAdapterPosition();

                if (currentPos != RecyclerView.NO_POSITION && items.size() > currentPos) {
                    String removido = items.get(currentPos);
                    items.remove(currentPos);

                    // 🔥 DEBUG: Vendo o que foi removido
                    Log.d("DEBUG_VENDA", "Item removido: " + removido);

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

        String resultadoFinal = builder.toString();



        return resultadoFinal;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextInputLayout layoutItem;
        TextInputEditText etItem;
        MaterialButton btnAction;
        TextWatcher textWatcher;

        @SuppressLint("WrongViewCast")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layoutItem);
            etItem = itemView.findViewById(R.id.etItem);

            View tempBtn = itemView.findViewById(R.id.btn_action);
            if (tempBtn instanceof MaterialButton) {
                btnAction = (MaterialButton) tempBtn;
            } else {
                btnAction = null;
            }
        }
    }
}