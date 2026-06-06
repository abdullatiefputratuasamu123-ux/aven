package com.smartpelayanan.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Nama lengkap harus diisi")
    @JsonProperty("namaLengkap")
    private String namaLengkap;

    @NotBlank(message = "Email harus diisi")
    @Email(message = "Format email tidak valid")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Password harus diisi")
    @Size(min = 6, message = "Password minimal 6 karakter")
    @JsonProperty("password")
    private String password;

    @JsonProperty("noTelp")
    private String noTelp;

    @JsonProperty("alamat")
    private String alamat;

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
