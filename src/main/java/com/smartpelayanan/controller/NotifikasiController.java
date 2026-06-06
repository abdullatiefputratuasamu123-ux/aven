package com.smartpelayanan.controller;

import com.smartpelayanan.dto.ApiResponse;
import com.smartpelayanan.entity.Notifikasi;
import com.smartpelayanan.entity.User;
import com.smartpelayanan.repository.NotifikasiRepository;
import com.smartpelayanan.repository.UserRepository;
import com.smartpelayanan.utils.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/notifikasi")
public class NotifikasiController {

    private final NotifikasiRepository notifikasiRepository;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public NotifikasiController(NotifikasiRepository notifikasiRepository,
                                UserRepository userRepository,
                                JwtUtils jwtUtils) {
        this.notifikasiRepository = notifikasiRepository;
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    private User getCurrentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        String email = jwtUtils.getEmailFromToken(token);
        if (email == null) return null;
        return userRepository.findByEmail(email).orElse(null);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getNotifikasi(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getCurrentUser(authHeader);
        if (user == null) return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));

        List<Notifikasi> list = notifikasiRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        // Limit to 50
        if (list.size() > 50) list = list.subList(0, 50);

        List<Map<String, Object>> result = list.stream().map(n -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", n.getId());
            m.put("judul", n.getJudul());
            m.put("pesan", n.getPesan());
            m.put("tipe", n.getTipe());
            m.put("referensiId", n.getReferensiId());
            m.put("sudahDibaca", n.getSudahDibaca());
            m.put("createdAt", n.getCreatedAt() != null ? n.getCreatedAt().toString() : null);
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnreadCount(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getCurrentUser(authHeader);
        if (user == null) return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));

        long count = notifikasiRepository.countByUserIdAndSudahDibacaFalse(user.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", count)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(
            @PathVariable UUID id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getCurrentUser(authHeader);
        if (user == null) return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));

        notifikasiRepository.findById(id).ifPresent(n -> {
            if (n.getUser().getId().equals(user.getId())) {
                n.setSudahDibaca(true);
                notifikasiRepository.save(n);
            }
        });
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        User user = getCurrentUser(authHeader);
        if (user == null) return ResponseEntity.status(401).body(ApiResponse.error("Unauthorized"));

        List<Notifikasi> unread = notifikasiRepository.findByUserId(user.getId());
        unread.forEach(n -> n.setSudahDibaca(true));
        notifikasiRepository.saveAll(unread);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
