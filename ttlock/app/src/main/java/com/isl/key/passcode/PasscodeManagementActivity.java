package com.isl.key.passcode;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.isl.key.MyApplication;
import com.isl.key.R;
import com.isl.key.databinding.ActivityPasscodeManagementBinding;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.CreateCustomPasscodeCallback;
import com.ttlock.bl.sdk.callback.DeletePasscodeCallback;
import com.ttlock.bl.sdk.callback.GetPasscodeCallback;
import com.ttlock.bl.sdk.callback.ModifyPasscodeCallback;
import com.ttlock.bl.sdk.entity.LockError;

import java.util.Calendar;

public class PasscodeManagementActivity extends AppCompatActivity {
    private ActivityPasscodeManagementBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_passcode_management);

        if (MyApplication.getInstance().getCurrentLock() == null) {
            Toast.makeText(this, "No lock selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupListeners();
    }

    private void setupListeners() {
        binding.btnCreatePasscode.setOnClickListener(v -> createCustomPasscode());
        binding.btnModifyPasscode.setOnClickListener(v -> modifyPasscode());
        binding.btnDeletePasscode.setOnClickListener(v -> deletePasscode());
        binding.btnGetPasscode.setOnClickListener(v -> getPasscode());
    }

    private void createCustomPasscode() {
        String passcode = binding.etPasscode.getText().toString().trim();
        if (passcode.isEmpty() || passcode.length() < 4) {
            Toast.makeText(this, "Please enter a valid passcode (min 4 digits)", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        // Get start and end time from date pickers
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1); // Default validity: 1 month

        TTLockClient.getDefault().createCustomPasscode(passcode, startDate, endDate, 
                new CreateCustomPasscodeCallback() {
            @Override
            public void onCreateCustomPasscodeSuccess(String passcodeStr) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvPasscodeInfo.setText("Created passcode: " + passcodeStr);
                    Toast.makeText(PasscodeManagementActivity.this, 
                            "Passcode created successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(PasscodeManagementActivity.this, 
                            "Failed to create passcode: " + error.getErrorMsg(), 
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void modifyPasscode() {
        String oldPasscode = binding.etOldPasscode.getText().toString().trim();
        String newPasscode = binding.etNewPasscode.getText().toString().trim();
        
        if (oldPasscode.isEmpty() || newPasscode.isEmpty()) {
            Toast.makeText(this, "Please enter both old and new passcodes", 
                    Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        TTLockClient.getDefault().modifyPasscode(oldPasscode, newPasscode, 
                new ModifyPasscodeCallback() {
            @Override
            public void onModifyPasscodeSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(PasscodeManagementActivity.this, 
                            "Passcode modified successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(PasscodeManagementActivity.this, 
                            "Failed to modify passcode: " + error.getErrorMsg(), 
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void deletePasscode() {
        String passcode = binding.etPasscodeToDelete.getText().toString().trim();
        if (passcode.isEmpty()) {
            Toast.makeText(this, "Please enter the passcode to delete", 
                    Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        TTLockClient.getDefault().deletePasscode(passcode, new DeletePasscodeCallback() {
            @Override
            public void onDeletePasscodeSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(PasscodeManagementActivity.this, 
                            "Passcode deleted successfully", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(PasscodeManagementActivity.this, 
                            "Failed to delete passcode: " + error.getErrorMsg(), 
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void getPasscode() {
        binding.progressBar.setVisibility(View.VISIBLE);

        TTLockClient.getDefault().getPasscode(new GetPasscodeCallback() {
            @Override
            public void onGetPasscodeSuccess(String passcode) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvPasscodeInfo.setText("Current passcode: " + passcode);
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(PasscodeManagementActivity.this, 
                            "Failed to get passcode: " + error.getErrorMsg(), 
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
