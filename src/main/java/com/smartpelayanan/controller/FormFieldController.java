package com.smartpelayanan.controller;

import com.smartpelayanan.entity.FormField;
import com.smartpelayanan.entity.KategoriLayanan;
import com.smartpelayanan.repository.FormFieldRepository;
import com.smartpelayanan.repository.KategoriLayananRepository;
import com.smartpelayanan.utils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class FormFieldController {

    private final FormFieldRepository formFieldRepository;
    private final KategoriLayananRepository kategoriLayananRepository;
    private final JwtUtils jwtUtils;

    public FormFieldController(FormFieldRepository formFieldRepository,
                               KategoriLayananRepository kategoriLayananRepository,
                               JwtUtils jwtUtils) {
        this.formFieldRepository = formFieldRepository;
        this.kategoriLayananRepository = kategoriLayananRepository;
        this.jwtUtils = jwtUtils;
    }

    // GET /api/v1/kategori/{id}/fields — list field untuk kategori (public/authenticated)
    @GetMapping("/kategori/{id}/fields")
    public ResponseEntity<Map<String, Object>> getFieldsByKategori(@PathVariable Integer id) {
        try {
            List<FormField> fields = formFieldRepository.findByKategoriIdOrderByUrutanAsc(id);
            List<Map<String, Object>> result = fields.stream().map(this::fieldToMap).toList();
            return ResponseEntity.ok(Map.of("data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // POST /api/v1/admin/kategori/{id}/fields — tambah field baru
    @PostMapping("/admin/kategori/{id}/fields")
    public ResponseEntity<Map<String, Object>> addField(@PathVariable Integer id,
                                                         @RequestBody Map<String, Object> request) {
        try {
            KategoriLayanan kategori = kategoriLayananRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Kategori not found"));

            String tipe = (String) request.get("tipe");
            validateTipe(tipe);

            String opsi = (String) request.get("opsi");
            if ("select".equals(tipe) && (opsi == null || opsi.isBlank())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Opsi wajib diisi untuk tipe select"));
            }

            FormField field = new FormField();
            field.setLabel((String) request.get("label"));
            field.setTipe(tipe);
            field.setRequired(request.get("required") != null ? (Boolean) request.get("required") : true);
            field.setUrutan(request.get("urutan") != null ? ((Number) request.get("urutan")).intValue() : 0);
            field.setOpsi(opsi);
            field.setPlaceholder((String) request.get("placeholder"));
            field.setKategori(kategori);

            field = formFieldRepository.save(field);
            return ResponseEntity.ok(Map.of("data", fieldToMap(field)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // PUT /api/v1/admin/fields/{fieldId} — update field
    @PutMapping("/admin/fields/{fieldId}")
    public ResponseEntity<Map<String, Object>> updateField(@PathVariable UUID fieldId,
                                                            @RequestBody Map<String, Object> request) {
        try {
            FormField field = formFieldRepository.findById(fieldId)
                    .orElseThrow(() -> new RuntimeException("Field not found"));

            if (request.containsKey("label")) field.setLabel((String) request.get("label"));
            if (request.containsKey("tipe")) {
                String tipe = (String) request.get("tipe");
                validateTipe(tipe);
                field.setTipe(tipe);
            }
            if (request.containsKey("required")) field.setRequired((Boolean) request.get("required"));
            if (request.containsKey("urutan")) field.setUrutan(((Number) request.get("urutan")).intValue());
            if (request.containsKey("opsi")) field.setOpsi((String) request.get("opsi"));
            if (request.containsKey("placeholder")) field.setPlaceholder((String) request.get("placeholder"));

            field = formFieldRepository.save(field);
            return ResponseEntity.ok(Map.of("data", fieldToMap(field)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // DELETE /api/v1/admin/fields/{fieldId} — hapus field
    @DeleteMapping("/admin/fields/{fieldId}")
    public ResponseEntity<Map<String, Object>> deleteField(@PathVariable UUID fieldId) {
        try {
            formFieldRepository.deleteById(fieldId);
            return ResponseEntity.ok(Map.of("data", Map.of("success", true)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // PUT /api/v1/admin/kategori/{id}/fields/reorder — update urutan semua field
    @PutMapping("/admin/kategori/{id}/fields/reorder")
    public ResponseEntity<Map<String, Object>> reorderFields(@PathVariable Integer id,
                                                              @RequestBody List<Map<String, Object>> reorderList) {
        try {
            for (Map<String, Object> item : reorderList) {
                UUID fieldId = UUID.fromString((String) item.get("id"));
                int urutan = ((Number) item.get("urutan")).intValue();
                formFieldRepository.findById(fieldId).ifPresent(field -> {
                    field.setUrutan(urutan);
                    formFieldRepository.save(field);
                });
            }
            return ResponseEntity.ok(Map.of("data", Map.of("success", true)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private void validateTipe(String tipe) {
        List<String> validTipes = Arrays.asList("text", "number", "textarea", "select", "date", "file");
        if (tipe == null || !validTipes.contains(tipe)) {
            throw new RuntimeException("Tipe tidak valid. Harus salah satu dari: text, number, textarea, select, date, file");
        }
    }

    private Map<String, Object> fieldToMap(FormField field) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", field.getId());
        map.put("label", field.getLabel());
        map.put("tipe", field.getTipe());
        map.put("required", field.getRequired());
        map.put("urutan", field.getUrutan());
        map.put("opsi", field.getOpsi());
        map.put("placeholder", field.getPlaceholder());
        return map;
    }
}
