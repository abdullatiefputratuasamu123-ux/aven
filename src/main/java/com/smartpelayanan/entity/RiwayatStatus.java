package com.smartpelayanan.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_riwayat_status")
public class RiwayatStatus extends BaseEntity {

    @Column(name = "referensi_id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    private java.util.UUID referensiId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipe_referensi", nullable = false)
    private TipeReferensiEnum tipeReferensi;

    @Column(name = "status_lama")
    private String statusLama;

    @Column(name = "status_baru")
    private String statusBaru;

    @Column(name = "alasan_perubahan", columnDefinition = "TEXT")
    private String alasanPerubahan;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    public enum TipeReferensiEnum {
        PENGADUAN,
        LAYANAN
    }

    public java.util.UUID getReferensiId() {
        return referensiId;
    }

    public void setReferensiId(java.util.UUID referensiId) {
        this.referensiId = referensiId;
    }

    public TipeReferensiEnum getTipeReferensi() {
        return tipeReferensi;
    }

    public void setTipeReferensi(TipeReferensiEnum tipeReferensi) {
        this.tipeReferensi = tipeReferensi;
    }

    public String getStatusLama() {
        return statusLama;
    }

    public void setStatusLama(String statusLama) {
        this.statusLama = statusLama;
    }

    public String getStatusBaru() {
        return statusBaru;
    }

    public void setStatusBaru(String statusBaru) {
        this.statusBaru = statusBaru;
    }

    public String getAlasanPerubahan() {
        return alasanPerubahan;
    }

    public void setAlasanPerubahan(String alasanPerubahan) {
        this.alasanPerubahan = alasanPerubahan;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }
}
