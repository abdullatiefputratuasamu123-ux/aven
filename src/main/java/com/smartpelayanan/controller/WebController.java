package com.smartpelayanan.controller;

import com.smartpelayanan.entity.FormField;
import com.smartpelayanan.entity.JawabanForm;
import com.smartpelayanan.entity.KategoriLayanan;
import com.smartpelayanan.entity.LayananAdministrasi;
import com.smartpelayanan.entity.Notifikasi;
import com.smartpelayanan.entity.Pengaduan;
import com.smartpelayanan.entity.RiwayatStatus;
import com.smartpelayanan.entity.RiwayatStatusLayanan;
import com.smartpelayanan.entity.User;
import com.smartpelayanan.enums.PrioritasEnum;
import com.smartpelayanan.enums.RoleEnum;
import com.smartpelayanan.enums.StatusLayananEnum;
import com.smartpelayanan.enums.StatusPengaduanEnum;
import com.smartpelayanan.entity.LampiranFile;
import com.smartpelayanan.repository.FormFieldRepository;
import com.smartpelayanan.repository.JawabanFormRepository;
import com.smartpelayanan.repository.LampiranFileRepository;
import com.smartpelayanan.repository.KategoriLayananRepository;
import com.smartpelayanan.repository.LayananAdministrasiRepository;
import com.smartpelayanan.repository.NotifikasiRepository;
import com.smartpelayanan.repository.PengaduanRepository;
import com.smartpelayanan.repository.RiwayatStatusLayananRepository;
import com.smartpelayanan.repository.RiwayatStatusRepository;
import com.smartpelayanan.repository.UserRepository;
import com.smartpelayanan.service.PengaduanService;
import com.smartpelayanan.utils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class WebController {

    private final PengaduanService pengaduanService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final KategoriLayananRepository kategoriLayananRepository;
    private final LayananAdministrasiRepository layananAdministrasiRepository;
    private final PengaduanRepository pengaduanRepository;
    private final NotifikasiRepository notifikasiRepository;
    private final JawabanFormRepository jawabanFormRepository;
    private final FormFieldRepository formFieldRepository;
    private final RiwayatStatusLayananRepository riwayatStatusLayananRepository;
    private final RiwayatStatusRepository riwayatStatusRepository;
    private final LampiranFileRepository lampiranFileRepository;

    public WebController(PengaduanService pengaduanService, UserRepository userRepository,
                         JwtUtils jwtUtils, KategoriLayananRepository kategoriLayananRepository,
                         LayananAdministrasiRepository layananAdministrasiRepository,
                         PengaduanRepository pengaduanRepository,
                         NotifikasiRepository notifikasiRepository,
                         JawabanFormRepository jawabanFormRepository,
                         FormFieldRepository formFieldRepository,
                         RiwayatStatusLayananRepository riwayatStatusLayananRepository,
                         RiwayatStatusRepository riwayatStatusRepository,
                         LampiranFileRepository lampiranFileRepository) {
        this.pengaduanService = pengaduanService;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.kategoriLayananRepository = kategoriLayananRepository;
        this.layananAdministrasiRepository = layananAdministrasiRepository;
        this.pengaduanRepository = pengaduanRepository;
        this.notifikasiRepository = notifikasiRepository;
        this.jawabanFormRepository = jawabanFormRepository;
        this.formFieldRepository = formFieldRepository;
        this.riwayatStatusLayananRepository = riwayatStatusLayananRepository;
        this.riwayatStatusRepository = riwayatStatusRepository;
        this.lampiranFileRepository = lampiranFileRepository;
    }

    private void createNotifikasi(User user, String judul, String pesan, String tipe, String referensiId) {
        Notifikasi notif = new Notifikasi();
        notif.setJudul(judul);
        notif.setPesan(pesan);
        notif.setTipe(tipe);
        notif.setReferensiId(referensiId);
        notif.setSudahDibaca(false);
        notif.setUser(user);
        notifikasiRepository.save(notif);
    }

    private void notifyAdmins(String judul, String pesan, String tipe, String referensiId) {
        List<User> admins = userRepository.findByRoleIn(Arrays.asList(RoleEnum.ADMIN));
        for (User admin : admins) {
            createNotifikasi(admin, judul, pesan, tipe, referensiId);
        }
    }

    @GetMapping("/admin/pengaduan")
    public ResponseEntity<Map<String, Object>> getAllPengaduanAdmin(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            var pengaduans = pengaduanService.getAllPengaduan();
            return ResponseEntity.ok(Map.of("data", pengaduans));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/admin/layanan")
    public ResponseEntity<Map<String, Object>> getAllLayananAdmin(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            var layanans = pengaduanService.getAllLayanan();
            return ResponseEntity.ok(Map.of("data", layanans));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/layanan/{id}/detail")
    public ResponseEntity<Map<String, Object>> getLayananDetailAdmin(@PathVariable UUID id,
                                                                      @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String email = extractEmail(authHeader);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            
            LayananAdministrasi layanan = layananAdministrasiRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Layanan not found"));
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", layanan.getId());
            result.put("nomorPermohonan", layanan.getNomorPermohonan());
            result.put("keperluan", layanan.getKeperluan());
            result.put("status", layanan.getStatus() != null ? layanan.getStatus().name() : null);
            result.put("tglDiajukan", layanan.getTglDiajukan());
            result.put("tglSelesai", layanan.getTglSelesai());
            result.put("catatanPetugas", layanan.getCatatanPetugas());
            result.put("dokumenPendukung", layanan.getDokumenPendukung());
            
            Map<String, Object> userMap = new HashMap<>();
            if (layanan.getUser() != null) {
                userMap.put("namaLengkap", layanan.getUser().getNamaLengkap());
                userMap.put("email", layanan.getUser().getEmail());
                userMap.put("noTelp", layanan.getUser().getNoTelp());
            }
            result.put("user", userMap);
            
            Map<String, Object> kategoriMap = new HashMap<>();
            if (layanan.getKategori() != null) {
                kategoriMap.put("id", layanan.getKategori().getId());
                kategoriMap.put("namaKategori", layanan.getKategori().getNamaKategori());
            }
            result.put("kategori", kategoriMap);
            
            // Jawaban form
            List<JawabanForm> jawabanList = jawabanFormRepository.findByLayananId(id);
            List<Map<String, Object>> jawabanData = jawabanList.stream().map(j -> {
                Map<String, Object> jm = new HashMap<>();
                jm.put("label", j.getField() != null ? j.getField().getLabel() : "Field");
                jm.put("nilai", j.getNilai());
                jm.put("tipe", j.getField() != null ? j.getField().getTipe() : "text");
                return jm;
            }).collect(Collectors.toList());
            result.put("jawaban", jawabanData);
            
            // Lampiran files
            List<LampiranFile> lampiranList = lampiranFileRepository.findByReferensiId(id.toString());
            List<Map<String, Object>> lampiranData = lampiranList.stream().map(l -> {
                Map<String, Object> lm = new HashMap<>();
                lm.put("id", l.getId());
                lm.put("namaFile", l.getNamaFile());
                lm.put("tipeFile", l.getTipeFile());
                lm.put("fileUrl", l.getUrlFile());
                return lm;
            }).collect(Collectors.toList());
            result.put("lampiran", lampiranData);
            
            // Riwayat status
            List<RiwayatStatusLayanan> riwayatList = riwayatStatusLayananRepository.findByLayananIdOrderByCreatedAtDesc(id);
            List<Map<String, Object>> riwayatData = riwayatList.stream().map(r -> {
                Map<String, Object> rm = new HashMap<>();
                rm.put("statusLama", r.getStatusLama());
                rm.put("statusBaru", r.getStatusBaru());
                rm.put("catatan", r.getCatatan());
                rm.put("diubahOleh", r.getDiubahOleh());
                rm.put("tanggal", r.getCreatedAt());
                return rm;
            }).collect(Collectors.toList());
            result.put("riwayatStatus", riwayatData);
            
            return ResponseEntity.ok(Map.of("data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/admin/kategori")
    public ResponseEntity<Map<String, Object>> getAllKategori() {
        try {
            List<KategoriLayanan> kategoris = kategoriLayananRepository.findAll();
            List<Map<String, Object>> result = kategoris.stream().map(k -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", k.getId());
                map.put("namaKategori", k.getNamaKategori());
                map.put("deskripsi", k.getDeskripsi());
                map.put("isActive", k.isIsActive());
                return map;
            }).toList();
            return ResponseEntity.ok(Map.of("data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/admin/kategori")
    public ResponseEntity<Map<String, Object>> createKategori(@RequestBody Map<String, Object> request) {
        try {
            KategoriLayanan kategori = new KategoriLayanan();
            kategori.setNamaKategori((String) request.get("namaKategori"));
            kategori.setDeskripsi((String) request.get("deskripsi"));
            kategori.setIsActive(true);
            kategori = kategoriLayananRepository.save(kategori);
            Map<String, Object> result = new HashMap<>();
            result.put("id", kategori.getId());
            result.put("namaKategori", kategori.getNamaKategori());
            return ResponseEntity.ok(Map.of("data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/admin/kategori/{id}")
    public ResponseEntity<Map<String, Object>> updateKategori(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {
            KategoriLayanan kategori = kategoriLayananRepository.findById(id).orElseThrow(() -> new RuntimeException("Kategori not found"));
            if (request.containsKey("namaKategori")) kategori.setNamaKategori((String) request.get("namaKategori"));
            if (request.containsKey("deskripsi")) kategori.setDeskripsi((String) request.get("deskripsi"));
            if (request.containsKey("isActive")) kategori.setIsActive((Boolean) request.get("isActive"));
            kategoriLayananRepository.save(kategori);
            return ResponseEntity.ok(Map.of("data", Map.of("success", true)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/kategori/{id}")
    public ResponseEntity<Map<String, Object>> deleteKategori(@PathVariable Integer id) {
        try {
            kategoriLayananRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("data", Map.of("success", true)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/warga/pengaduan")
    public ResponseEntity<Map<String, Object>> createPengaduan(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                               @RequestBody Map<String, Object> request) {
        try {
            String email = extractEmail(authHeader);
            if (email == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            
            Pengaduan pengaduan = new Pengaduan();
            pengaduan.setJudul((String) request.get("judul"));
            pengaduan.setDeskripsi((String) request.get("deskripsi"));
            pengaduan.setLokasi((String) request.get("lokasi"));
            pengaduan.setPrioritas(PrioritasEnum.valueOf((String) request.getOrDefault("prioritas", "SEDANG")));
            pengaduan.setStatus(StatusPengaduanEnum.BARU);
            pengaduan.setUser(user);
            pengaduan.setTanggalKejadian(LocalDate.now());
            
            pengaduan = pengaduanRepository.save(pengaduan);

            // Notify all admins about new pengaduan
            notifyAdmins("Pengaduan Baru",
                    "Ada pengaduan baru dari " + user.getNamaLengkap() + ": " + pengaduan.getJudul(),
                    "PENGADUAN_BARU", pengaduan.getId().toString());

            Map<String, Object> result = new HashMap<>();
            result.put("id", pengaduan.getId());
            result.put("judul", pengaduan.getJudul());
            result.put("status", pengaduan.getStatus().name());
            return ResponseEntity.ok(Map.of("data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/warga/layanan")
    public ResponseEntity<Map<String, Object>> createLayanan(@RequestHeader(value = "Authorization", required = false) String authHeader,
                                                             @RequestBody Map<String, Object> request) {
        try {
            String email = extractEmail(authHeader);
            if (email == null) return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            
            Integer kategoriId = (Integer) request.get("kategoriId");
            KategoriLayanan kategori = kategoriLayananRepository.findById(kategoriId).orElseThrow(() -> new RuntimeException("Kategori not found"));
            
            LayananAdministrasi layanan = new LayananAdministrasi();
            layanan.setNomorPermohonan("PLY-" + System.currentTimeMillis());
            layanan.setKeperluan((String) request.get("keperluan"));
            layanan.setDokumenPendukung((String) request.get("dokumenPendukung"));
            layanan.setStatus(StatusLayananEnum.MENUNGGU);
            layanan.setUser(user);
            layanan.setKategori(kategori);
            layanan.setTglDiajukan(LocalDate.now());
            
            layanan = layananAdministrasiRepository.save(layanan);

            // Simpan jawaban form jika ada
            @SuppressWarnings("unchecked")
            Map<String, String> jawaban = (Map<String, String>) request.get("jawaban");
            if (jawaban != null) {
                final LayananAdministrasi savedLayanan = layanan;
                for (Map.Entry<String, String> entry : jawaban.entrySet()) {
                    try {
                        UUID fieldId = UUID.fromString(entry.getKey());
                        formFieldRepository.findById(fieldId).ifPresent(field -> {
                            JawabanForm jawabanForm = new JawabanForm();
                            jawabanForm.setLayanan(savedLayanan);
                            jawabanForm.setField(field);
                            jawabanForm.setNilai(entry.getValue());
                            jawabanFormRepository.save(jawabanForm);
                        });
                    } catch (IllegalArgumentException ignored) {
                        // skip invalid UUID keys
                    }
                }
            }

            // Notify all admins about new layanan
            notifyAdmins("Permohonan Layanan Baru",
                    "Ada permohonan layanan baru dari " + user.getNamaLengkap() + ": " + layanan.getKeperluan(),
                    "LAYANAN_BARU", layanan.getId().toString());

            Map<String, Object> result = new HashMap<>();
            result.put("id", layanan.getId());
            result.put("nomorPermohonan", layanan.getNomorPermohonan());
            result.put("status", layanan.getStatus().name());
            return ResponseEntity.ok(Map.of("data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/admin/pengaduan/{id}/status")
    public ResponseEntity<Map<String, Object>> updatePengaduanStatus(@PathVariable UUID id,
                                                                    @RequestHeader(value = "Authorization", required = false) String authHeader,
                                                                    @RequestBody Map<String, Object> request) {
        try {
            String statusStr = (String) request.get("status");
            String catatan = (String) request.get("catatanAdmin");
            
            // Get old status before update
            String statusLama = pengaduanRepository.findById(id)
                    .map(p -> p.getStatus() != null ? p.getStatus().name() : null)
                    .orElse(null);

            pengaduanService.updateStatus(id, StatusPengaduanEnum.valueOf(statusStr));

            pengaduanRepository.findById(id).ifPresent(pengaduan -> {
                if (catatan != null) {
                    pengaduan.setCatatanAdmin(catatan);
                    pengaduanRepository.save(pengaduan);
                }
                // Notify pengaduan owner (if not public)
                if (pengaduan.getUser() != null) {
                    createNotifikasi(pengaduan.getUser(),
                            "Status Pengaduan Diperbarui",
                            "Pengaduan '" + pengaduan.getJudul() + "' sekarang berstatus " + statusStr,
                            "STATUS_PENGADUAN", pengaduan.getId().toString());
                }
            });

            // Simpan riwayat status
            String adminEmail = extractEmail(authHeader);
            User admin = userRepository.findByEmail(adminEmail != null ? adminEmail : "")
                    .orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));
            RiwayatStatus riwayat = new RiwayatStatus();
            riwayat.setReferensiId(id);
            riwayat.setTipeReferensi(RiwayatStatus.TipeReferensiEnum.PENGADUAN);
            riwayat.setStatusLama(statusLama);
            riwayat.setStatusBaru(statusStr);
            riwayat.setAlasanPerubahan(catatan);
            riwayat.setChangedAt(LocalDateTime.now());
            riwayat.setAdmin(admin);
            riwayatStatusRepository.save(riwayat);

            return ResponseEntity.ok(Map.of("data", Map.of("success", true)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/admin/layanan/{id}/status")
    public ResponseEntity<Map<String, Object>> updateLayananStatus(@PathVariable UUID id,
                                                                    @RequestHeader(value = "Authorization", required = false) String authHeader,
                                                                    @RequestBody Map<String, Object> request) {
        try {
            LayananAdministrasi layanan = layananAdministrasiRepository.findById(id).orElseThrow(() -> new RuntimeException("Layanan not found"));
            String statusLama = layanan.getStatus() != null ? layanan.getStatus().name() : null;
            String statusStr = (String) request.get("status");
            String catatan = (String) request.get("catatanPetugas");
            
            layanan.setStatus(StatusLayananEnum.valueOf(statusStr));
            if (catatan != null) layanan.setCatatanPetugas(catatan);
            if ("SELESAI".equals(statusStr)) layanan.setTglSelesai(LocalDate.now());

            layananAdministrasiRepository.save(layanan);

            // Simpan riwayat status
            String adminEmail = extractEmail(authHeader);
            RiwayatStatusLayanan riwayat = new RiwayatStatusLayanan();
            riwayat.setLayanan(layanan);
            riwayat.setStatusLama(statusLama);
            riwayat.setStatusBaru(statusStr);
            riwayat.setCatatan(catatan);
            riwayat.setDiubahOleh(adminEmail != null ? adminEmail : "system");
            riwayatStatusLayananRepository.save(riwayat);

            // Notify layanan owner
            if (layanan.getUser() != null) {
                createNotifikasi(layanan.getUser(),
                        "Status Layanan Diperbarui",
                        "Permohonan '" + layanan.getNomorPermohonan() + "' sekarang berstatus " + statusStr,
                        "STATUS_LAYANAN", layanan.getId().toString());
            }

            return ResponseEntity.ok(Map.of("data", Map.of("success", true)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/pengaduan/{id}")
    public ResponseEntity<Map<String, Object>> deletePengaduan(@PathVariable UUID id) {
        try {
            pengaduanService.delete(id);
            return ResponseEntity.ok(Map.of("data", Map.of("success", true)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/warga/pengaduan")
    public ResponseEntity<Map<String, Object>> getMyPengaduan(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String email = extractEmail(authHeader);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            var pengaduans = pengaduanService.getPengaduanByUserId(user.getId());
            return ResponseEntity.ok(Map.of("data", pengaduans));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/warga/layanan")
    public ResponseEntity<Map<String, Object>> getMyLayanan(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String email = extractEmail(authHeader);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            var layanans = pengaduanService.getLayananByUserId(user.getId());
            return ResponseEntity.ok(Map.of("data", layanans));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/warga/layanan/{id}")
    public ResponseEntity<Map<String, Object>> getMyLayananDetail(@PathVariable UUID id,
                                                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String email = extractEmail(authHeader);
            if (email == null) {
                return ResponseEntity.status(401).body(Map.of("message", "Unauthorized"));
            }
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
            
            LayananAdministrasi layanan = layananAdministrasiRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Layanan not found"));
            
            // Verify ownership
            if (!layanan.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("message", "Forbidden"));
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("id", layanan.getId());
            result.put("nomorPermohonan", layanan.getNomorPermohonan());
            result.put("keperluan", layanan.getKeperluan());
            result.put("status", layanan.getStatus() != null ? layanan.getStatus().name() : null);
            result.put("tglDiajukan", layanan.getTglDiajukan());
            result.put("tglSelesai", layanan.getTglSelesai());
            result.put("catatanPetugas", layanan.getCatatanPetugas());
            result.put("dokumenPendukung", layanan.getDokumenPendukung());
            
            Map<String, Object> kategoriMap = new HashMap<>();
            if (layanan.getKategori() != null) {
                kategoriMap.put("id", layanan.getKategori().getId());
                kategoriMap.put("namaKategori", layanan.getKategori().getNamaKategori());
            }
            result.put("kategori", kategoriMap);
            
            // Jawaban form
            List<JawabanForm> jawabanList = jawabanFormRepository.findByLayananId(id);
            List<Map<String, Object>> jawabanData = jawabanList.stream().map(j -> {
                Map<String, Object> jm = new HashMap<>();
                jm.put("field", Map.of("label", j.getField() != null ? j.getField().getLabel() : "Field"));
                jm.put("fieldId", j.getField() != null ? j.getField().getId().toString() : "");
                jm.put("jawaban", j.getNilai());
                return jm;
            }).collect(Collectors.toList());
            result.put("jawabanData", jawabanData);
            
            // Lampiran files
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
            result.put("lampiran", lampiranData);
            
            // Riwayat status
            List<RiwayatStatusLayanan> riwayatList = riwayatStatusLayananRepository.findByLayananIdOrderByCreatedAtDesc(id);
            List<Map<String, Object>> riwayatData = riwayatList.stream().map(r -> {
                Map<String, Object> rm = new HashMap<>();
                rm.put("statusLama", r.getStatusLama());
                rm.put("statusBaru", r.getStatusBaru());
                rm.put("catatan", r.getCatatan());
                rm.put("diubahOleh", r.getDiubahOleh());
                rm.put("waktu", r.getCreatedAt());
                return rm;
            }).collect(Collectors.toList());
            result.put("riwayatStatus", riwayatData);
            
            return ResponseEntity.ok(Map.of("data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/kategori/list")
    public ResponseEntity<Map<String, Object>> getKategoriList() {
        try {
            List<KategoriLayanan> kategoris = kategoriLayananRepository.findAll();
            List<Map<String, Object>> result = kategoris.stream().map(k -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", k.getId());
                map.put("namaKategori", k.getNamaKategori());
                return map;
            }).toList();
            return ResponseEntity.ok(Map.of("data", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private String extractEmail(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtils.getEmailFromToken(token);
        }
        return null;
    }
}
