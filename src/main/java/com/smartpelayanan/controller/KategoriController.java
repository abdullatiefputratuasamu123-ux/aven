package com.smartpelayanan.controller;

import com.smartpelayanan.dto.ApiResponse;
import com.smartpelayanan.entity.KategoriLayanan;
import com.smartpelayanan.repository.KategoriLayananRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/kategori")
public class KategoriController {

    private final KategoriLayananRepository kategoriLayananRepository;

    public KategoriController(KategoriLayananRepository kategoriLayananRepository) {
        this.kategoriLayananRepository = kategoriLayananRepository;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllKategori() {
        List<KategoriLayanan> kategoris = kategoriLayananRepository.findAll();
        List<Map<String, Object>> result = kategoris.stream()
                .filter(KategoriLayanan::isIsActive)
                .map(k -> Map.<String, Object>of(
                        "id", k.getId(),
                        "nama", k.getNamaKategori(),
                        "deskripsi", k.getDeskripsi() != null ? k.getDeskripsi() : ""
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createKategori(@RequestBody Map<String, Object> request) {
        return ResponseEntity.status(201).body(ApiResponse.success(Map.of("kategori", "new-kategori")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> updateKategori(
            @PathVariable Integer id, @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(ApiResponse.success(Map.of("kategori", "updated-kategori")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteKategori(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
