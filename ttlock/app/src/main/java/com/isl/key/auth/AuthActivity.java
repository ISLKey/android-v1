package com.isl.key.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.isl.key.MyApplication;
import com.isl.key.R;
import com.isl.key.databinding.ActivityAuthBinding;
import com.isl.key.model.AccountInfo;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.TTLockCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {
    private ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth);

        initializeViews();
    }

    private void initializeViews() {
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void attemptLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        
        TTLockClient.getDefault().loginWithTTLockAccount(username, password, new TTLockCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    
                    // Save account info
                    AccountInfo accountInfo = new AccountInfo();
                    accountInfo.setUsername(username);
                    // Set other account details from API response
                    
                    MyApplication.getInstance().setAccountInfo(accountInfo);
                    
                    Toast.makeText(AuthActivity.this, R.string.success_login, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(AuthActivity.this, 
                            "Login failed: " + errorMsg, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void attemptRegister() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        TTLockClient.getDefault().createTTLockAccount(username, password, new TTLockCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(AuthActivity.this, R.string.success_register, Toast.LENGTH_SHORT).show();
                    // After successful registration, attempt login
                    attemptLogin();
                });
            }

            @Override
            public void onError(String errorCode, String errorMsg) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(AuthActivity.this, 
                            "Registration failed: " + errorMsg, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
