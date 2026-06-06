package com.smartpelayanan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/public-pengaduan")
    public String publicPengaduanPage() {
        return "public-pengaduan";
    }

    @GetMapping("/superadmin/dashboard")
    public String superadminDashboard() {
        return "superadmin-dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin-dashboard";
    }

    @GetMapping("/warga/dashboard")
    public String wargaDashboard() {
        return "warga-dashboard";
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }
}