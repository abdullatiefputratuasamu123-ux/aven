package com.smartpelayanan.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_riwayat_status_layanan")
public class RiwayatStatusLayanan extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "layanan_id", nullable = false)
    private LayananAdministrasi layanan;

    @Column(name = "status_lama")
    private String statusLama;

    @Column(name = "status_baru")
    private String statusBaru;

    @Column(name = "catatan", columnDefinition = "TEXT")
    private String catatan;

    @Column(name = "diubah_oleh")
    private String diubahOleh; // email admin

    public LayananAdministrasi getLayanan() { return layanan; }
    public void setLayanan(LayananAdministrasi layanan) { this.layanan = layanan; }

    public String getStatusLama() { return statusLama; }
    public void setStatusLama(String statusLama) { this.statusLama = statusLama; }

    public String getStatusBaru() { return statusBaru; }
    public void setStatusBaru(String statusBaru) { this.statusBaru = statusBaru; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }

    public String getDiubahOleh() { return diubahOleh; }
    public void setDiubahOleh(String diubahOleh) { this.diubahOleh = diubahOleh; }
}
