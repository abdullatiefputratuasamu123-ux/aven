package com.smartpelayanan.dto;

import com.smartpelayanan.enums.PrioritasEnum;
import com.smartpelayanan.enums.StatusPengaduanEnum;

import java.time.LocalDate;
import java.util.UUID;

public class PengaduanDTO {

    private UUID id;
    private String judul;
    private String deskripsi;
    private String lokasi;
    private StatusPengaduanEnum status;
    private PrioritasEnum prioritas;
    private String fotoBukti;
    private LocalDate tanggalKejadian;
    private String catatanAdmin;
    private String namaUser;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public StatusPengaduanEnum getStatus() {
        return status;
    }

    public void setStatus(StatusPengaduanEnum status) {
        this.status = status;
    }

    public PrioritasEnum getPrioritas() {
        return prioritas;
    }

    public void setPrioritas(PrioritasEnum prioritas) {
        this.prioritas = prioritas;
    }

    public String getFotoBukti() {
        return fotoBukti;
    }

    public void setFotoBukti(String fotoBukti) {
        this.fotoBukti = fotoBukti;
    }

    public LocalDate getTanggalKejadian() {
        return tanggalKejadian;
    }

    public void setTanggalKejadian(LocalDate tanggalKejadian) {
        this.tanggalKejadian = tanggalKejadian;
    }

    public String getCatatanAdmin() {
        return catatanAdmin;
    }

    public void setCatatanAdmin(String catatanAdmin) {
        this.catatanAdmin = catatanAdmin;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }
}
