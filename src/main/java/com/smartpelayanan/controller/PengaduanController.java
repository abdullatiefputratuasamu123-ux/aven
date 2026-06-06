package com.smartpelayanan.controller;

import com.smartpelayanan.dto.ApiResponse;
import com.smartpelayanan.dto.PengaduanDTO;
import com.smartpelayanan.entity.LampiranFile;
import com.smartpelayanan.entity.Pengaduan;
import com.smartpelayanan.entity.RiwayatStatus;
import com.smartpelayanan.enums.StatusPengaduanEnum;
import com.smartpelayanan.repository.LampiranFileRepository;
import com.smartpelayanan.repository.PengaduanRepository;
import com.smartpelayanan.repository.RiwayatStatusRepository;
import com.smartpelayanan.service.PengaduanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pengaduan")
public class PengaduanController {

    @Autowired    private PengaduanService pengaduanService;
    @Autowired    private LampiranFileRepository lampiranFileRepository;
    @Autowired    private PengaduanRepository pengaduanRepository;
    @Autowired    private RiwayatStatusRepository riwayatStatusRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PengaduanDTO>>> getAllPengaduan(
            @RequestParam(required = false) StatusPengaduanEnum status,
            @RequestParam(required = false) String prioritas) {
        List<PengaduanDTO> list = pengaduanService.findAll();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PengaduanDTO>> getPengaduanById(@PathVariable UUID id) {
        PengaduanDTO pengaduan = pengaduanService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(pengaduan));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getPengaduanDetail(@PathVariable UUID id) {
        try {
            Pengaduan pengaduan = pengaduanRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Pengaduan not found"));
            
            // Load lampiran files terkait
            List<LampiranFile> lampiranList = lampiranFileRepository.findByReferensiId(id.toString());
            List<Map<String, Object>> lampiranData = lampiranList.stream().map(l -> {
                Map<String, Object> lm = new HashMap<>();
                lm.put("id", l.getId());
                lm.put("namaFile", l.getNamaFile());
                lm.put("tipeFile", l.getTipeFile());
                lm.put("ukuranFile", l.getUkuranFile());
                lm.put("fileUrl", l.getUrlFile());
                return lm;
            }).collect(Collectors.toList());
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", pengaduan.getId());
            result.put("judul", pengaduan.getJudul());
            result.put("deskripsi", pengaduan.getDeskripsi());
            result.put("lokasi", pengaduan.getLokasi());
            result.put("status", pengaduan.getStatus() != null ? pengaduan.getStatus().name() : null);
            result.put("prioritas", pengaduan.getPrioritas() != null ? pengaduan.getPrioritas().name() : null);
            result.put("tanggalKejadian", pengaduan.getTanggalKejadian());
            result.put("catatanAdmin", pengaduan.getCatatanAdmin());
            result.put("fotoBukti", pengaduan.getFotoBukti());
            result.put("namaPelapor", pengaduan.getNamaPelapor());
            result.put("lampiran", lampiranData);

            List<RiwayatStatus> riwayatList = riwayatStatusRepository
                    .findByReferensiIdAndTipeReferensiOrderByChangedAtDesc(id, RiwayatStatus.TipeReferensiEnum.PENGADUAN);
            List<Map<String, Object>> riwayatData = riwayatList.stream().map(r -> {
                Map<String, Object> rm = new HashMap<>();
                rm.put("statusLama", r.getStatusLama());
                rm.put("statusBaru", r.getStatusBaru());
                rm.put("catatan", r.getAlasanPerubahan());
                rm.put("diubahOleh", r.getAdmin() != null ? r.getAdmin().getEmail() : "system");
                rm.put("tanggal", r.getChangedAt());
                return rm;
            }).collect(Collectors.toList());
            result.put("riwayatStatus", riwayatData);
            
            // User info for admin/warga detail view
            Map<String, Object> userMap = new HashMap<>();
            if (pengaduan.getUser() != null) {
                userMap.put("namaLengkap", pengaduan.getUser().getNamaLengkap());
                userMap.put("email", pengaduan.getUser().getEmail());
                userMap.put("noTelp", pengaduan.getUser().getNoTelp());
            } else if (pengaduan.getNamaPelapor() != null) {
                userMap.put("namaLengkap", pengaduan.getNamaPelapor());
                userMap.put("email", pengaduan.getKontakPelapor());
            }
            result.put("user", userMap);
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PengaduanDTO>> createPengaduan(@RequestBody PengaduanDTO req) {
        PengaduanDTO created = pengaduanService.save(req);
        return ResponseEntity.status(201).body(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PengaduanDTO>> updatePengaduan(
            @PathVariable UUID id, @RequestBody PengaduanDTO req) {
        PengaduanDTO updated = pengaduanService.update(id, req);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(
            @PathVariable UUID id, @RequestBody StatusUpdateRequest request) {
        pengaduanService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable UUID id) {
        pengaduanService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}/respon")
    public ResponseEntity<ApiResponse<List<Object>>> getRespon(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/respon")
    public ResponseEntity<ApiResponse<Object>> addRespon(
            @PathVariable UUID id, @RequestBody ResponRequest req) {
        return ResponseEntity.status(201).body(ApiResponse.success(null));
    }

    static class StatusUpdateRequest {
        private StatusPengaduanEnum status;
        private String catatanAdmin;

        public StatusPengaduanEnum getStatus() {
            return status;
        }

        public void setStatus(StatusPengaduanEnum status) {
            this.status = status;
        }

        public String getCatatanAdmin() {
            return catatanAdmin;
        }

        public void setCatatanAdmin(String catatanAdmin) {
            this.catatanAdmin = catatanAdmin;
        }
    }

    static class ResponRequest {
        private String isiRespon;

        public String getIsiRespon() {
            return isiRespon;
        }

        public void setIsiRespon(String isiRespon) {
            this.isiRespon = isiRespon;
        }
    }
}
