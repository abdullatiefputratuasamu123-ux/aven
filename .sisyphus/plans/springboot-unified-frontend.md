# Unifikasi Frontend ke Spring Boot (HTML/CSS/JS)

## TL;DR

> **Quick Summary**: Menyatukan frontend React yang terpisah ke dalam Spring Boot sebagai Thymeleaf templates HTML/CSS/JS murni. Tidak ada lagi Node.js/Vite/React — semua di-serve langsung oleh Spring Boot.
>
> **Deliverables**:
> - `register.html` — halaman registrasi warga baru
> - `PageController.java` — tambah route `/register`
> - `SecurityConfig.java` — izinkan akses `/register` tanpa auth
> - `login.html` — tambah link ke halaman register
> - Folder `frontend/` bisa diabaikan/dihapus (opsional)
>
> **Estimated Effort**: Quick
> **Parallel Execution**: NO - sequential (sedikit task, saling bergantung)
> **Critical Path**: SecurityConfig → PageController → register.html → login.html update

---

## Context

### Original Request
"buatkan agar frontend dan backend menyatu jadi menggunakan springboot saja untuk project ini dan untuk front end cukup html css js"

### Interview Summary
**Key Discussions**:
- Proyek sudah pakai Spring Boot 3.2.5 + Thymeleaf + H2 + JWT
- Folder `frontend/` berisi React/Vite app yang berjalan terpisah di port berbeda
- Templates Thymeleaf sudah ada dan JS-nya sudah memanggil REST API dengan fetch()
- Yang kurang: halaman register, route register, link register di login page

**Research Findings**:
- `SecurityConfig.java` sudah mengizinkan `/login`, `/api/v1/auth/**`, `/h2-console/**`
- `AuthController.java` sudah punya `POST /api/v1/auth/register` yang berfungsi
- `login.html` sudah punya JS fetch ke `/api/v1/auth/login` — berfungsi dengan baik
- `admin-dashboard.html` dan `warga-dashboard.html` sudah lengkap dengan semua fitur
- `DataInitializer.java` membuat data dummy: admin + 4 warga + kategori + pengaduan

### Metis Review
**Identified Gaps** (addressed):
- Tidak ada halaman register → dibuat `register.html`
- Route `/register` belum ada di `PageController` → ditambahkan
- `/register` belum diizinkan di `SecurityConfig` → ditambahkan ke permitAll
- Login page tidak ada link ke register → ditambahkan

---

## Work Objectives

### Core Objective
Membuat aplikasi Spring Boot yang self-contained: semua halaman (login, register, admin dashboard, warga dashboard) di-serve langsung oleh Spring Boot sebagai Thymeleaf templates HTML/CSS/JS murni. Tidak ada dependency ke React/Node.js/Vite.

### Concrete Deliverables
- `src/main/resources/templates/register.html` — halaman registrasi warga
- `src/main/java/com/smartpelayanan/controller/PageController.java` — tambah route `/register`
- `src/main/java/com/smartpelayanan/config/SecurityConfig.java` — izinkan `/register`
- `src/main/resources/templates/login.html` — tambah link "Belum punya akun? Daftar"

### Definition of Done
- [ ] Aplikasi bisa dijalankan dengan `mvn spring-boot:run` saja (tanpa `npm run dev`)
- [ ] Akses `http://localhost:8082/login` → tampil halaman login
- [ ] Akses `http://localhost:8082/register` → tampil halaman register
- [ ] Register warga baru berhasil → redirect ke login
- [ ] Login admin → redirect ke `/admin/dashboard`
- [ ] Login warga → redirect ke `/warga/dashboard`
- [ ] Semua fitur dashboard (CRUD pengaduan, layanan, kategori) berfungsi

### Must Have
- Halaman register dengan form: nama lengkap, email, password, no telp, alamat
- Validasi form di sisi client (field required, format email)
- Setelah register berhasil → redirect ke `/login` dengan pesan sukses
- Style konsisten dengan `login.html` (gradient biru-ungu, card putih)
- Link "Daftar" di login page, link "Sudah punya akun? Login" di register page

### Must NOT Have (Guardrails)
- JANGAN ubah logika backend (service, repository, entity) — hanya controller dan templates
- JANGAN hapus folder `frontend/` secara paksa — biarkan saja (tidak dijalankan)
- JANGAN tambah dependency baru ke `pom.xml`
- JANGAN ubah `admin-dashboard.html` dan `warga-dashboard.html` — sudah berfungsi
- JANGAN pakai framework JS (React, Vue, Angular) — murni vanilla JS
- JANGAN pakai Thymeleaf server-side rendering untuk data — tetap pakai fetch() ke REST API

