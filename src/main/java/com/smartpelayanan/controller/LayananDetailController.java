package com.smartpelayanan.controller;

import com.smartpelayanan.entity.*;
import org.springframework.transaction.annotation.Transactional;
import com.smartpelayanan.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/layanan/detail")
public class LayananDetailController {

    private final LayananAdministrasiRepository layananAdministrasiRepository;
    private final JawabanFormRepository jawabanFormRepository;
    private final LampiranFileRepository lampiranFileRepository;
    private final RiwayatStatusLayananRepository riwayatStatusLayananRepository;
    public LayananDetailController(LayananAdministrasiRepository layananAdministrasiRepository,
                                   JawabanFormRepository jawabanFormRepository,
                                   LampiranFileRepository lampiranFileRepository,
                                   RiwayatStatusLayananRepository riwayatStatusLayananRepository) {
        this.layananAdministrasiRepository = layananAdministrasiRepository;
        this.jawabanFormRepository = jawabanFormRepository;
        this.lampiranFileRepository = lampiranFileRepository;
        this.riwayatStatusLayananRepository = riwayatStatusLayananRepository;
    }

    @Transactional
    // GET /api/v1/layanan/detail/{id} — detail lengkap permohonan layanan
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getLayananDetail(@PathVariable UUID id) {
        try {
            LayananAdministrasi layanan = layananAdministrasiRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Layanan not found"));

            Map<String, Object> result = new HashMap<>();
            result.put("id", layanan.getId());
            result.put("nomorPermohonan", layanan.getNomorPermohonan());
            result.put("status", layanan.getStatus() != null ? layanan.getStatus().name() : null);
            result.put("tglDiajukan", layanan.getTglDiajukan() != null ? layanan.getTglDiajukan().toString() : null);
            result.put("tglSelesai", layanan.getTglSelesai() != null ? layanan.getTglSelesai().toString() : null);
            result.put("keperluan", layanan.getKeperluan());
            result.put("catatanPetugas", layanan.getCatatanPetugas());

            // User info
            Map<String, Object> userMap = new HashMap<>();
            if (layanan.getUser() != null) {
                userMap.put("namaLengkap", layanan.getUser().getNamaLengkap());
                userMap.put("email", layanan.getUser().getEmail());
                userMap.put("noTelp", layanan.getUser().getNoTelp());
            }
            result.put("user", userMap);

            // Kategori info
            Map<String, Object> kategoriMap = new HashMap<>();
            if (layanan.getKategori() != null) {
                kategoriMap.put("id", layanan.getKategori().getId());
                kategoriMap.put("namaKategori", layanan.getKategori().getNamaKategori());
            }
            result.put("kategori", kategoriMap);

            // Jawaban form
            List<JawabanForm> jawabanList = jawabanFormRepository.findByLayananId(id);
            List<Map<String, Object>> jawabanResult = jawabanList.stream().map(j -> {
                Map<String, Object> jMap = new HashMap<>();
                jMap.put("fieldId", j.getField() != null ? j.getField().getId() : null);
                jMap.put("label", j.getField() != null ? j.getField().getLabel() : null);
                jMap.put("tipe", j.getField() != null ? j.getField().getTipe() : null);
                jMap.put("nilai", j.getNilai());
                return jMap;
            }).toList();
            result.put("jawaban", jawabanResult);

            // Lampiran file
            List<LampiranFile> lampiranList = lampiranFileRepository.findByReferensiId(id.toString());
            List<Map<String, Object>> lampiranResult = lampiranList.stream().map(l -> {
                Map<String, Object> lMap = new HashMap<>();
                lMap.put("id", l.getId());
                lMap.put("namaFile", l.getNamaFile());
                lMap.put("urlFile", l.getUrlFile());
                lMap.put("tipeFile", l.getTipeFile());
                return lMap;
            }).toList();
            result.put("lampiran", lampiranResult);

            // Riwayat status
            List<RiwayatStatusLayanan> riwayatList = riwayatStatusLayananRepository.findByLayananIdOrderByCreatedAtDesc(id);
            List<Map<String, Object>> riwayatResult = riwayatList.stream().map(r -> {
                Map<String, Object> rMap = new HashMap<>();
                rMap.put("statusLama", r.getStatusLama());
                rMap.put("statusBaru", r.getStatusBaru());
                rMap.put("catatan", r.getCatatan());
                rMap.put("diubahOleh", r.getDiubahOleh());
                rMap.put("tanggal", r.getCreatedAt() != null ? r.getCreatedAt().toString() : null);
                return rMap;
            }).toList();
            result.put("riwayatStatus", riwayatResult);

            return ResponseEntity.ok(Map.of("data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Internal error"));
        }
    }

}
