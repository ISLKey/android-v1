package com.example.ttlockapp;

import android.app.Application;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.example.ttlockapp.model.AccountInfo;
import com.example.ttlockapp.model.LockObj;

public class MyApplication extends Application {
    private static MyApplication instance;
    private AccountInfo accountInfo;
    private LockObj currentLock;

    public static final String CLIENT_ID = "YOUR_CLIENT_ID";    // Replace with your TTLock client ID
    public static final String CLIENT_SECRET = "YOUR_CLIENT_SECRET";  // Replace with your TTLock client secret

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        // Initialize TTLock SDK
        TTLockClient.getDefault().prepareBTService(getApplicationContext());
    }

    public static MyApplication getInstance() {
        return instance;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }

    public LockObj getCurrentLock() {
        return currentLock;
    }

    public void setCurrentLock(LockObj lock) {
        this.currentLock = lock;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        TTLockClient.getDefault().stopBTService();
    }
}
