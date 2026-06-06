package com.smartpelayanan.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_respon_pengaduan")
public class ResponPengaduan extends BaseEntity {

    @Column(name = "isi_respon", columnDefinition = "TEXT", nullable = false)
    private String isiRespon;

    @Column(name = "tgl_respon", nullable = false)
    private LocalDateTime tglRespon;

    @ManyToOne
    @JoinColumn(name = "pengaduan_id", nullable = false)
    private Pengaduan pengaduan;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    public String getIsiRespon() {
        return isiRespon;
    }

    public void setIsiRespon(String isiRespon) {
        this.isiRespon = isiRespon;
    }

    public LocalDateTime getTglRespon() {
        return tglRespon;
    }

    public void setTglRespon(LocalDateTime tglRespon) {
        this.tglRespon = tglRespon;
    }

    public Pengaduan getPengaduan() {
        return pengaduan;
    }

    public void setPengaduan(Pengaduan pengaduan) {
        this.pengaduan = pengaduan;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }
}
