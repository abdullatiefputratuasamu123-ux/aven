package com.smartpelayanan.service.impl;

import com.smartpelayanan.dto.PengaduanDTO;
import com.smartpelayanan.entity.Pengaduan;
import com.smartpelayanan.entity.User;
import com.smartpelayanan.entity.LayananAdministrasi;
import com.smartpelayanan.enums.StatusPengaduanEnum;
import com.smartpelayanan.repository.PengaduanRepository;
import com.smartpelayanan.repository.RiwayatStatusRepository;
import com.smartpelayanan.repository.UserRepository;
import com.smartpelayanan.repository.LayananAdministrasiRepository;
import com.smartpelayanan.service.PengaduanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Service
public class PengaduanServiceImpl implements PengaduanService {

    @Autowired
    private PengaduanRepository pengaduanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RiwayatStatusRepository riwayatStatusRepository;

    @Autowired
    private LayananAdministrasiRepository layananAdministrasiRepository;

    @Override
    public List<PengaduanDTO> findAll() {
        return pengaduanRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PengaduanDTO findById(UUID id) {
        Pengaduan pengaduan = pengaduanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengaduan not found"));
        return convertToDTO(pengaduan);
    }

    @Override
    public PengaduanDTO save(PengaduanDTO pengaduanDTO) {
        Pengaduan pengaduan = convertToEntity(pengaduanDTO);
        pengaduan = pengaduanRepository.save(pengaduan);
        return convertToDTO(pengaduan);
    }

    @Override
    public PengaduanDTO update(UUID id, PengaduanDTO pengaduanDTO) {
        Pengaduan pengaduan = pengaduanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengaduan not found"));
        pengaduan.setJudul(pengaduanDTO.getJudul());
        pengaduan.setDeskripsi(pengaduanDTO.getDeskripsi());
        pengaduan.setLokasi(pengaduanDTO.getLokasi());
        pengaduan = pengaduanRepository.save(pengaduan);
        return convertToDTO(pengaduan);
    }

    @Override
    public void updateStatus(UUID id, StatusPengaduanEnum status) {
        Pengaduan pengaduan = pengaduanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengaduan not found"));
        pengaduan.setStatus(status);
        pengaduanRepository.save(pengaduan);
    }

    @Override
    public void delete(UUID id) {
        pengaduanRepository.deleteById(id);
    }

    public void validateOwnership(UUID id, UUID userId) {
        Pengaduan pengaduan = pengaduanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pengaduan not found"));
        if (!pengaduan.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
    }

    private PengaduanDTO convertToDTO(Pengaduan pengaduan) {
        PengaduanDTO dto = new PengaduanDTO();
        dto.setId(pengaduan.getId());
        dto.setJudul(pengaduan.getJudul());
        dto.setDeskripsi(pengaduan.getDeskripsi());
        dto.setLokasi(pengaduan.getLokasi());
        dto.setStatus(pengaduan.getStatus());
        dto.setPrioritas(pengaduan.getPrioritas());
        dto.setFotoBukti(pengaduan.getFotoBukti());
        dto.setTanggalKejadian(pengaduan.getTanggalKejadian());
        dto.setCatatanAdmin(pengaduan.getCatatanAdmin());
        dto.setNamaUser(pengaduan.getUser() != null ? pengaduan.getUser().getNamaLengkap() : pengaduan.getNamaPelapor());
        return dto;
    }

    private Pengaduan convertToEntity(PengaduanDTO dto) {
        Pengaduan pengaduan = new Pengaduan();
        pengaduan.setJudul(dto.getJudul());
        pengaduan.setDeskripsi(dto.getDeskripsi());
        pengaduan.setLokasi(dto.getLokasi());
        pengaduan.setPrioritas(dto.getPrioritas());
        pengaduan.setFotoBukti(dto.getFotoBukti());
        pengaduan.setTanggalKejadian(dto.getTanggalKejadian());
        if (dto.getNamaUser() != null) {
            User user = userRepository.findByEmail(dto.getNamaUser())
                    .orElse(null);
            if (user != null) {
                pengaduan.setUser(user);
            }
        }
        return pengaduan;
    }

    @Override
    public List<Map<String, Object>> getAllPengaduan() {
        return pengaduanRepository.findAll().stream()
                .map(this::convertPengaduanToMap)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getPengaduanByUserId(UUID userId) {
        return pengaduanRepository.findByUserId(userId).stream()
                .map(this::convertPengaduanToMap)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getLayananByUserId(UUID userId) {
        return layananAdministrasiRepository.findByUserId(userId).stream()
                .map(this::convertLayananToMap)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAllLayanan() {
        return layananAdministrasiRepository.findAll().stream()
                .map(this::convertLayananToMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> convertPengaduanToMap(Pengaduan pengaduan) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", pengaduan.getId());
        map.put("judul", pengaduan.getJudul());
        map.put("deskripsi", pengaduan.getDeskripsi());
        map.put("lokasi", pengaduan.getLokasi());
        map.put("status", pengaduan.getStatus() != null ? pengaduan.getStatus().name() : null);
        map.put("prioritas", pengaduan.getPrioritas() != null ? pengaduan.getPrioritas().name() : null);
        map.put("tanggalKejadian", pengaduan.getTanggalKejadian());
        map.put("catatanAdmin", pengaduan.getCatatanAdmin());
        map.put("fotoBukti", pengaduan.getFotoBukti());
        
        Map<String, Object> userMap = new HashMap<>();
        if (pengaduan.getUser() != null) {
            userMap.put("id", pengaduan.getUser().getId());
            userMap.put("namaLengkap", pengaduan.getUser().getNamaLengkap());
            userMap.put("email", pengaduan.getUser().getEmail());
        } else {
            userMap.put("namaLengkap", pengaduan.getNamaPelapor());
            userMap.put("email", pengaduan.getKontakPelapor());
        }
        map.put("user", userMap);
        map.put("namaPelapor", pengaduan.getNamaPelapor());
        map.put("kontakPelapor", pengaduan.getKontakPelapor());
        
        return map;
    }

    private Map<String, Object> convertLayananToMap(LayananAdministrasi layanan) {
        Map<String, Object> map = new java.util.HashMap<>();
        map.put("id", layanan.getId());
        map.put("nomorPermohonan", layanan.getNomorPermohonan());
        map.put("keperluan", layanan.getKeperluan());
        map.put("status", layanan.getStatus() != null ? layanan.getStatus().name() : null);
        map.put("tglDiajukan", layanan.getTglDiajukan());
        
        Map<String, Object> kategoriMap = new java.util.HashMap<>();
        kategoriMap.put("id", layanan.getKategori().getId());
        kategoriMap.put("namaKategori", layanan.getKategori().getNamaKategori());
        map.put("kategori", kategoriMap);
        
        Map<String, Object> userMap = new java.util.HashMap<>();
        userMap.put("id", layanan.getUser().getId());
        userMap.put("namaLengkap", layanan.getUser().getNamaLengkap());
        userMap.put("email", layanan.getUser().getEmail());
        map.put("user", userMap);
        
        return map;
    }
}
