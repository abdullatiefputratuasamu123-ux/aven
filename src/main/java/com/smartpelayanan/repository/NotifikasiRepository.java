package com.smartpelayanan.repository;

import com.smartpelayanan.entity.Notifikasi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotifikasiRepository extends JpaRepository<Notifikasi, UUID> {
    List<Notifikasi> findByUserIdOrderByCreatedAtDesc(UUID userId);
    long countByUserIdAndSudahDibacaFalse(UUID userId);
    List<Notifikasi> findByUserId(UUID userId);
}
