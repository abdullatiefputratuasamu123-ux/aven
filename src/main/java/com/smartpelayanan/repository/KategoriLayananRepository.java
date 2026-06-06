package com.smartpelayanan.repository;

import com.smartpelayanan.entity.KategoriLayanan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KategoriLayananRepository extends JpaRepository<KategoriLayanan, Integer> {
    boolean existsByNamaKategori(String namaKategori);
}
