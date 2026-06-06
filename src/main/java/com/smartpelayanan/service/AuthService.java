package com.smartpelayanan.service;

import com.smartpelayanan.config.CustomUserDetails;
import com.smartpelayanan.dto.AuthResponse;
import com.smartpelayanan.dto.LoginRequest;
import com.smartpelayanan.dto.RegisterRequest;
import com.smartpelayanan.entity.User;
import com.smartpelayanan.enums.RoleEnum;
import com.smartpelayanan.repository.UserRepository;
import com.smartpelayanan.utils.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    public Map<String, Object> register(RegisterRequest request) {
        // Validasi field wajib
        if (request.getNamaLengkap() == null || request.getNamaLengkap().trim().isEmpty()) {
            throw new RuntimeException("Nama lengkap harus diisi");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email harus diisi");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password harus diisi");
        }
        if (request.getPassword().length() < 6) {
            throw new RuntimeException("Password minimal 6 karakter");
        }

        // Cek duplikasi email
        if (userRepository.existsByEmail(request.getEmail().trim())) {
            throw new RuntimeException("Email sudah terdaftar");
        }

        // Buat user baru
        User user = new User();
        user.setNamaLengkap(request.getNamaLengkap().trim());
        user.setEmail(request.getEmail().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNoTelp(request.getNoTelp() != null ? request.getNoTelp().trim() : null);
        user.setAlamat(request.getAlamat() != null ? request.getAlamat().trim() : null);
        user.setRole(RoleEnum.WARGA);
        user.setStatusAktif(true);

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("user", Map.of(
                "id", user.getId().toString(),
                "email", user.getEmail(),
                "namaLengkap", user.getNamaLengkap(),
                "role", user.getRole().name()
        ));
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        String token = jwtUtils.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getEmail(), user.getNamaLengkap(), user.getRole().name());
    }
}
