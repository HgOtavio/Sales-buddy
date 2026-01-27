package com.br.salesbuddy.view.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.br.salesbuddy.R;
import com.br.salesbuddy.model.body.ReprocessSaleData;

import java.util.List;
import java.util.Locale;

public class ReprocessingAdapter extends RecyclerView.Adapter<ReprocessingAdapter.ViewHolder> {

    private final List<ReprocessSaleData> items;

    public ReprocessingAdapter(List<ReprocessSaleData> items) {
        this.items = items;
    }

    public void markAsProcessed(int position) {
        if (position >= 0 && position < items.size()) {
            items.get(position).setProcessed(true);
            notifyItemChanged(position);
        }
    }

    public void clearList() {
        items.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // AQUI ELE CHAMA SEU XML "item_reprocess_sale"
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reprocess_sale, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReprocessSaleData item = items.get(position);

        holder.tvClientName.setText(item.getClientName());
        holder.tvValue.setText(String.format(Locale.getDefault(), "R$ %.2f", item.getSaleValue()));

        if (item.isProcessed()) {
            holder.statusIndicator.setBackgroundColor(Color.parseColor("#B0B0B0"));
        } else {
            holder.statusIndicator.setBackgroundColor(Color.parseColor("#A32C65"));
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName, tvValue;
        View statusIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // LIGANDO OS IDS DO SEU XML AO CÓDIGO JAVA
            tvClientName = itemView.findViewById(R.id.tv_client_name);
            tvValue = itemView.findViewById(R.id.tv_total_value);
            statusIndicator = itemView.findViewById(R.id.view_status_indicator);
        }
    }
}