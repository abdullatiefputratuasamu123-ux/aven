package com.smartpelayanan.controller;

import com.smartpelayanan.dto.ApiResponse;
import com.smartpelayanan.service.PengaduanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final PengaduanService pengaduanService;

    public DashboardController(PengaduanService pengaduanService) {
        this.pengaduanService = pengaduanService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Object>> getStats() {
        var pengaduans = pengaduanService.getAllPengaduan();
        var layanans = pengaduanService.getAllLayanan();
        
        long baru = pengaduans.stream().filter(p -> "BARU".equals(p.get("status"))).count();
        long diproses = pengaduans.stream().filter(p -> "DIPROSES".equals(p.get("status"))).count();
        long selesai = pengaduans.stream().filter(p -> "SELESAI".equals(p.get("status"))).count();
        long ditolak = pengaduans.stream().filter(p -> "DITOLAK".equals(p.get("status"))).count();
        
        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "data", Map.of(
                    "pengaduan", Map.of("baru", baru, "diproses", diproses, "selesai", selesai, "ditolak", ditolak, "total", pengaduans.size()),
                    "layanan", Map.of("total", layanans.size())
                )
        )));
    }
}
