# Learnings - fitur-form-dinamis

## 2026-05-14 Initial Codebase Analysis

### Project Structure
- Spring Boot 3.2.5, Java 17, Maven
- Package: `com.smartpelayanan`
- Maven path: `C:\tools\apache-maven-3.9.15\bin\mvn.cmd`
- Build command: `C:\tools\apache-maven-3.9.15\bin\mvn.cmd compile`

### Existing Entities (already done)
- `FormField.java` ✅ - entity with label, tipe, required, urutan, opsi, placeholder, kategori
- `JawabanForm.java` ✅ - entity with layanan, field, nilai
- `RiwayatStatusLayanan.java` ✅ - entity with layanan, statusLama, statusBaru, catatan, diubahOleh
- `FormFieldRepository.java` ✅ - findByKategoriIdOrderByUrutanAsc, deleteByKategoriId
- `JawabanFormRepository.java` ✅ - findByLayananId, deleteByLayananId
- `RiwayatStatusLayananRepository.java` ✅ - findByLayananIdOrderByCreatedAtDesc
- `pom.xml` ✅ - iText 7 kernel + layout 7.2.5 already added

### DataInitializer Status
- Does NOT inject FormFieldRepository yet
- Does NOT seed form fields for 6 categories
- Uses `userRepository.existsByEmail("admin@smartpelayanan.com")` as guard
- Has helper methods: createKategori(), createWarga(), createLayanan()

### WebController.java
- POST /api/v1/warga/layanan - does NOT save JawabanForm yet
- PATCH /api/v1/admin/layanan/{id}/status - does NOT save RiwayatStatusLayanan yet
- convertPengaduanToMap() in PengaduanServiceImpl.java has null user bug at line 162-164

### SecurityConfig.java
- `/api/v1/kategori/**` is already in authenticated() list
- `/api/v1/layanan/**`, `/api/v1/pengaduan/**/detail`, `/api/v1/laporan/**` NOT yet added
- `/api/v1/admin/fields/**`, `/api/v1/admin/kategori/**/fields` NOT yet added

### Patterns
- Controllers use `@RestController @RequestMapping("/api/v1")`
- Auth via `Authorization: Bearer {token}` header, extracted with `jwtUtils.getEmailFromToken()`
- Response format: `ResponseEntity.ok(Map.of("data", result))`
- Error format: `ResponseEntity.badRequest().body(Map.of("message", e.getMessage()))`
- Entities extend BaseEntity (has UUID id, createdAt, updatedAt)
- KategoriLayanan uses Integer ID (not UUID)
- LayananAdministrasi uses UUID ID

### Missing Controllers (need to create)
- `FormFieldController.java` - CRUD field per kategori
- `LayananDetailController.java` - detail layanan + pengaduan
- `LaporanController.java` - PDF export

### HTML Templates
- `admin-dashboard.html` - needs modal detail + konfigurasi field form
- `warga-dashboard.html` - needs dynamic form per category
