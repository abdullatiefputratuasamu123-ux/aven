package com.smartpelayanan.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "tb_pengaduan")
public class Pengaduan extends BaseEntity {

    @Column(name = "judul", nullable = false)
    private String judul;

    @Column(name = "deskripsi", columnDefinition = "TEXT", nullable = false)
    private String deskripsi;

    @Column(name = "lokasi")
    private String lokasi;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private com.smartpelayanan.enums.StatusPengaduanEnum status = com.smartpelayanan.enums.StatusPengaduanEnum.BARU;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioritas", nullable = false)
    private com.smartpelayanan.enums.PrioritasEnum prioritas = com.smartpelayanan.enums.PrioritasEnum.SEDANG;

    @Column(name = "foto_bukti")
    private String fotoBukti;

    @Column(name = "tanggal_kejadian")
    private LocalDate tanggalKejadian;

    @Column(name = "catatan_admin", columnDefinition = "TEXT")
    private String catatanAdmin;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "nama_pelapor")
    private String namaPelapor;

    @Column(name = "kontak_pelapor")
    private String kontakPelapor;

    @OneToMany(mappedBy = "pengaduan")
    private List<ResponPengaduan> responPengaduans;

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

    public com.smartpelayanan.enums.StatusPengaduanEnum getStatus() {
        return status;
    }

    public void setStatus(com.smartpelayanan.enums.StatusPengaduanEnum status) {
        this.status = status;
    }

    public com.smartpelayanan.enums.PrioritasEnum getPrioritas() {
        return prioritas;
    }

    public void setPrioritas(com.smartpelayanan.enums.PrioritasEnum prioritas) {
        this.prioritas = prioritas;
    }

    public boolean isOpen() {
        return status == com.smartpelayanan.enums.StatusPengaduanEnum.BARU;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<ResponPengaduan> getResponPengaduans() {
        return responPengaduans;
    }

    public String getNamaPelapor() {
        return namaPelapor;
    }

    public void setNamaPelapor(String namaPelapor) {
        this.namaPelapor = namaPelapor;
    }

    public String getKontakPelapor() {
        return kontakPelapor;
    }

    public void setKontakPelapor(String kontakPelapor) {
        this.kontakPelapor = kontakPelapor;
    }
}
