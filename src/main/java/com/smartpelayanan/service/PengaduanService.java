package com.smartpelayanan.service;

import com.smartpelayanan.dto.PengaduanDTO;
import com.smartpelayanan.enums.StatusPengaduanEnum;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PengaduanService {

    List<PengaduanDTO> findAll();
    PengaduanDTO findById(UUID id);
    PengaduanDTO save(PengaduanDTO pengaduanDTO);
    PengaduanDTO update(UUID id, PengaduanDTO pengaduanDTO);
    void updateStatus(UUID id, StatusPengaduanEnum status);
    void delete(UUID id);
    List<Map<String, Object>> getAllPengaduan();
    List<Map<String, Object>> getPengaduanByUserId(UUID userId);
    List<Map<String, Object>> getLayananByUserId(UUID userId);
    List<Map<String, Object>> getAllLayanan();
}
