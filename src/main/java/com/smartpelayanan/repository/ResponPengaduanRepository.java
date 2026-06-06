package com.smartpelayanan.repository;

import com.smartpelayanan.entity.ResponPengaduan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ResponPengaduanRepository extends JpaRepository<ResponPengaduan, UUID> {
    List<ResponPengaduan> findByPengaduanId(UUID pengaduanId);
}
