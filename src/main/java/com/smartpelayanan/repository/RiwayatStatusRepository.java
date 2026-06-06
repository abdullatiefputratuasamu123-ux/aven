package com.smartpelayanan.repository;

import com.smartpelayanan.entity.RiwayatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface RiwayatStatusRepository extends JpaRepository<RiwayatStatus, UUID> {
    List<RiwayatStatus> findByReferensiId(UUID referensiId);
    List<RiwayatStatus> findByReferensiIdAndTipeReferensiOrderByChangedAtDesc(
            UUID referensiId,
            RiwayatStatus.TipeReferensiEnum tipeReferensi
    );
}
