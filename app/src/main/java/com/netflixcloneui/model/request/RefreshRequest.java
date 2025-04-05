package com.netflixcloneui.model.request;

public class RefreshRequest {
    private String token;

    public RefreshRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
