package com.smartpelayanan.dto;

public class AuthResponse {
    private String token;
    private String email;
    private String namaLengkap;
    private String role;

    public AuthResponse(String token, String email, String namaLengkap, String role) {
        this.token = token;
        this.email = email;
        this.namaLengkap = namaLengkap;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}