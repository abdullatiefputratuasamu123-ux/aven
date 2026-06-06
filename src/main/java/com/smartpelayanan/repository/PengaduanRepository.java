package com.smartpelayanan.repository;

import com.smartpelayanan.entity.Pengaduan;
import com.smartpelayanan.enums.StatusPengaduanEnum;
import com.smartpelayanan.enums.PrioritasEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PengaduanRepository extends JpaRepository<Pengaduan, UUID> {
    List<Pengaduan> findByUserId(UUID userId);
    List<Pengaduan> findByStatus(StatusPengaduanEnum status);
    List<Pengaduan> findByPrioritas(PrioritasEnum prioritas);
}
