# Fitur Lanjutan: Upload File, Pengaduan Publik, Role Superadmin, Notifikasi

## TL;DR

> **Quick Summary**: Menambahkan 4 fitur besar ke aplikasi SmartPelayanan yang sudah ada: upload file lampiran, pengaduan publik tanpa login, role SUPERADMIN, dan sistem notifikasi in-app.
>
> **Deliverables**:
> - Upload file (foto/video/PDF) untuk pengaduan dan layanan â€” disimpan di `uploads/` server
> - Halaman pengaduan publik di login page â€” tanpa perlu login, cukup nama + kontak
> - Role SUPERADMIN dengan dashboard khusus (kelola semua user, lihat semua data)
> - Sistem notifikasi in-app (bell icon) dengan 4 trigger event
>
> **Estimated Effort**: Large
> **Parallel Execution**: YES â€” 3 waves
> **Critical Path**: Wave 1 (entity/enum) â†’ Wave 2 (backend API) â†’ Wave 3 (frontend HTML)

---

## Context

### Original Request
- Upload file (foto, video, PDF, dll) untuk pengaduan dan layanan
- Tombol pengaduan di login page â€” tidak perlu login, cukup nama + kontak
- Role SUPERADMIN (full access) + ADMIN (karyawan) + WARGA (sudah ada)
- Notifikasi in-app (bell icon di dashboard)

### Research Findings
- `Pengaduan.java` sudah punya field `fotoBukti` (String) â€” perlu diubah jadi support multiple files
- `LayananAdministrasi.java` punya `dokumenPendukung` (String) â€” sama, perlu multiple files
- `RoleEnum.java` hanya punya `WARGA` dan `ADMIN` â€” perlu tambah `SUPERADMIN`
- `Pengaduan.java` punya `@ManyToOne user` yang `nullable = false` â€” perlu diubah agar nullable untuk pengaduan publik
- `CustomUserDetails.java` sudah pakai `"ROLE_" + user.getRole().name()` â€” SUPERADMIN otomatis jadi `ROLE_SUPERADMIN`
- `BaseEntity.java` pakai UUID sebagai ID â€” konsisten untuk entity Notifikasi baru
- H2 in-memory database â€” `ddl-auto=create-drop`, semua tabel dibuat ulang saat restart

### Key Decisions
- File upload: simpan di `uploads/` di root project, serve via `/uploads/**` endpoint
- Pengaduan publik: field `user` di `Pengaduan` dibuat nullable, tambah field `namaPelapor` + `kontakPelapor`
- SUPERADMIN: dashboard terpisah `/superadmin/dashboard`, bisa kelola user admin, lihat semua data
- Notifikasi: entity `Notifikasi` baru, polling setiap 30 detik dari frontend via fetch()

---

## Work Objectives

### Core Objective
Menambahkan 4 fitur ke aplikasi yang sudah berjalan tanpa merusak fitur yang ada.

### Concrete Deliverables
- `src/main/java/com/smartpelayanan/entity/LampiranFile.java` â€” entity untuk file lampiran
- `src/main/java/com/smartpelayanan/entity/Notifikasi.java` â€” entity notifikasi
- `src/main/java/com/smartpelayanan/enums/RoleEnum.java` â€” tambah SUPERADMIN
- `src/main/java/com/smartpelayanan/entity/Pengaduan.java` â€” user nullable, tambah namaPelapor/kontakPelapor
- `src/main/java/com/smartpelayanan/controller/FileUploadController.java` â€” upload & serve file
- `src/main/java/com/smartpelayanan/controller/PublicPengaduanController.java` â€” pengaduan tanpa login
- `src/main/java/com/smartpelayanan/controller/NotifikasiController.java` â€” CRUD notifikasi
- `src/main/java/com/smartpelayanan/controller/SuperAdminController.java` â€” kelola user & data
- `src/main/java/com/smartpelayanan/controller/PageController.java` â€” tambah route superadmin
- `src/main/java/com/smartpelayanan/config/SecurityConfig.java` â€” update permissions
- `src/main/resources/templates/public-pengaduan.html` â€” form pengaduan publik
- `src/main/resources/templates/superadmin-dashboard.html` â€” dashboard superadmin
- `src/main/resources/templates/login.html` â€” tambah tombol "Laporkan Masalah"
- `src/main/resources/templates/admin-dashboard.html` â€” tambah bell notifikasi + upload file
- `src/main/resources/templates/warga-dashboard.html` â€” tambah bell notifikasi + upload file

