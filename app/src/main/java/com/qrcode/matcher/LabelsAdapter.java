package com.qrcode.matcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

public class LabelsAdapter extends RecyclerView.Adapter<LabelsAdapter.ViewHolder> {

    private List<HashMap<String, String>> items;

    public LabelsAdapter(List<HashMap<String, String>> items) {
        this.items = items;
    }

    public void updateData(List<HashMap<String, String>> newItems) {
        this.items = newItems;
        notifyDataSetChanged(); // Tells RecyclerView to refresh all items
    }

    public void addItem(HashMap<String, String> item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public HashMap<String, String> getItem(int position) {
        return items.get(position);
    }

    public int getTotalItemsCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtCartonName;
        public TextView txtQuantity;

        public ViewHolder(View view) {
            super(view);
            txtQuantity = view.findViewById(R.id.txtQuantity);
            txtCartonName = view.findViewById(R.id.txtCartonName);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_label, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String, String> labelData = items.get(position);
        holder.txtQuantity.setText(labelData.getOrDefault(Utils.QUANTITY, ""));
        holder.txtCartonName.setText(labelData.getOrDefault(Utils.CARTON_NR, ""));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
