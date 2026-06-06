package com.smartpelayanan.controller;

import com.smartpelayanan.dto.ApiResponse;
import com.smartpelayanan.entity.User;
import com.smartpelayanan.enums.RoleEnum;
import com.smartpelayanan.repository.LayananAdministrasiRepository;
import com.smartpelayanan.repository.PengaduanRepository;
import com.smartpelayanan.repository.UserRepository;
import com.smartpelayanan.service.PengaduanService;
import com.smartpelayanan.utils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/superadmin")
public class SuperAdminController {

    private final UserRepository userRepository;
    private final PengaduanRepository pengaduanRepository;
    private final LayananAdministrasiRepository layananAdministrasiRepository;
    private final PengaduanService pengaduanService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminController(UserRepository userRepository,
                                PengaduanRepository pengaduanRepository,
                                LayananAdministrasiRepository layananAdministrasiRepository,
                                PengaduanService pengaduanService,
                                JwtUtils jwtUtils,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.pengaduanRepository = pengaduanRepository;
        this.layananAdministrasiRepository = layananAdministrasiRepository;
        this.pengaduanService = pengaduanService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    private User getCurrentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String email = jwtUtils.getEmailFromToken(authHeader.substring(7));
        if (email == null) return null;
        return userRepository.findByEmail(email).orElse(null);
    }

    private boolean isSuperAdmin(String authHeader) {
        User user = getCurrentUser(authHeader);
        return user != null && user.getRole() == RoleEnum.SUPERADMIN;
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isSuperAdmin(authHeader)) return ResponseEntity.status(403).body(ApiResponse.error("Forbidden"));

        List<Map<String, Object>> result = userRepository.findAll().stream().map(u -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", u.getId());
            m.put("namaLengkap", u.getNamaLengkap());
            m.put("email", u.getEmail());
            m.put("noTelp", u.getNoTelp());
            m.put("role", u.getRole().name());
            m.put("statusAktif", u.isStatusAktif());
            m.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : null);
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createAdmin(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody Map<String, Object> request) {
        if (!isSuperAdmin(authHeader)) return ResponseEntity.status(403).body(ApiResponse.error("Forbidden"));

        String email = (String) request.get("email");
        if (email == null || email.isBlank()) return ResponseEntity.badRequest().body(ApiResponse.error("Email wajib diisi"));
        if (userRepository.existsByEmail(email)) return ResponseEntity.badRequest().body(ApiResponse.error("Email sudah terdaftar"));

        User admin = new User();
        admin.setNamaLengkap((String) request.getOrDefault("namaLengkap", "Admin Baru"));
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode((String) request.getOrDefault("password", "admin123")));
        admin.setNoTelp((String) request.getOrDefault("noTelp", ""));
        admin.setAlamat((String) request.getOrDefault("alamat", ""));
        admin.setRole(RoleEnum.ADMIN);
        admin.setStatusAktif(true);
        userRepository.save(admin);

        Map<String, Object> result = new HashMap<>();
        result.put("id", admin.getId());
        result.put("email", admin.getEmail());
        result.put("namaLengkap", admin.getNamaLengkap());
        result.put("role", admin.getRole().name());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/users/{id}/toggle-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleStatus(
            @PathVariable UUID id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isSuperAdmin(authHeader)) return ResponseEntity.status(403).body(ApiResponse.error("Forbidden"));

        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        user.setStatusAktif(!user.isStatusAktif());
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "id", user.getId(),
                "statusAktif", user.isStatusAktif()
        )));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isSuperAdmin(authHeader)) return ResponseEntity.status(403).body(ApiResponse.error("Forbidden"));

        long totalWarga = userRepository.findByRole(RoleEnum.WARGA).size();
        long totalAdmin = userRepository.findByRole(RoleEnum.ADMIN).size();
        long totalSuperAdmin = userRepository.findByRole(RoleEnum.SUPERADMIN).size();

        var pengaduans = pengaduanService.getAllPengaduan();
        long pengBaru = pengaduans.stream().filter(p -> "BARU".equals(p.get("status"))).count();
        long pengDiproses = pengaduans.stream().filter(p -> "DIPROSES".equals(p.get("status"))).count();
        long pengSelesai = pengaduans.stream().filter(p -> "SELESAI".equals(p.get("status"))).count();
        long pengDitolak = pengaduans.stream().filter(p -> "DITOLAK".equals(p.get("status"))).count();

        var layanans = pengaduanService.getAllLayanan();

        return ResponseEntity.ok(ApiResponse.success(Map.of(
                "users", Map.of("warga", totalWarga, "admin", totalAdmin, "superadmin", totalSuperAdmin,
                        "total", totalWarga + totalAdmin + totalSuperAdmin),
                "pengaduan", Map.of("baru", pengBaru, "diproses", pengDiproses, "selesai", pengSelesai,
                        "ditolak", pengDitolak, "total", (long) pengaduans.size()),
                "layanan", Map.of("total", (long) layanans.size())
        )));
    }

    @GetMapping("/pengaduan")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllPengaduan(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isSuperAdmin(authHeader)) return ResponseEntity.status(403).body(ApiResponse.error("Forbidden"));
        return ResponseEntity.ok(ApiResponse.success(pengaduanService.getAllPengaduan()));
    }

    @GetMapping("/layanan")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllLayanan(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isSuperAdmin(authHeader)) return ResponseEntity.status(403).body(ApiResponse.error("Forbidden"));
        return ResponseEntity.ok(ApiResponse.success(pengaduanService.getAllLayanan()));
    }
}
