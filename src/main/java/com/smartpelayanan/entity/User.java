package com.smartpelayanan.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_user")
public class User extends BaseEntity {

    @Column(name = "nama_lengkap", nullable = false)
    private String namaLengkap;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "no_telp")
    private String noTelp;

    @Column(name = "alamat", columnDefinition = "TEXT")
    private String alamat;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private com.smartpelayanan.enums.RoleEnum role;

    @Column(name = "status_aktif", nullable = false)
    private Boolean statusAktif = true;

    @OneToMany(mappedBy = "user")
    private List<Pengaduan> pengaduans;

    @OneToMany(mappedBy = "user")
    private List<LayananAdministrasi> layananAdministrasis;

    @OneToMany(mappedBy = "admin")
    private List<ResponPengaduan> responPengaduans;

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

    public com.smartpelayanan.enums.RoleEnum getRole() {
        return role;
    }

    public void setRole(com.smartpelayanan.enums.RoleEnum role) {
        this.role = role;
    }

    public void setStatusAktif(Boolean statusAktif) {
        this.statusAktif = statusAktif;
    }

    public Boolean isStatusAktif() {
        return statusAktif;
    }

    public List<Pengaduan> getPengaduans() {
        return pengaduans;
    }

    public List<LayananAdministrasi> getLayananAdministrasis() {
        return layananAdministrasis;
    }

    public List<ResponPengaduan> getResponPengaduans() {
        return responPengaduans;
    }
}
