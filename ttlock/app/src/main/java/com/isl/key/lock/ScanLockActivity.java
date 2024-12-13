package com.isl.key.lock;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.isl.key.MyApplication;
import com.isl.key.R;
import com.isl.key.databinding.ActivityScanLockBinding;
import com.isl.key.model.LockObj;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ScanLockCallback;
import com.ttlock.bl.sdk.callback.TTLockCallback;
import com.ttlock.bl.sdk.entity.LockError;
import com.ttlock.bl.sdk.scanner.ExtendedBluetoothDevice;

import java.util.ArrayList;
import java.util.List;

public class ScanLockActivity extends AppCompatActivity {
    private ActivityScanLockBinding binding;
    private List<ExtendedBluetoothDevice> lockList = new ArrayList<>();
    private LockListAdapter adapter;
    private boolean isScanning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scan_lock);

        setupRecyclerView();
        initializeViews();
    }

    private void setupRecyclerView() {
        adapter = new LockListAdapter(lockList, device -> {
            // Handle lock selection
            stopScan();
            connectToLock(device);
        });

        binding.rvLocks.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLocks.setAdapter(adapter);
    }

    private void initializeViews() {
        binding.btnScan.setOnClickListener(v -> {
            if (isScanning) {
                stopScan();
            } else {
                startScan();
            }
        });
    }

    private void startScan() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            Toast.makeText(this, R.string.error_bluetooth_required, Toast.LENGTH_SHORT).show();
            return;
        }

        lockList.clear();
        adapter.notifyDataSetChanged();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnScan.setText("Stop Scan");
        isScanning = true;

        TTLockClient.getDefault().startScanLock(new ScanLockCallback() {
            @Override
            public void onScanLockSuccess(ExtendedBluetoothDevice device) {
                runOnUiThread(() -> {
                    lockList.add(device);
                    adapter.notifyItemInserted(lockList.size() - 1);
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    Toast.makeText(ScanLockActivity.this, 
                            "Scan failed: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    stopScan();
                });
            }
        });
    }

    private void stopScan() {
        TTLockClient.getDefault().stopScanLock();
        binding.progressBar.setVisibility(View.GONE);
        binding.btnScan.setText("Start Scan");
        isScanning = false;
    }

    private void connectToLock(ExtendedBluetoothDevice device) {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        TTLockClient.getDefault().connectLock(device, new TTLockCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    
                    // Save lock info
                    LockObj lockObj = new LockObj();
                    lockObj.setLockMac(device.getAddress());
                    lockObj.setLockName(device.getName());
                    lockObj.setConnected(true);
                    
                    MyApplication.getInstance().setCurrentLock(lockObj);
                    
                    Toast.makeText(ScanLockActivity.this, 
                            "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(ScanLockActivity.this, 
                            "Connection failed: " + errorMsg, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScan();
    }
}