---

## Verification Strategy

> **ZERO HUMAN INTERVENTION** - ALL verification is agent-executed.

### Test Decision
- **Infrastructure exists**: NO (tidak ada test framework)
- **Automated tests**: None
- **Framework**: none

### QA Policy
Setiap task diverifikasi dengan curl dan browser check.

---

## Execution Strategy

### Sequential Execution (task sedikit, saling bergantung)

```
Step 1: Update SecurityConfig.java — izinkan /register
Step 2: Update PageController.java — tambah route /register
Step 3: Buat register.html — halaman registrasi lengkap
Step 4: Update login.html — tambah link ke /register
Step 5: Verifikasi — jalankan aplikasi dan test semua flow
```

---

## TODOs

- [x] 1. Update `SecurityConfig.java` — izinkan `/register` tanpa autentikasi

  **What to do**:
  - Buka file `src/main/java/com/smartpelayanan/config/SecurityConfig.java`
  - Di dalam `authorizeHttpRequests`, tambahkan `/register` ke daftar `permitAll()` bersama `/login`
  - Baris yang perlu diubah: `.requestMatchers("/login", "/", "/admin/dashboard", "/warga/dashboard").permitAll()`
  - Ubah menjadi: `.requestMatchers("/login", "/register", "/", "/admin/dashboard", "/warga/dashboard").permitAll()`

  **Must NOT do**:
  - JANGAN ubah konfigurasi JWT atau session management
  - JANGAN ubah CORS configuration
  - JANGAN ubah permission untuk endpoint API lainnya

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Perubahan satu baris di file konfigurasi, tidak ada logika kompleks
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Step 1 (harus pertama)
  - **Blocks**: Task 2, 3, 4
  - **Blocked By**: None (dapat dimulai langsung)

  **References**:
  - `src/main/java/com/smartpelayanan/config/SecurityConfig.java:41-46` — baris authorizeHttpRequests yang perlu diubah

  **Acceptance Criteria**:
  - [ ] File `SecurityConfig.java` berhasil diubah
  - [ ] Baris `.requestMatchers` mengandung `/register`

  **QA Scenarios**:
  ```
  Scenario: Akses /register tanpa token harus berhasil (200)
    Tool: Bash (curl)
    Preconditions: Aplikasi berjalan di port 8082
    Steps:
      1. curl -I http://localhost:8082/register
      2. Periksa HTTP status code di response header
    Expected Result: HTTP 200 OK (bukan 302 redirect ke login atau 403 Forbidden)
    Evidence: .sisyphus/evidence/task-1-register-accessible.txt
  ```

  **Commit**: YES (group dengan task 2)
  - Message: `feat(security): allow /register page without authentication`
  - Files: `src/main/java/com/smartpelayanan/config/SecurityConfig.java`

- [x] 2. Update `PageController.java` — tambah route GET `/register`

  **What to do**:
  - Buka file `src/main/java/com/smartpelayanan/controller/PageController.java`
  - Tambahkan method baru setelah method `loginPage()`:
    ```java
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    ```
  - Method ini akan me-render template `src/main/resources/templates/register.html`

  **Must NOT do**:
  - JANGAN ubah method yang sudah ada
  - JANGAN tambah parameter atau model attributes — halaman register menggunakan fetch() ke API

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Tambah satu method sederhana di controller
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES (bisa bersamaan dengan task 1)
  - **Parallel Group**: Step 1-2
  - **Blocks**: Task 3
  - **Blocked By**: None

  **References**:
  - `src/main/java/com/smartpelayanan/controller/PageController.java` — file yang diubah
  - Pattern: ikuti pola method `loginPage()` yang sudah ada

  **Acceptance Criteria**:
  - [ ] Method `registerPage()` dengan `@GetMapping("/register")` ada di `PageController.java`
  - [ ] Method mengembalikan string `"register"`

  **QA Scenarios**:
  ```
  Scenario: Route /register terdaftar dan mengembalikan template register
    Tool: Bash (curl)
    Preconditions: Aplikasi berjalan
    Steps:
      1. curl -s http://localhost:8082/register | grep -i "register\|daftar"
    Expected Result: Response HTML mengandung kata "register" atau "daftar"
    Evidence: .sisyphus/evidence/task-2-register-route.txt
  ```

  **Commit**: YES (group dengan task 1)
  - Message: `feat(controller): add /register route`
  - Files: `src/main/java/com/smartpelayanan/controller/PageController.java`

