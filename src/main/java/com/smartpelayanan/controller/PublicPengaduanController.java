package com.smartpelayanan.controller;

import com.smartpelayanan.dto.ApiResponse;
import com.smartpelayanan.entity.LampiranFile;
import com.smartpelayanan.entity.Notifikasi;
import com.smartpelayanan.entity.Pengaduan;
import com.smartpelayanan.entity.User;
import com.smartpelayanan.enums.PrioritasEnum;
import com.smartpelayanan.enums.RoleEnum;
import com.smartpelayanan.enums.StatusPengaduanEnum;
import com.smartpelayanan.repository.LampiranFileRepository;
import com.smartpelayanan.repository.NotifikasiRepository;
import com.smartpelayanan.repository.PengaduanRepository;
import com.smartpelayanan.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/v1/public")
public class PublicPengaduanController {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(
            Arrays.asList("jpg", "jpeg", "png", "gif", "webp"));

    private final PengaduanRepository pengaduanRepository;
    private final UserRepository userRepository;
    private final NotifikasiRepository notifikasiRepository;
    private final LampiranFileRepository lampiranFileRepository;

    public PublicPengaduanController(PengaduanRepository pengaduanRepository,
                                     UserRepository userRepository,
                                     NotifikasiRepository notifikasiRepository,
                                     LampiranFileRepository lampiranFileRepository) {
        this.pengaduanRepository = pengaduanRepository;
        this.userRepository = userRepository;
        this.notifikasiRepository = notifikasiRepository;
        this.lampiranFileRepository = lampiranFileRepository;
    }

    /**
     * Endpoint upload foto untuk publik (tanpa login).
     * Menyimpan file ke direktori uploads dan mengembalikan URL file.
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadPublicFile(
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("File tidak boleh kosong"));
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Ukuran file maksimal 10MB"));
            }

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
            }
            if (!ALLOWED_IMAGE_EXTENSIONS.contains(ext)) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("Tipe file tidak diizinkan. Gunakan: jpg, jpeg, png, gif, webp"));
            }

            Path uploadDir = Paths.get("uploads").toAbsolutePath();
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String savedName = UUID.randomUUID().toString() + "." + ext;
            Path targetPath = uploadDir.resolve(savedName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String urlFile = "/uploads/" + savedName;

            // Simpan metadata lampiran (tanpa referensiId dulu, akan dikaitkan setelah pengaduan dibuat)
            LampiranFile lampiran = new LampiranFile();
            lampiran.setNamaFile(originalName);
            lampiran.setNamaTersimpan(savedName);
            lampiran.setTipeFile(file.getContentType());
            lampiran.setUkuranFile(file.getSize());
            lampiran.setUrlFile(urlFile);
            lampiran.setTipeReferensi("PENGADUAN_PUBLIK");
            lampiranFileRepository.save(lampiran);

            Map<String, Object> result = new HashMap<>();
            result.put("urlFile", urlFile);
            result.put("namaFile", originalName);
            result.put("lampiranId", lampiran.getId());

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                    ApiResponse.error("Gagal menyimpan file: " + e.getMessage()));
        }
    }

    /**
     * Endpoint submit pengaduan publik (tanpa login).
     * Menerima fotoBukti (URL string dari upload sebelumnya).
     */
    @PostMapping("/pengaduan")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createPublicPengaduan(
            @RequestBody Map<String, Object> request) {
        try {
            String namaPelapor = (String) request.get("namaPelapor");
            String kontakPelapor = (String) request.get("kontakPelapor");
            String judul = (String) request.get("judul");
            String deskripsi = (String) request.get("deskripsi");

            if (namaPelapor == null || namaPelapor.isBlank()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Nama pelapor wajib diisi"));
            }
            if (kontakPelapor == null || kontakPelapor.isBlank()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Kontak pelapor wajib diisi"));
            }
            if (judul == null || judul.isBlank()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Judul pengaduan wajib diisi"));
            }
            if (deskripsi == null || deskripsi.isBlank()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Deskripsi pengaduan wajib diisi"));
            }

            Pengaduan pengaduan = new Pengaduan();
            pengaduan.setJudul(judul);
            pengaduan.setDeskripsi(deskripsi);
            pengaduan.setLokasi((String) request.getOrDefault("lokasi", ""));
            pengaduan.setNamaPelapor(namaPelapor);
            pengaduan.setKontakPelapor(kontakPelapor);
            pengaduan.setStatus(StatusPengaduanEnum.BARU);
            pengaduan.setTanggalKejadian(LocalDate.now());

            // Simpan foto bukti jika ada (URL dari upload /api/v1/public/upload)
            String fotoBukti = (String) request.get("fotoBukti");
            if (fotoBukti != null && !fotoBukti.isBlank()) {
                pengaduan.setFotoBukti(fotoBukti);
            }

            String prioritasStr = (String) request.getOrDefault("prioritas", "SEDANG");
            try {
                pengaduan.setPrioritas(PrioritasEnum.valueOf(prioritasStr));
            } catch (IllegalArgumentException e) {
                pengaduan.setPrioritas(PrioritasEnum.SEDANG);
            }
            pengaduan.setUser(null);
            pengaduan = pengaduanRepository.save(pengaduan);

            // Kaitkan lampiran yang baru diupload dengan pengaduan ini
            if (fotoBukti != null && !fotoBukti.isBlank()) {
                String savedName = fotoBukti.replace("/uploads/", "");
                List<LampiranFile> lampiranList = lampiranFileRepository
                        .findByNamaTersimpan(savedName);
                for (LampiranFile lf : lampiranList) {
                    if (lf.getReferensiId() == null) {
                        lf.setReferensiId(pengaduan.getId().toString());
                        lampiranFileRepository.save(lf);
                    }
                }
            }

            // Notify all ADMIN
            final Pengaduan savedPengaduan = pengaduan;
            List<User> admins = userRepository.findByRoleIn(
                    Arrays.asList(RoleEnum.ADMIN));
            for (User admin : admins) {
                Notifikasi notif = new Notifikasi();
                notif.setJudul("Pengaduan Publik Baru");
                notif.setPesan("Ada pengaduan baru dari " + namaPelapor + ": " + judul);
                notif.setTipe("PENGADUAN_BARU");
                notif.setReferensiId(savedPengaduan.getId().toString());
                notif.setSudahDibaca(false);
                notif.setUser(admin);
                notifikasiRepository.save(notif);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("id", pengaduan.getId());
            result.put("judul", pengaduan.getJudul());
            result.put("status", pengaduan.getStatus().name());
            result.put("namaPelapor", pengaduan.getNamaPelapor());
            result.put("tanggalKejadian", pengaduan.getTanggalKejadian().toString());
            result.put("fotoBukti", pengaduan.getFotoBukti());

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
