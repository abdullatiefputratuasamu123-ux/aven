package com.smartpelayanan.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_kategori_layanan")
public class KategoriLayanan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nama_kategori", nullable = false)
    private String namaKategori;

    @Column(name = "deskripsi", columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "kategori")
    private List<LayananAdministrasi> layananAdministrasis;

    public Integer getId() {
        return id;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
    
    public boolean isIsActive() {
        return isActive;
    }

    public List<LayananAdministrasi> getLayananAdministrasis() {
        return layananAdministrasis;
    }
}