- [x] 3. Buat `register.html` — halaman registrasi warga baru

  **What to do**:
  - Buat file baru: `src/main/resources/templates/register.html`
  - Style harus konsisten dengan `login.html`: background gradient biru-ungu (`#1a1a2e` → `#0f3460`), card putih dengan border-radius 25px, font Segoe UI
  - Form fields yang diperlukan:
    - Nama Lengkap (text, required)
    - Email (email, required)
    - Password (password, required, min 6 karakter)
    - Konfirmasi Password (password, required, harus sama dengan password)
    - No. Telepon (tel, optional)
    - Alamat (textarea, optional)
  - Tombol "Daftar" yang memanggil `POST /api/v1/auth/register`
  - Link "Sudah punya akun? Login" yang mengarah ke `/login`
  - Tampilkan pesan error jika registrasi gagal (email sudah terdaftar, dll)
  - Setelah registrasi berhasil: tampilkan pesan sukses, lalu redirect ke `/login` setelah 2 detik
  - Gunakan Font Awesome dari CDN: `https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css`
  - JS menggunakan vanilla fetch() — TIDAK pakai library apapun
  - Request body ke API: `{ namaLengkap, email, password, noTelp, alamat }`
  - Validasi client-side: semua required field terisi, email valid, password minimal 6 karakter, konfirmasi password cocok

  **Must NOT do**:
  - JANGAN pakai Thymeleaf server-side binding (`th:field`, `th:object`) — gunakan fetch() biasa
  - JANGAN pakai framework CSS (Bootstrap, Tailwind) — murni CSS inline di `<style>`
  - JANGAN redirect langsung tanpa pesan sukses

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Membuat satu file HTML dengan CSS dan JS vanilla, mengikuti pola yang sudah ada di login.html
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: NO
  - **Parallel Group**: Step 3 (setelah task 1 dan 2)
  - **Blocks**: Task 4 (verifikasi akhir)
  - **Blocked By**: Task 2 (route harus ada dulu)

  **References**:
  - `src/main/resources/templates/login.html` — ikuti PERSIS style CSS dan struktur HTML-nya
  - `src/main/java/com/smartpelayanan/controller/AuthController.java:22-30` — endpoint register yang dipanggil
  - `src/main/java/com/smartpelayanan/dto/RegisterRequest.java` — field yang diterima API
  - `src/main/java/com/smartpelayanan/dto/ApiResponse.java` — format response API

  **Acceptance Criteria**:
  - [ ] File `register.html` ada di `src/main/resources/templates/`
  - [ ] Form memiliki semua field yang diperlukan
  - [ ] JS memanggil `POST /api/v1/auth/register` dengan fetch()
  - [ ] Ada link ke `/login`
  - [ ] Validasi client-side berfungsi (password match, required fields)

  **QA Scenarios**:
  ```
  Scenario: Register warga baru berhasil
    Tool: Bash (curl)
    Preconditions: Aplikasi berjalan, email belum terdaftar
    Steps:
      1. curl -X POST http://localhost:8082/api/v1/auth/register \
           -H "Content-Type: application/json" \
           -d '{"namaLengkap":"Test Warga","email":"testwarga@test.com","password":"test123","noTelp":"081234567890","alamat":"Jl. Test No. 1"}'
      2. Periksa response JSON: field "status" harus 200, field "data.message" harus "User registered successfully"
    Expected Result: {"status":200,"data":{"message":"User registered successfully","user":{...}}}
    Evidence: .sisyphus/evidence/task-3-register-success.txt

  Scenario: Register dengan email yang sudah terdaftar harus gagal
    Tool: Bash (curl)
    Preconditions: Email "admin@smartpelayanan.com" sudah ada di database
    Steps:
      1. curl -X POST http://localhost:8082/api/v1/auth/register \
           -H "Content-Type: application/json" \
           -d '{"namaLengkap":"Duplikat","email":"admin@smartpelayanan.com","password":"test123"}'
      2. Periksa response: status harus 400, ada pesan error
    Expected Result: HTTP 400 dengan body {"status":400,"message":"Email already exists"}
    Evidence: .sisyphus/evidence/task-3-register-duplicate-error.txt
  ```

  **Commit**: YES
  - Message: `feat(frontend): add register page with HTML/CSS/JS`
  - Files: `src/main/resources/templates/register.html`

