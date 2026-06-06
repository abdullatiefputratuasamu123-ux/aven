package com.smartpelayanan.repository;

import com.smartpelayanan.entity.FormField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FormFieldRepository extends JpaRepository<FormField, UUID> {
    List<FormField> findByKategoriIdOrderByUrutanAsc(Integer kategoriId);
    void deleteByKategoriId(Integer kategoriId);
}
