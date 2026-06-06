package com.smartpelayanan.repository;

import com.smartpelayanan.entity.LampiranFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LampiranFileRepository extends JpaRepository<LampiranFile, UUID> {
    List<LampiranFile> findByReferensiId(String referensiId);
    List<LampiranFile> findByNamaTersimpan(String namaTersimpan);
}
