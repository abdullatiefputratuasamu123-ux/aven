package com.smartpelayanan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_notifikasi")
public class Notifikasi extends BaseEntity {

    @Column(name = "judul", nullable = false)
    private String judul;

    @Column(name = "pesan", columnDefinition = "TEXT")
    private String pesan;

    @Column(name = "tipe")
    private String tipe;

    @Column(name = "referensi_id")
    private String referensiId;

    @Column(name = "sudah_dibaca")
    private Boolean sudahDibaca = false;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getPesan() { return pesan; }
    public void setPesan(String pesan) { this.pesan = pesan; }

    public String getTipe() { return tipe; }
    public void setTipe(String tipe) { this.tipe = tipe; }

    public String getReferensiId() { return referensiId; }
    public void setReferensiId(String referensiId) { this.referensiId = referensiId; }

    public Boolean getSudahDibaca() { return sudahDibaca; }
    public void setSudahDibaca(Boolean sudahDibaca) { this.sudahDibaca = sudahDibaca; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
