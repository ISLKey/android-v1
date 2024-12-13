package com.example.ttlockapp.model;

public class LockObj {
    private long lockId;
    private String lockName;
    private String lockMac;
    private String lockVersion;
    private int electricQuantity;
    private int lockStatus;
    private String lockData;
    private boolean isInitialized;
    private boolean isConnected;

    public long getLockId() {
        return lockId;
    }

    public void setLockId(long lockId) {
        this.lockId = lockId;
    }

    public String getLockName() {
        return lockName;
    }

    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    public String getLockMac() {
        return lockMac;
    }

    public void setLockMac(String lockMac) {
        this.lockMac = lockMac;
    }

    public String getLockVersion() {
        return lockVersion;
    }

    public void setLockVersion(String lockVersion) {
        this.lockVersion = lockVersion;
    }

    public int getElectricQuantity() {
        return electricQuantity;
    }

    public void setElectricQuantity(int electricQuantity) {
        this.electricQuantity = electricQuantity;
    }

    public int getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(int lockStatus) {
        this.lockStatus = lockStatus;
    }

    public String getLockData() {
        return lockData;
    }

    public void setLockData(String lockData) {
        this.lockData = lockData;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
