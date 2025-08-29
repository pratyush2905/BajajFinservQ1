package com.example.demo.dto;

public class HookResponse {
    private String webhook;
    private String accessToken;

    public HookResponse() {}

    // Getters and Setters
    public String getWebhook() { return webhook; }
    public void setWebhook(String webhook) { this.webhook = webhook; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}