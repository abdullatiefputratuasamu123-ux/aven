package com.smartpelayanan.repository;

import com.smartpelayanan.entity.JawabanForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JawabanFormRepository extends JpaRepository<JawabanForm, UUID> {
    List<JawabanForm> findByLayananId(UUID layananId);
    void deleteByLayananId(UUID layananId);
}
