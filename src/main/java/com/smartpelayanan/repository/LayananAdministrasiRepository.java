package com.smartpelayanan.repository;

import com.smartpelayanan.entity.LayananAdministrasi;
import com.smartpelayanan.enums.StatusLayananEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LayananAdministrasiRepository extends JpaRepository<LayananAdministrasi, UUID> {
    List<LayananAdministrasi> findByUserId(UUID userId);
    List<LayananAdministrasi> findByStatus(StatusLayananEnum status);
    List<LayananAdministrasi> findByKategoriId(Integer kategoriId);
}
