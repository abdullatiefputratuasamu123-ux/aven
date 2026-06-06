package com.smartpelayanan.repository;

import com.smartpelayanan.entity.RiwayatStatusLayanan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RiwayatStatusLayananRepository extends JpaRepository<RiwayatStatusLayanan, UUID> {
    List<RiwayatStatusLayanan> findByLayananIdOrderByCreatedAtDesc(UUID layananId);
}
