package com.isl.key;

import android.app.Application;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.isl.key.model.AccountInfo;
import com.isl.key.model.LockObj;

public class MyApplication extends Application {
    private static MyApplication instance;
    private AccountInfo accountInfo;
    private LockObj currentLock;

    public static final String CLIENT_ID = "be0913d92f17483fbba7b4c303f1d403";
    public static final String CLIENT_SECRET = "944dd5d2a9ab61e03ce8b0384c717e54";

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
