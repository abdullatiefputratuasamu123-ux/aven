package com.smartpelayanan.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "tb_layanan_administrasi")
public class LayananAdministrasi extends BaseEntity {

    @Column(name = "nomor_permohonan", unique = true)
    private String nomorPermohonan;

    @Column(name = "keperluan", columnDefinition = "TEXT")
    private String keperluan;

    @Column(name = "dokumen_pendukung")
    private String dokumenPendukung;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private com.smartpelayanan.enums.StatusLayananEnum status = com.smartpelayanan.enums.StatusLayananEnum.MENUNGGU;

    @Column(name = "catatan_petugas", columnDefinition = "TEXT")
    private String catatanPetugas;

    @Column(name = "tgl_diajukan")
    private LocalDate tglDiajukan;

    @Column(name = "tgl_selesai")
    private LocalDate tglSelesai;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "kategori_id", nullable = false)
    private KategoriLayanan kategori;

    public String getNomorPermohonan() {
        return nomorPermohonan;
    }

    public void setNomorPermohonan(String nomorPermohonan) {
        this.nomorPermohonan = nomorPermohonan;
    }

    public String getKeperluan() {
        return keperluan;
    }

    public void setKeperluan(String keperluan) {
        this.keperluan = keperluan;
    }

    public String getDokumenPendukung() {
        return dokumenPendukung;
    }

    public void setDokumenPendukung(String dokumenPendukung) {
        this.dokumenPendukung = dokumenPendukung;
    }

    public String getCatatanPetugas() {
        return catatanPetugas;
    }

    public void setCatatanPetugas(String catatanPetugas) {
        this.catatanPetugas = catatanPetugas;
    }

    public LocalDate getTglDiajukan() {
        return tglDiajukan;
    }

    public void setTglDiajukan(LocalDate tglDiajukan) {
        this.tglDiajukan = tglDiajukan;
    }

    public LocalDate getTglSelesai() {
        return tglSelesai;
    }

    public void setTglSelesai(LocalDate tglSelesai) {
        this.tglSelesai = tglSelesai;
    }

    public com.smartpelayanan.enums.StatusLayananEnum getStatus() {
        return status;
    }

    public void setStatus(com.smartpelayanan.enums.StatusLayananEnum status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public KategoriLayanan getKategori() {
        return kategori;
    }

    public void setKategori(KategoriLayanan kategori) {
        this.kategori = kategori;
    }

    public String generateNomorPermohonan() {
        return "PLY-" + System.currentTimeMillis();
    }
}
