package com.isl.key.model;

public class AccountInfo {
    private String username;
    private String accessToken;
    private long uid;
    private String lockData;
    private long lockId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getLockData() {
        return lockData;
    }

    public void setLockData(String lockData) {
        this.lockData = lockData;
    }

    public long getLockId() {
        return lockId;
    }

    public void setLockId(long lockId) {
        this.lockId = lockId;
    }
}