### Definition of Done
- [ ] Upload file berfungsi di form pengaduan dan layanan warga
- [ ] File tersimpan di folder `uploads/` dan bisa diakses via URL
- [ ] Tombol "Laporkan Masalah" di login page membuka form pengaduan publik
- [ ] Pengaduan publik tersimpan di database dengan status BARU
- [ ] Login dengan akun SUPERADMIN berhasil dan redirect ke `/superadmin/dashboard`
- [ ] SUPERADMIN bisa lihat semua user, tambah/nonaktifkan akun admin
- [ ] Bell icon muncul di semua dashboard dengan badge jumlah notif belum dibaca
- [ ] Notifikasi muncul saat: pengaduan baru, status pengaduan berubah, layanan baru, status layanan berubah

### Must Have
- Upload multiple files per pengaduan/layanan (max 5 file, max 10MB per file)
- Tipe file yang diizinkan: jpg, jpeg, png, gif, mp4, avi, pdf, doc, docx
- Pengaduan publik: nama pelapor + kontak (HP/email) wajib diisi
- SUPERADMIN bisa tambah akun ADMIN baru langsung dari dashboard
- SUPERADMIN bisa nonaktifkan/aktifkan akun user manapun
- Notifikasi ditandai "sudah dibaca" saat diklik
- Badge notifikasi update otomatis setiap 30 detik (polling)

### Must NOT Have (Guardrails)
- JANGAN ubah H2 database config di `application.properties`
- JANGAN hapus atau ubah fitur yang sudah ada (login, register, dashboard admin/warga)
- JANGAN pakai library frontend baru (React, Vue, dll) â€” tetap vanilla HTML/CSS/JS
- JANGAN pakai cloud storage (S3, GCS) â€” simpan lokal saja
- JANGAN kirim email/SMS untuk notifikasi â€” in-app saja
- JANGAN ubah struktur JWT atau auth flow yang sudah ada
- JANGAN pakai WebSocket untuk notifikasi â€” cukup polling setiap 30 detik

---

## Verification Strategy

### Test Decision
- **Infrastructure exists**: NO
- **Automated tests**: None
- **Agent-Executed QA**: YES (curl untuk API, manual check untuk HTML)

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Foundation â€” jalankan paralel):
â”œâ”€â”€ Task 1: Update RoleEnum.java â€” tambah SUPERADMIN
â”œâ”€â”€ Task 2: Update Pengaduan.java â€” user nullable, tambah namaPelapor/kontakPelapor
â”œâ”€â”€ Task 3: Buat LampiranFile.java â€” entity file lampiran
â””â”€â”€ Task 4: Buat Notifikasi.java â€” entity notifikasi

Wave 2 (Backend API â€” setelah Wave 1, jalankan paralel):
â”œâ”€â”€ Task 5: FileUploadController.java â€” upload & serve file
â”œâ”€â”€ Task 6: PublicPengaduanController.java â€” pengaduan tanpa login
â”œâ”€â”€ Task 7: NotifikasiController.java â€” get/mark-read notifikasi
â”œâ”€â”€ Task 8: SuperAdminController.java â€” kelola user & data
â”œâ”€â”€ Task 9: Update WebController.java â€” integrasi upload file ke pengaduan & layanan
â””â”€â”€ Task 10: Update SecurityConfig.java + PageController.java â€” permissions & routes baru

Wave 3 (Frontend HTML â€” setelah Wave 2, jalankan paralel):
â”œâ”€â”€ Task 11: public-pengaduan.html â€” form pengaduan publik
â”œâ”€â”€ Task 12: superadmin-dashboard.html â€” dashboard superadmin lengkap
â”œâ”€â”€ Task 13: Update login.html â€” tombol "Laporkan Masalah"
â”œâ”€â”€ Task 14: Update admin-dashboard.html â€” bell notifikasi + upload file di form
â””â”€â”€ Task 15: Update warga-dashboard.html â€” bell notifikasi + upload file di form

