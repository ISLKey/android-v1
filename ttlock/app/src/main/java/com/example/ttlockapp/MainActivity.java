package com.example.ttlockapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.ttlockapp.auth.AuthActivity;
import com.example.ttlockapp.card.CardManagementActivity;
import com.example.ttlockapp.databinding.ActivityMainBinding;
import com.example.ttlockapp.lock.LockManagementActivity;
import com.example.ttlockapp.lock.ScanLockActivity;
import com.example.ttlockapp.model.LockObj;
import com.example.ttlockapp.passcode.PasscodeManagementActivity;
import com.ttlock.bl.sdk.api.TTLockClient;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String[] PERMISSIONS = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        
        checkPermissions();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void setupListeners() {
        // Authentication
        binding.btnAuth.setOnClickListener(v -> {
            startActivity(new Intent(this, AuthActivity.class));
        });

        // Lock Management
        binding.btnScanLock.setOnClickListener(v -> {
            if (MyApplication.getInstance().getAccountInfo() == null) {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, ScanLockActivity.class));
        });

        binding.btnLockManagement.setOnClickListener(v -> {
            if (MyApplication.getInstance().getCurrentLock() == null) {
                Toast.makeText(this, "Please connect to a lock first", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, LockManagementActivity.class));
        });

        // Access Management
        binding.btnPasscodeManagement.setOnClickListener(v -> {
            if (MyApplication.getInstance().getCurrentLock() == null) {
                Toast.makeText(this, "Please connect to a lock first", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, PasscodeManagementActivity.class));
        });

        binding.btnCardManagement.setOnClickListener(v -> {
            if (MyApplication.getInstance().getCurrentLock() == null) {
                Toast.makeText(this, "Please connect to a lock first", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, CardManagementActivity.class));
        });
    }

    private void updateUI() {
        boolean isLoggedIn = MyApplication.getInstance().getAccountInfo() != null;
        LockObj currentLock = MyApplication.getInstance().getCurrentLock();
        
        // Update scan button based on login status
        binding.btnScanLock.setEnabled(isLoggedIn);
        
        // Update feature buttons based on lock connection
        boolean hasLock = currentLock != null;
        binding.btnLockManagement.setEnabled(hasLock);
        binding.btnPasscodeManagement.setEnabled(hasLock);
        binding.btnCardManagement.setEnabled(hasLock);
        
        // Update status text
        if (!isLoggedIn) {
            binding.tvStatus.setText("Please login to continue");
        } else if (!hasLock) {
            binding.tvStatus.setText("Please connect to a lock");
        } else {
            binding.tvStatus.setText("Connected to: " + currentLock.getLockName());
        }
    }

    private void checkPermissions() {
        boolean allPermissionsGranted = true;
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false;
                break;
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                Toast.makeText(this, "All permissions are required for the app to function properly",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTLockClient.getDefault().stopBTService();
    }
}