- [x] 4. Update `login.html` — tambah link ke halaman register

  **What to do**:
  - Buka file `src/main/resources/templates/login.html`
  - Tambahkan link "Belum punya akun? **Daftar di sini**" di bawah tombol login (sebelum div `.test-accounts`)
  - Style link: teks kecil, centered, warna `#00d4ff` untuk bagian "Daftar di sini"
  - Contoh HTML yang ditambahkan:
    ```html
    <div style="text-align: center; margin-top: 20px; font-size: 14px; color: #666;">
        Belum punya akun? <a href="/register" style="color: #00d4ff; font-weight: 600; text-decoration: none;">Daftar di sini</a>
    </div>
    ```
  - Letakkan tepat setelah `</form>` dan sebelum `<div class="loading"...>`

  **Must NOT do**:
  - JANGAN ubah logika JS login yang sudah ada
  - JANGAN ubah style yang sudah ada
  - JANGAN ubah form fields

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Tambah beberapa baris HTML saja
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES (bisa bersamaan dengan task 3)
  - **Parallel Group**: Step 3-4
  - **Blocks**: None
  - **Blocked By**: None (perubahan independen)

  **References**:
  - `src/main/resources/templates/login.html:252-255` — area setelah `</form>` untuk meletakkan link
  - `src/main/resources/templates/login.html:54` — warna `#00d4ff` yang dipakai di logo

  **Acceptance Criteria**:
  - [ ] Link "Daftar di sini" ada di `login.html`
  - [ ] Link mengarah ke `/register`
  - [ ] Style konsisten (warna `#00d4ff`)

  **QA Scenarios**:
  ```
  Scenario: Link register ada di halaman login
    Tool: Bash (curl)
    Preconditions: Aplikasi berjalan
    Steps:
      1. curl -s http://localhost:8082/login | grep -i "register\|daftar"
    Expected Result: Output mengandung href="/register" dan teks "Daftar"
    Evidence: .sisyphus/evidence/task-4-login-register-link.txt
  ```

  **Commit**: YES (group dengan task 3)
  - Message: `feat(frontend): add register link to login page`
  - Files: `src/main/resources/templates/login.html`

---

## Final Verification Wave

- [x] F1. **Verifikasi Aplikasi Berjalan** — `unspecified-high`
  Jalankan `mvn spring-boot:run` dari direktori project. Pastikan tidak ada error compile. Akses `http://localhost:8082/` → harus redirect ke `/login`. Akses `/register` → harus tampil halaman register. Test login dengan `admin@smartpelayanan.com` / `admin123` → harus redirect ke `/admin/dashboard`.
  Output: `Build [PASS/FAIL] | Login [PASS/FAIL] | Register [PASS/FAIL] | Dashboard [PASS/FAIL] | VERDICT`

---

## Commit Strategy

- **1**: `feat(frontend): unify frontend into Spring Boot as HTML/CSS/JS templates`
  - Files: `SecurityConfig.java`, `PageController.java`, `register.html`, `login.html`

---

## Success Criteria

### Verification Commands
```bash
# Build
mvn compile  # Expected: BUILD SUCCESS

# Test login API
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@smartpelayanan.com","password":"admin123"}'
# Expected: {"status":200,"data":{"token":"...","role":"ADMIN",...}}

# Test register API
curl -X POST http://localhost:8082/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"namaLengkap":"Test User","email":"test@test.com","password":"test123","noTelp":"081234567890","alamat":"Jl. Test"}'
# Expected: {"status":200,"data":{"message":"User registered successfully",...}}

# Test pages accessible
curl -I http://localhost:8082/login    # Expected: 200 OK
curl -I http://localhost:8082/register # Expected: 200 OK
```

### Final Checklist
- [ ] `mvn spring-boot:run` berhasil tanpa error
- [ ] Halaman `/login` tampil dengan benar
- [ ] Halaman `/register` tampil dengan benar
- [ ] Link "Daftar" di login page mengarah ke `/register`
- [ ] Link "Login" di register page mengarah ke `/login`
- [ ] Register warga baru berhasil via form
- [ ] Login admin berhasil → redirect ke admin dashboard
- [ ] Login warga berhasil → redirect ke warga dashboard
- [ ] Semua fitur dashboard berfungsi (tidak ada yang rusak)
