package com.smartpelayanan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_lampiran_file")
public class LampiranFile extends BaseEntity {

    @Column(name = "nama_file", nullable = false)
    private String namaFile;

    @Column(name = "nama_tersimpan", nullable = false)
    private String namaTersimpan;

    @Column(name = "tipe_file")
    private String tipeFile;

    @Column(name = "ukuran_file")
    private Long ukuranFile;

    @Column(name = "url_file")
    private String urlFile;

    @Column(name = "referensi_id")
    private String referensiId;

    @Column(name = "tipe_referensi")
    private String tipeReferensi;

    public String getNamaFile() { return namaFile; }
    public void setNamaFile(String namaFile) { this.namaFile = namaFile; }

    public String getNamaTersimpan() { return namaTersimpan; }
    public void setNamaTersimpan(String namaTersimpan) { this.namaTersimpan = namaTersimpan; }

    public String getTipeFile() { return tipeFile; }
    public void setTipeFile(String tipeFile) { this.tipeFile = tipeFile; }

    public Long getUkuranFile() { return ukuranFile; }
    public void setUkuranFile(Long ukuranFile) { this.ukuranFile = ukuranFile; }

    public String getUrlFile() { return urlFile; }
    public void setUrlFile(String urlFile) { this.urlFile = urlFile; }

    public String getReferensiId() { return referensiId; }
    public void setReferensiId(String referensiId) { this.referensiId = referensiId; }

    public String getTipeReferensi() { return tipeReferensi; }
    public void setTipeReferensi(String tipeReferensi) { this.tipeReferensi = tipeReferensi; }
}
