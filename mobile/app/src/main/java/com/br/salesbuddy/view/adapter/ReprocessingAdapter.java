package com.br.salesbuddy.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.salesbuddy.R;
import com.br.salesbuddy.model.ReprocessSaleData;

import java.util.List;
import java.util.Locale;

public class ReprocessingAdapter extends RecyclerView.Adapter<ReprocessingAdapter.ViewHolder> {

    private final List<ReprocessSaleData> items;

    // Removemos o Listener, pois o clique agora é no botão da tela pai
    public ReprocessingAdapter(List<ReprocessSaleData> items) {
        this.items = items;
    }

    public void clearList() {
        items.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reprocess_sale, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReprocessSaleData item = items.get(position);

        holder.tvClientName.setText(item.getClientName());
        // Ajuste conforme seu XML (se removeu o tv_error_reason, remova aqui tbm)
        holder.tvValue.setText(String.format(Locale.getDefault(), "R$ %.2f", item.getSaleValue()));
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvValue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tv_client_name);
            tvValue = itemView.findViewById(R.id.tv_total_value);
        }
    }
}