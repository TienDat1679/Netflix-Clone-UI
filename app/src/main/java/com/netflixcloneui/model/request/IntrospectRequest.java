package com.netflixcloneui.model.request;

public class IntrospectRequest {
    private String token;

    public IntrospectRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
