package com.br.salesbuddy.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.salesbuddy.R;

import java.util.List;
import java.util.Locale;

public class ConfirmItemsAdapter extends RecyclerView.Adapter<ConfirmItemsAdapter.ViewHolder> {

    private final List<String> items;

    public ConfirmItemsAdapter(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_confirm_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String itemText = items.get(position);

        // 1. Gera o ID visual baseado na posição da lista (0 -> 01, 1 -> 02)
        holder.tvItemIndex.setText(String.format(Locale.getDefault(), "%02d", position + 1));

        // 2. Define o nome do produto
        holder.tvItemName.setText(itemText);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemIndex;
        TextView tvItemName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Certifique-se que estes IDs existem no XML acima
            tvItemIndex = itemView.findViewById(R.id.tvItemIndex);
            tvItemName = itemView.findViewById(R.id.tvItemName);
        }
    }
}