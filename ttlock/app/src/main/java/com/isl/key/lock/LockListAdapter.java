package com.isl.key.lock;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.isl.key.R;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import java.util.List;

public class LockListAdapter extends RecyclerView.Adapter<LockListAdapter.LockViewHolder> {
    private List<ExtendedBluetoothDevice> locks;
    private OnLockClickListener listener;

    public interface OnLockClickListener {
        void onLockClick(ExtendedBluetoothDevice device);
    }

    public LockListAdapter(List<ExtendedBluetoothDevice> locks, OnLockClickListener listener) {
        this.locks = locks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lock, parent, false);
        return new LockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LockViewHolder holder, int position) {
        ExtendedBluetoothDevice device = locks.get(position);
        holder.bind(device);
    }

    @Override
    public int getItemCount() {
        return locks.size();
    }

    class LockViewHolder extends RecyclerView.ViewHolder {
        private TextView tvLockName;
        private TextView tvMacAddress;
        private TextView tvRssi;

        public LockViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLockName = itemView.findViewById(R.id.tvLockName);
            tvMacAddress = itemView.findViewById(R.id.tvMacAddress);
            tvRssi = itemView.findViewById(R.id.tvRssi);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onLockClick(locks.get(position));
                }
            });
        }

        public void bind(ExtendedBluetoothDevice device) {
            String name = device.getName();
            if (name == null || name.isEmpty()) {
                name = "Unknown Lock";
            }
            tvLockName.setText(name);
            tvMacAddress.setText("MAC: " + device.getAddress());
            tvRssi.setText("Signal: " + device.getRssi() + " dBm");
        }
    }

    public void updateLocks(List<ExtendedBluetoothDevice> newLocks) {
        this.locks = newLocks;
        notifyDataSetChanged();
    }
}
