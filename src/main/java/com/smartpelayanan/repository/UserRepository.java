package com.smartpelayanan.repository;

import com.smartpelayanan.entity.User;
import com.smartpelayanan.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, java.util.UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(RoleEnum role);
    List<User> findByRoleIn(List<RoleEnum> roles);
}
