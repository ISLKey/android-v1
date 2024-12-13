package com.example.ttlockapp.lock;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ttlockapp.MyApplication;
import com.example.ttlockapp.R;
import com.example.ttlockapp.databinding.ActivityLockManagementBinding;
import com.example.ttlockapp.model.LockObj;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.ControlLockCallback;
import com.ttlock.bl.sdk.callback.GetBatteryLevelCallback;
import com.ttlock.bl.sdk.callback.GetLockStatusCallback;
import com.ttlock.bl.sdk.callback.GetLockTimeCallback;
import com.ttlock.bl.sdk.callback.ResetKeyCallback;
import com.ttlock.bl.sdk.callback.SetAutoLockCallback;
import com.ttlock.bl.sdk.constant.ControlAction;
import com.ttlock.bl.sdk.entity.LockError;

import java.util.Calendar;

public class LockManagementActivity extends AppCompatActivity {
    private ActivityLockManagementBinding binding;
    private LockObj currentLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_management);
        
        currentLock = MyApplication.getInstance().getCurrentLock();
        if (currentLock == null) {
            Toast.makeText(this, "No lock selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupListeners();
        updateLockInfo();
    }

    private void initializeViews() {
        binding.tvLockName.setText(currentLock.getLockName());
        binding.tvLockMac.setText("MAC: " + currentLock.getLockMac());
    }

    private void setupListeners() {
        // Lock Control
        binding.btnUnlock.setOnClickListener(v -> controlLock(ControlAction.UNLOCK));
        binding.btnLock.setOnClickListener(v -> controlLock(ControlAction.LOCK));
        
        // Lock Settings
        binding.btnAutoLock.setOnClickListener(v -> setAutoLockTime(10)); // 10 seconds
        binding.btnResetLock.setOnClickListener(v -> resetLock());
        
        // Lock Info
        binding.btnRefresh.setOnClickListener(v -> {
            getLockStatus();
            getBatteryLevel();
            getLockTime();
        });
    }

    private void controlLock(int action) {
        binding.progressBar.setVisibility(View.VISIBLE);
        TTLockClient.getDefault().controlLock(action, new ControlLockCallback() {
            @Override
            public void onControlLockSuccess(int lockAction, int battery, int uniqueId) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    String actionStr = lockAction == ControlAction.UNLOCK ? "unlocked" : "locked";
                    Toast.makeText(LockManagementActivity.this, 
                            "Lock successfully " + actionStr, Toast.LENGTH_SHORT).show();
                    updateBatteryLevel(battery);
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(LockManagementActivity.this, 
                            "Control failed: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setAutoLockTime(int seconds) {
        binding.progressBar.setVisibility(View.VISIBLE);
        TTLockClient.getDefault().setAutoLockTime(seconds, new SetAutoLockCallback() {
            @Override
            public void onSetAutoLockTimeSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(LockManagementActivity.this, 
                            "Auto lock time set to " + seconds + " seconds", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(LockManagementActivity.this, 
                            "Setting auto lock failed: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void resetLock() {
        binding.progressBar.setVisibility(View.VISIBLE);
        TTLockClient.getDefault().resetLock(new ResetKeyCallback() {
            @Override
            public void onResetKeySuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(LockManagementActivity.this, 
                            "Lock reset successful", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(LockManagementActivity.this, 
                            "Lock reset failed: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void getLockStatus() {
        TTLockClient.getDefault().getLockStatus(new GetLockStatusCallback() {
            @Override
            public void onGetLockStatusSuccess(int status) {
                runOnUiThread(() -> {
                    String statusText = status == 0 ? "Locked" : "Unlocked";
                    binding.tvLockStatus.setText("Status: " + statusText);
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    Toast.makeText(LockManagementActivity.this, 
                            "Get status failed: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void getBatteryLevel() {
        TTLockClient.getDefault().getBatteryLevel(new GetBatteryLevelCallback() {
            @Override
            public void onGetBatteryLevelSuccess(int battery) {
                runOnUiThread(() -> updateBatteryLevel(battery));
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    Toast.makeText(LockManagementActivity.this, 
                            "Get battery failed: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void getLockTime() {
        TTLockClient.getDefault().getLockTime(new GetLockTimeCallback() {
            @Override
            public void onGetLockTimeSuccess(Calendar calendar) {
                runOnUiThread(() -> {
                    binding.tvLockTime.setText("Lock Time: " + 
                            calendar.get(Calendar.HOUR_OF_DAY) + ":" + 
                            calendar.get(Calendar.MINUTE));
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    Toast.makeText(LockManagementActivity.this, 
                            "Get time failed: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void updateBatteryLevel(int battery) {
        binding.tvBatteryLevel.setText("Battery: " + battery + "%");
    }

    private void updateLockInfo() {
        getLockStatus();
        getBatteryLevel();
        getLockTime();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TTLockClient.getDefault().disconnect();
    }
}