Wave FINAL (Verifikasi):
â””â”€â”€ Task F1: Compile + smoke test semua endpoint
```

---

## TODOs

- [x] 1. Update `RoleEnum.java` -- tambah SUPERADMIN

  **What to do**: Tambahkan `SUPERADMIN` ke enum. Update DataInitializer dengan user superadmin@smartpelayanan.com / super123.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 1 paralel. **Blocks**: Task 8,10,12. **Blocked By**: None.
  **References**: `src/main/java/com/smartpelayanan/enums/RoleEnum.java`, `src/main/java/com/smartpelayanan/config/DataInitializer.java`  **Acceptance Criteria**: [ ] RoleEnum berisi SUPERADMIN [ ] DataInitializer buat user superadmin  **QA Scenarios**: curl login superadmin -> dapat token dengan role SUPERADMIN  **Commit**: YES Wave 1

- [x] 2. Update `Pengaduan.java` -- user nullable, tambah namaPelapor/kontakPelapor

  **What to do**: Ubah user_id nullable=true. Tambah field namaPelapor (String) dan kontakPelapor (String) beserta getter/setter.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 1 paralel. **Blocks**: Task 6,9. **Blocked By**: None.
  **References**: `src/main/java/com/smartpelayanan/entity/Pengaduan.java`  **Acceptance Criteria**: [ ] user nullable [ ] field namaPelapor dan kontakPelapor ada  **Commit**: YES Wave 1

- [x] 3. Buat `LampiranFile.java` -- entity file lampiran

  **What to do**: Buat entity baru extends BaseEntity dengan field: namaFile, namaTersimpan, tipeFile, ukuranFile, urlFile, referensiId, tipeReferensi. Buat juga LampiranFileRepository dengan method findByReferensiId.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 1 paralel. **Blocks**: Task 5,9. **Blocked By**: None.
  **References**: `src/main/java/com/smartpelayanan/entity/BaseEntity.java`, `src/main/java/com/smartpelayanan/repository/PengaduanRepository.java`  **Acceptance Criteria**: [ ] LampiranFile.java ada [ ] LampiranFileRepository.java ada  **Commit**: YES Wave 1

- [x] 4. Buat `Notifikasi.java` dan `NotifikasiRepository.java`

  **What to do**: Buat entity Notifikasi extends BaseEntity dengan field: judul, pesan, tipe, referensiId, sudahDibaca (default false), user (ManyToOne). Buat NotifikasiRepository dengan method findByUserIdOrderByCreatedAtDesc dan countByUserIdAndSudahDibacaFalse.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 1 paralel. **Blocks**: Task 6,7,9. **Blocked By**: None.
  **References**: `src/main/java/com/smartpelayanan/entity/BaseEntity.java`, `src/main/java/com/smartpelayanan/repository/PengaduanRepository.java`  **Acceptance Criteria**: [ ] Notifikasi.java ada [ ] NotifikasiRepository.java ada  **Commit**: YES Wave 1

- [x] 5. Buat `FileUploadController.java` -- upload dan serve file

  **What to do**: POST /api/v1/upload menerima multipart file + referensiId + tipeReferensi. Validasi tipe (jpg/png/gif/mp4/avi/pdf/doc/docx) dan ukuran (max 10MB). Simpan ke folder uploads/ di root project dengan nama UUID+ekstensi. Simpan metadata ke LampiranFile. GET /uploads/{filename} serve file. GET /api/v1/lampiran/{referensiId} list file. DELETE /api/v1/lampiran/{id} hapus file.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 2 paralel. **Blocks**: Task 9,14,15. **Blocked By**: Task 3.
  **References**: `src/main/java/com/smartpelayanan/entity/LampiranFile.java`, `src/main/java/com/smartpelayanan/controller/WebController.java`  **Acceptance Criteria**: [ ] POST /api/v1/upload berfungsi [ ] file tersimpan di uploads/ [ ] GET /uploads/{filename} serve file  **Commit**: YES Wave 2

- [x] 6. Buat `PublicPengaduanController.java` -- pengaduan tanpa login

  **What to do**: POST /api/v1/public/pengaduan tanpa auth. Body: namaPelapor (wajib), kontakPelapor (wajib), judul, deskripsi, lokasi, prioritas. Set user=null, isi namaPelapor/kontakPelapor. Setelah simpan, buat notifikasi untuk semua ADMIN dan SUPERADMIN.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 2 paralel. **Blocks**: Task 11. **Blocked By**: Task 2,4.
  **References**: `src/main/java/com/smartpelayanan/entity/Pengaduan.java`, `src/main/java/com/smartpelayanan/entity/Notifikasi.java`, `src/main/java/com/smartpelayanan/repository/UserRepository.java`  **Acceptance Criteria**: [ ] POST /api/v1/public/pengaduan bisa diakses tanpa token [ ] pengaduan tersimpan dengan user=null  **Commit**: YES Wave 2

- [x] 7. Buat `NotifikasiController.java` -- get dan mark-read

  **What to do**: GET /api/v1/notifikasi (list notif user, max 50, urut terbaru). GET /api/v1/notifikasi/unread-count (hitung belum dibaca). PATCH /api/v1/notifikasi/{id}/read (tandai satu). PATCH /api/v1/notifikasi/read-all (tandai semua). Semua butuh JWT. Ekstrak email dari token pakai JwtUtils.getEmailFromToken().
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 2 paralel. **Blocks**: Task 14,15. **Blocked By**: Task 4.
  **References**: `src/main/java/com/smartpelayanan/entity/Notifikasi.java`, `src/main/java/com/smartpelayanan/repository/NotifikasiRepository.java`, `src/main/java/com/smartpelayanan/utils/JwtUtils.java`  **Acceptance Criteria**: [ ] GET /api/v1/notifikasi mengembalikan list [ ] PATCH mark-read berfungsi  **Commit**: YES Wave 2

- [x] 8. Buat `SuperAdminController.java` -- kelola semua user dan data

  **What to do**: GET /api/v1/superadmin/users (list semua user). POST /api/v1/superadmin/users (buat ADMIN baru, body: namaLengkap/email/password/noTelp). PATCH /api/v1/superadmin/users/{id}/toggle-status (aktif/nonaktif). GET /api/v1/superadmin/stats (statistik lengkap). GET /api/v1/superadmin/pengaduan. GET /api/v1/superadmin/layanan. Validasi role SUPERADMIN, return 403 jika bukan.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 2 paralel. **Blocks**: Task 12. **Blocked By**: Task 1.
  **References**: `src/main/java/com/smartpelayanan/repository/UserRepository.java`, `src/main/java/com/smartpelayanan/controller/WebController.java`, `src/main/java/com/smartpelayanan/utils/JwtUtils.java`  **Acceptance Criteria**: [ ] GET /api/v1/superadmin/users berfungsi [ ] POST buat admin berfungsi [ ] 403 untuk non-superadmin  **Commit**: YES Wave 2

- [x] 9. Update `WebController.java` -- tambah trigger notifikasi

  **What to do**: Di updatePengaduanStatus: setelah update, buat notifikasi untuk user pemilik pengaduan (jika user != null) dengan tipe STATUS_PENGADUAN. Di updateLayananStatus: buat notifikasi untuk user pemilik layanan dengan tipe STATUS_LAYANAN. Di createPengaduan: buat notifikasi untuk semua ADMIN+SUPERADMIN dengan tipe PENGADUAN_BARU. Di createLayanan: buat notifikasi untuk semua ADMIN+SUPERADMIN dengan tipe LAYANAN_BARU. Inject NotifikasiRepository ke constructor.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 2 paralel. **Blocks**: Task 14,15. **Blocked By**: Task 4.
  **References**: `src/main/java/com/smartpelayanan/controller/WebController.java`, `src/main/java/com/smartpelayanan/entity/Notifikasi.java`, `src/main/java/com/smartpelayanan/repository/NotifikasiRepository.java`  **Acceptance Criteria**: [ ] Notifikasi dibuat saat status berubah [ ] Notifikasi dibuat untuk admin saat data baru masuk  **Commit**: YES Wave 2

- [x] 10. Update `SecurityConfig.java` dan `PageController.java` -- routes dan permissions baru

  **What to do**: SecurityConfig: tambah /public-pengaduan, /api/v1/public/**, /uploads/** ke permitAll(). Tambah /api/v1/notifikasi/**, /api/v1/upload, /api/v1/lampiran/**, /api/v1/superadmin/**, /superadmin/dashboard ke authenticated(). PageController: tambah GET /public-pengaduan -> 'public-pengaduan' dan GET /superadmin/dashboard -> 'superadmin-dashboard'. Login.html: update JS redirect agar role SUPERADMIN diarahkan ke /superadmin/dashboard.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 2 paralel. **Blocks**: Task 11,12. **Blocked By**: Task 1.
  **References**: `src/main/java/com/smartpelayanan/config/SecurityConfig.java`, `src/main/java/com/smartpelayanan/controller/PageController.java`, `src/main/resources/templates/login.html`  **Acceptance Criteria**: [ ] /public-pengaduan bisa diakses tanpa login [ ] /superadmin/dashboard route ada  **Commit**: YES Wave 2

- [x] 11. Buat `public-pengaduan.html` -- form pengaduan publik

  **What to do**: Buat src/main/resources/templates/public-pengaduan.html. Style konsisten dengan login.html (gradient biru-ungu, card putih). Form fields: Nama Pelapor (required), Kontak/HP (required), Judul Pengaduan (required), Deskripsi (required, textarea), Lokasi (optional), Prioritas (select: RENDAH/SEDANG/TINGGI, default SEDANG). Tombol 'Kirim Laporan' yang POST ke /api/v1/public/pengaduan. Setelah berhasil: tampilkan pesan sukses dengan nomor ID pengaduan. Link 'Kembali ke Login' di bawah form.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 3 paralel. **Blocks**: None. **Blocked By**: Task 6,10.
  **References**: `src/main/resources/templates/login.html` (ikuti style), `src/main/resources/templates/register.html` (pola form)  **Acceptance Criteria**: [ ] Form ada dengan semua field [ ] POST ke /api/v1/public/pengaduan [ ] Pesan sukses muncul setelah berhasil  **Commit**: YES Wave 3

- [x] 12. Buat `superadmin-dashboard.html` -- dashboard superadmin lengkap

  **What to do**: Buat src/main/resources/templates/superadmin-dashboard.html. Style: sidebar merah-gelap (gradient #8B0000 ke #c0392b) untuk membedakan dari admin biasa. Sidebar menu: Dashboard, Kelola User, Semua Pengaduan, Semua Layanan, Logout. Section Dashboard: stats card (total user per role, total pengaduan per status, total layanan). Section Kelola User: tabel semua user dengan kolom nama/email/role/status, tombol Tambah Admin, tombol Toggle Status. Modal Tambah Admin: form nama/email/password/noTelp. Section Semua Pengaduan: tabel lengkap termasuk pengaduan publik (tampilkan namaPelapor jika user null). Section Semua Layanan: tabel lengkap. Bell notifikasi di header (sama seperti admin dashboard). Cek token dan role SUPERADMIN di awal JS, redirect ke /login jika bukan.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 3 paralel. **Blocks**: None. **Blocked By**: Task 8,10.
  **References**: `src/main/resources/templates/admin-dashboard.html` (ikuti struktur), `src/main/java/com/smartpelayanan/controller/SuperAdminController.java`  **Acceptance Criteria**: [ ] Dashboard tampil untuk SUPERADMIN [ ] Kelola user berfungsi [ ] Bell notifikasi ada  **Commit**: YES Wave 3

- [x] 13. Update `login.html` -- tambah tombol Laporkan Masalah

  **What to do**: Tambahkan tombol 'Laporkan Masalah' yang mencolok di halaman login, di bawah form login dan di atas test accounts. Style: tombol outline dengan border merah/oranye atau warna berbeda dari tombol login. Klik tombol -> window.location.href = '/public-pengaduan'. Update JS redirect: tambahkan kondisi jika role === 'SUPERADMIN' maka redirect ke '/superadmin/dashboard'.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 3 paralel. **Blocks**: None. **Blocked By**: Task 10.
  **References**: `src/main/resources/templates/login.html` -- file yang diubah  **Acceptance Criteria**: [ ] Tombol 'Laporkan Masalah' ada dan mengarah ke /public-pengaduan [ ] Login SUPERADMIN redirect ke /superadmin/dashboard  **Commit**: YES Wave 3

- [x] 14. Update `admin-dashboard.html` -- bell notifikasi + upload file di form

  **What to do**: Tambahkan bell icon di header (kanan atas, sebelum user info): <i class='fas fa-bell'></i> dengan badge merah untuk unread count. Polling GET /api/v1/notifikasi/unread-count setiap 30 detik. Klik bell -> tampilkan dropdown list notifikasi (GET /api/v1/notifikasi), klik notif -> PATCH mark-read. Di form modal pengaduan (jika ada) dan form update status: tidak perlu upload (admin hanya update status). Tambahkan section/tab 'Lampiran' di detail pengaduan: tampilkan file yang diupload warga (GET /api/v1/lampiran/{pengaduanId}), tampilkan sebagai link/thumbnail.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 3 paralel. **Blocks**: None. **Blocked By**: Task 7,9.
  **References**: `src/main/resources/templates/admin-dashboard.html`, `src/main/java/com/smartpelayanan/controller/NotifikasiController.java`  **Acceptance Criteria**: [ ] Bell icon ada di header [ ] Badge unread count muncul [ ] Dropdown notifikasi berfungsi [ ] Mark-read berfungsi  **Commit**: YES Wave 3

- [x] 15. Update `warga-dashboard.html` -- bell notifikasi + upload file di form

  **What to do**: Tambahkan bell icon di header sama seperti admin. Polling unread count setiap 30 detik. Di modal 'Ajukan Pengaduan': tambahkan field upload file (input type=file, multiple, accept=image/*,video/*,.pdf,.doc,.docx). Setelah pengaduan berhasil dibuat, upload file satu per satu ke POST /api/v1/upload dengan referensiId=pengaduanId. Di modal 'Ajukan Layanan': tambahkan field upload dokumen (input type=file, multiple). Setelah layanan berhasil dibuat, upload file ke /api/v1/upload. Tampilkan progress upload jika ada file.
  **Recommended Agent Profile**: `quick`  **Parallelization**: Wave 3 paralel. **Blocks**: None. **Blocked By**: Task 5,7,9.
  **References**: `src/main/resources/templates/warga-dashboard.html`, `src/main/java/com/smartpelayanan/controller/FileUploadController.java`, `src/main/java/com/smartpelayanan/controller/NotifikasiController.java`  **Acceptance Criteria**: [ ] Bell icon ada [ ] Upload file berfungsi di form pengaduan [ ] Upload file berfungsi di form layanan  **Commit**: YES Wave 3


---

## Final Verification Wave

- [x] F1. **Compile & Smoke Test** â€” `quick`
  Jalankan `C:\tools\apache-maven-3.9.15\bin\mvn.cmd compile -f pom.xml`. Harus BUILD SUCCESS.
  Lalu test endpoint kunci dengan curl:
  - `POST /api/v1/public/pengaduan` dengan nama + kontak + judul + deskripsi
  - `GET /api/v1/notifikasi` dengan token admin
  - `GET /api/v1/superadmin/users` dengan token superadmin
  Output: `Build [PASS/FAIL] | PublicPengaduan [PASS/FAIL] | Notifikasi [PASS/FAIL] | SuperAdmin [PASS/FAIL]`

---

## Commit Strategy

- **Wave 1**: `feat(entity): add SUPERADMIN role, public pengaduan fields, LampiranFile, Notifikasi entities`
- **Wave 2**: `feat(api): add file upload, public pengaduan, notification, superadmin endpoints`
- **Wave 3**: `feat(frontend): add public pengaduan form, superadmin dashboard, notifications UI`

---

## Success Criteria

```bash
# Build harus sukses
C:\tools\apache-maven-3.9.15\bin\mvn.cmd compile
# Expected: BUILD SUCCESS

# Pengaduan publik
curl -X POST http://localhost:8082/api/v1/public/pengaduan \
  -H "Content-Type: application/json" \
  -d '{"namaPelapor":"Budi","kontakPelapor":"081234","judul":"Jalan rusak","deskripsi":"Jalan di RT 5 berlubang"}'
# Expected: {"status":200,"data":{...}}

# Notifikasi (butuh token)
curl http://localhost:8082/api/v1/notifikasi \
  -H "Authorization: Bearer {token}"
# Expected: {"status":200,"data":[...]}
```




