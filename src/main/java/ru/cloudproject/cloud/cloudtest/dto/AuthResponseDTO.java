package ru.cloudproject.cloud.cloudtest.dto;

public class AuthResponseDTO {
    private String authToken;

    public AuthResponseDTO(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}