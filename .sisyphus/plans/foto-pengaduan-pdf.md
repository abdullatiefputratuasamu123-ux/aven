# Upload Foto Pengaduan Publik + Tampil di Admin + PDF

## TL;DR

> **Quick Summary**: Menambahkan fitur upload foto pada form laporan masalah publik (tanpa login), menampilkan foto di dashboard admin, dan meng-embed foto ke dalam PDF laporan pengaduan maupun layanan administrasi.
>
> **Deliverables**:
> - Form publik bisa upload foto (jpg/png/gif, max 5MB)
> - Admin melihat foto sebagai gambar di modal detail pengaduan
> - PDF pengaduan menyertakan foto yang di-embed
> - PDF layanan administrasi menyertakan foto lampiran yang di-embed
>
> **Estimated Effort**: Short
> **Parallel Execution**: YES - 2 waves
> **Critical Path**: Task 1 → Task 2 → Task 3 → Task 4 → Task 5

---

## Context

### Original Request
"buatkan agar laporan masalah tanpa login bisa untuk mengirim foto. Dan admin juga bisa melihat foto dari semua pengaduan dan layanan administrasi yang nanti di pdf juga bisa di lihat"

### Research Findings
- **iText 7.2.5** sudah ada (kernel + layout), tapi belum ada modul `io` yang dibutuhkan untuk embed gambar (`ImageDataFactory`)
- **`FileUploadController`** sudah ada di `/api/v1/upload` tapi memerlukan autentikasi JWT
- **`PublicPengaduanController`** pakai `@RequestBody Map` (JSON) — tidak bisa terima file
- **`LampiranFile`** entity sudah siap: `referensiId`, `tipeReferensi`, `urlFile`, `tipeFile`
- **`SecurityConfig`** sudah permit `/api/v1/public/**` dan `/uploads/**`
- **Admin dashboard** sudah render lampiran sebagai `<a href>` link — perlu upgrade ke `<img>` untuk foto
- **`LaporanController`** sudah list lampiran di PDF tapi hanya sebagai teks nama file
- **`application.properties`** belum ada konfigurasi multipart file size

### Guardrails dari Analisis
- Jangan ubah endpoint `/api/v1/upload` yang sudah authenticated — buat endpoint baru `/api/v1/public/upload`
- Validasi tipe file di backend: hanya jpg, jpeg, png, gif untuk foto publik
- Validasi ukuran file: max 5MB di frontend dan backend
- Saat embed PDF: handle gracefully jika file tidak ada atau bukan gambar (skip, jangan crash)
- Foto di PDF dibatasi lebar max 400pt agar tidak overflow halaman

---

## Work Objectives

### Core Objective
Mengaktifkan upload foto pada form pengaduan publik, menampilkan foto tersebut di dashboard admin, dan meng-embed foto ke dalam PDF laporan.

### Concrete Deliverables
- `pom.xml` — tambah dependency `com.itextpdf:io:7.2.5`
- `application.properties` — tambah multipart config
- `PublicPengaduanController.java` — endpoint baru `/api/v1/public/upload` + ubah `/api/v1/public/pengaduan` ke multipart
- `public-pengaduan.html` — input foto + preview + kirim sebagai FormData
- `admin-dashboard.html` — render foto sebagai `<img>` di modal detail pengaduan dan layanan
- `LaporanController.java` — embed gambar ke PDF pengaduan dan layanan

### Must Have
- Upload foto tanpa login berfungsi
- Foto tampil sebagai gambar (bukan link) di admin dashboard
- Foto ter-embed di PDF pengaduan
- Foto lampiran ter-embed di PDF layanan administrasi

### Must NOT Have (Guardrails)
- Jangan hapus atau ubah endpoint `/api/v1/upload` yang sudah ada (masih dipakai warga login)
- Jangan ubah struktur `LampiranFile` entity
- Jangan tambah autentikasi ke endpoint public upload
- Jangan crash PDF jika file foto tidak ditemukan — skip gracefully

---

## Verification Strategy

### Test Decision
- **Infrastructure exists**: NO (H2 in-memory, no test files)
- **Automated tests**: NO
- **Agent-Executed QA**: YES (manual curl + browser check)

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Foundation - bisa paralel):
├── Task 1: pom.xml + application.properties (config)
├── Task 2: PublicPengaduanController — endpoint public upload + ubah ke multipart
└── Task 3: public-pengaduan.html — form foto + preview + FormData submit

Wave 2 (Setelah Wave 1):
├── Task 4: admin-dashboard.html — render foto sebagai <img> di modal
└── Task 5: LaporanController.java — embed gambar ke PDF pengaduan + layanan

Wave FINAL:
└── Task F1: Verifikasi end-to-end (build + manual test)
```

---

## TODOs

- [ ] 1. Tambah dependency iText `io` + konfigurasi multipart

  **What to do**:
  - Di `pom.xml`, tambahkan dependency baru setelah blok iText yang sudah ada:
    ```xml
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>io</artifactId>
        <version>7.2.5</version>
    </dependency>
    ```
  - Di `src/main/resources/application.properties`, tambahkan di akhir file:
    ```properties
    # Multipart file upload
    spring.servlet.multipart.enabled=true
    spring.servlet.multipart.max-file-size=5MB
    spring.servlet.multipart.max-request-size=10MB
    ```

  **Must NOT do**:
  - Jangan ubah versi iText yang sudah ada (7.2.5)
  - Jangan hapus dependency kernel atau layout

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (dengan Task 2 dan 3)
  - **Blocks**: Task 5 (butuh iText io untuk embed image)
  - **Blocked By**: None

  **References**:
  - `pom.xml:82-92` — blok iText yang sudah ada, tambahkan setelah baris 92
  - `src/main/resources/application.properties` — tambahkan di akhir file

  **Acceptance Criteria**:
  - [ ] `pom.xml` mengandung `<artifactId>io</artifactId>` dengan version 7.2.5
  - [ ] `application.properties` mengandung `spring.servlet.multipart.max-file-size=5MB`

  **QA Scenarios**:
  ```
  Scenario: Verifikasi dependency terdaftar
    Tool: Bash
    Steps:
      1. grep "io" pom.xml | grep "7.2.5"
    Expected Result: Baris dependency io ditemukan
    Evidence: .sisyphus/evidence/task-1-pom-check.txt

  Scenario: Verifikasi multipart config
    Tool: Bash
    Steps:
      1. grep "multipart" src/main/resources/application.properties
    Expected Result: 3 baris multipart config ditemukan
    Evidence: .sisyphus/evidence/task-1-props-check.txt
  ```

  **Commit**: YES (groups dengan Task 2)
  - Message: `feat(config): tambah iText io dependency dan multipart config`
  - Files: `pom.xml`, `src/main/resources/application.properties`

- [ ] 2. Buat endpoint public upload foto + ubah endpoint pengaduan publik ke multipart

  **What to do**:
  - Di `PublicPengaduanController.java`, tambahkan:
    1. Import `MultipartFile`, `Files`, `Paths`, `StandardCopyOption`, `UUID`, `LampiranFile`, `LampiranFileRepository`
    2. Inject `LampiranFileRepository` via constructor
    3. Tambah konstanta: `MAX_FILE_SIZE = 5MB`, `ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg","jpeg","png","gif")`
    4. Tambah method helper `getUploadDir()` yang membuat folder `uploads/` jika belum ada
    5. Tambah endpoint baru:
       ```java
       @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
       public ResponseEntity<ApiResponse<Map<String,Object>>> uploadPublicFoto(
           @RequestParam("file") MultipartFile file,
           @RequestParam(value = "referensiId", required = false) String referensiId) {
           // validasi: tidak kosong, max 5MB, hanya jpg/jpeg/png/gif
           // simpan ke uploads/ dengan UUID filename
           // simpan LampiranFile dengan tipeReferensi="PENGADUAN_PUBLIK"
           // return urlFile
       }
       ```
    6. Ubah endpoint `/pengaduan` dari `@RequestBody Map<String,Object>` ke `@RequestParam` multipart:
       ```java
       @PostMapping(value = "/pengaduan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
       public ResponseEntity<ApiResponse<Map<String,Object>>> createPublicPengaduan(
           @RequestParam("namaPelapor") String namaPelapor,
           @RequestParam("kontakPelapor") String kontakPelapor,
           @RequestParam("judul") String judul,
           @RequestParam("deskripsi") String deskripsi,
           @RequestParam(value = "lokasi", required = false, defaultValue = "") String lokasi,
           @RequestParam(value = "prioritas", required = false, defaultValue = "SEDANG") String prioritasStr,
           @RequestParam(value = "foto", required = false) MultipartFile foto) {
           // logika sama seperti sebelumnya
           // jika foto tidak null dan tidak empty: simpan foto, buat LampiranFile dengan referensiId=pengaduan.getId()
       }
       ```
    7. Pastikan foto disimpan SETELAH pengaduan di-save (agar ada ID)

  **Must NOT do**:
  - Jangan hapus atau ubah `FileUploadController` yang sudah ada
  - Jangan tambah `@Secured` atau autentikasi ke endpoint public
  - Jangan ubah endpoint `/api/v1/upload` yang sudah ada

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (dengan Task 1 dan 3)
  - **Blocks**: Task 3 (frontend butuh tahu endpoint baru), Task 4 (admin butuh data lampiran)
  - **Blocked By**: None (logika tidak bergantung iText)

  **References**:
  - `src/main/java/com/smartpelayanan/controller/PublicPengaduanController.java` — file yang diubah
  - `src/main/java/com/smartpelayanan/controller/FileUploadController.java:39-95` — pola upload yang sama (getUploadDir, UUID filename, LampiranFile save)
  - `src/main/java/com/smartpelayanan/entity/LampiranFile.java` — entity yang dipakai
  - `src/main/java/com/smartpelayanan/repository/LampiranFileRepository.java` — repository
  - `src/main/java/com/smartpelayanan/config/SecurityConfig.java:44` — `/api/v1/public/**` sudah permit all, endpoint baru otomatis tercakup

  **Acceptance Criteria**:
  - [ ] Endpoint `POST /api/v1/public/upload` menerima multipart file tanpa token
  - [ ] Endpoint `POST /api/v1/public/pengaduan` menerima multipart form-data dengan field foto opsional
  - [ ] File foto tersimpan di folder `uploads/`
  - [ ] Record `LampiranFile` tersimpan di database dengan `referensiId` = ID pengaduan

  **QA Scenarios**:
  ```
  Scenario: Upload foto publik tanpa token
    Tool: Bash (curl)
    Preconditions: Aplikasi berjalan di port 8082, ada file foto.jpg
    Steps:
      1. curl -X POST http://localhost:8082/api/v1/public/upload -F "file=@foto.jpg"
      2. Cek response: status 200, data.urlFile berisi "/uploads/xxx.jpg"
      3. Cek file ada di folder uploads/
    Expected Result: HTTP 200, urlFile valid, file ada di disk
    Evidence: .sisyphus/evidence/task-2-upload-response.txt

  Scenario: Kirim pengaduan dengan foto
    Tool: Bash (curl)
    Steps:
      1. curl -X POST http://localhost:8082/api/v1/public/pengaduan -F "namaPelapor=Budi" -F "kontakPelapor=08123" -F "judul=Jalan Rusak" -F "deskripsi=Jalan berlubang" -F "foto=@foto.jpg"
      2. Cek response: status 200, data.id ada
    Expected Result: HTTP 200, pengaduan tersimpan dengan lampiran foto
    Evidence: .sisyphus/evidence/task-2-pengaduan-foto.txt

  Scenario: Upload file bukan gambar (harus ditolak)
    Tool: Bash (curl)
    Steps:
      1. curl -X POST http://localhost:8082/api/v1/public/upload -F "file=@dokumen.pdf"
      2. Cek response: status 400, pesan error tipe file tidak diizinkan
    Expected Result: HTTP 400 dengan pesan error
    Evidence: .sisyphus/evidence/task-2-upload-reject.txt
  ```

  **Commit**: YES (groups dengan Task 1)
  - Message: `feat(public): endpoint upload foto publik + pengaduan multipart`
  - Files: `src/main/java/com/smartpelayanan/controller/PublicPengaduanController.java`

- [ ] 3. Update form public-pengaduan.html: input foto + preview + kirim FormData

  **What to do**:
  - Tambah CSS untuk area upload foto (drag-drop style atau input biasa):
    ```css
    .foto-upload { border: 2px dashed #e74c3c; border-radius: 10px; padding: 20px; text-align: center; cursor: pointer; transition: all 0.3s; }
    .foto-upload:hover { background: rgba(231,76,60,0.05); }
    .foto-preview { max-width: 100%; max-height: 200px; border-radius: 8px; margin-top: 10px; display: none; }
    .foto-info { font-size: 12px; color: #666; margin-top: 6px; }
    ```
  - Tambah form group baru setelah field prioritas (sebelum tombol submit):
    ```html
    <div class="form-group">
        <label>Foto Bukti <span style="color:#666;font-weight:400">(opsional, max 5MB)</span></label>
        <div class="foto-upload" onclick="document.getElementById('fotoInput').click()">
            <i class="fas fa-camera" style="font-size:24px;color:#e74c3c;margin-bottom:8px;display:block"></i>
            <span id="fotoLabel">Klik untuk pilih foto (jpg, png, gif)</span>
        </div>
        <input type="file" id="fotoInput" accept="image/jpeg,image/png,image/gif" style="display:none">
        <img id="fotoPreview" class="foto-preview" alt="Preview foto">
        <div class="foto-info" id="fotoInfo"></div>
        <div class="field-error" id="fotoError">Ukuran foto maksimal 5MB</div>
    </div>
    ```
  - Update JavaScript submit handler:
    1. Ubah dari `fetch` dengan `JSON.stringify` ke `FormData`
    2. Tambah validasi foto: jika ada file, cek ukuran <= 5MB
    3. Append semua field ke FormData: namaPelapor, kontakPelapor, judul, deskripsi, lokasi, prioritas, foto (jika ada)
    4. Kirim ke `/api/v1/public/pengaduan` dengan method POST, tanpa `Content-Type` header (biarkan browser set boundary)
    5. Tambah event listener `fotoInput` untuk preview gambar dan tampilkan nama file
    6. Hapus `headers: { 'Content-Type': 'application/json' }` dari fetch options

  **Must NOT do**:
  - Jangan ubah styling yang sudah ada (warna, font, layout utama)
  - Jangan buat foto menjadi field wajib
  - Jangan tambah library baru (pakai vanilla JS)

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (dengan Task 1 dan 2)
  - **Blocks**: None
  - **Blocked By**: None (bisa dikerjakan paralel, tapi pastikan endpoint di Task 2 sudah sesuai)

  **References**:
  - `src/main/resources/templates/public-pengaduan.html` — file yang diubah
  - `src/main/resources/templates/public-pengaduan.html:152-165` — posisi form row prioritas (tambah setelah ini)
  - `src/main/resources/templates/public-pengaduan.html:177-234` — JavaScript submit handler yang perlu diubah
  - `src/main/resources/templates/public-pengaduan.html:8-102` — CSS yang sudah ada (ikuti style yang sama)

  **Acceptance Criteria**:
  - [ ] Input foto muncul di form dengan label "Foto Bukti (opsional)"
  - [ ] Preview gambar muncul setelah file dipilih
  - [ ] Validasi ukuran 5MB berjalan (tampilkan error jika lebih besar)
  - [ ] Form submit berhasil dengan foto (FormData, bukan JSON)
  - [ ] Form submit berhasil tanpa foto (foto opsional)

  **QA Scenarios**:
  ```
  Scenario: Submit form dengan foto
    Tool: Playwright
    Preconditions: Aplikasi berjalan, buka http://localhost:8082/public-pengaduan
    Steps:
      1. Isi field namaPelapor: "Budi Santoso"
      2. Isi field kontakPelapor: "081234567890"
      3. Isi field judul: "Jalan Berlubang"
      4. Isi field deskripsi: "Jalan di RT 03 berlubang besar"
      5. Klik area foto upload, pilih file gambar jpg
      6. Verifikasi preview gambar muncul (img#fotoPreview visible)
      7. Klik tombol "Kirim Laporan"
      8. Tunggu response (max 10s)
      9. Verifikasi pesan sukses muncul (div#successMsg visible)
    Expected Result: Pesan sukses tampil dengan ID laporan
    Evidence: .sisyphus/evidence/task-3-submit-foto.png

  Scenario: Submit form tanpa foto (foto opsional)
    Tool: Playwright
    Steps:
      1. Isi semua field wajib tanpa pilih foto
      2. Klik "Kirim Laporan"
      3. Verifikasi pesan sukses muncul
    Expected Result: Form berhasil tanpa foto
    Evidence: .sisyphus/evidence/task-3-submit-tanpa-foto.png

  Scenario: Pilih file terlalu besar (>5MB)
    Tool: Playwright
    Steps:
      1. Pilih file >5MB di input foto
      2. Verifikasi div#fotoError muncul dengan pesan error ukuran
    Expected Result: Error ukuran tampil, form tidak bisa submit
    Evidence: .sisyphus/evidence/task-3-foto-terlalu-besar.png
  ```

  **Commit**: YES (groups dengan Task 4)
  - Message: `feat(ui): form pengaduan publik dengan upload foto`
  - Files: `src/main/resources/templates/public-pengaduan.html`

- [ ] 4. Update admin dashboard: render foto sebagai gambar di modal detail pengaduan

  **What to do**:
  - Di `admin-dashboard.html`, cari fungsi `openDetailPengaduan(id)` (sekitar baris 804)
  - Ubah bagian render lampiran dari link teks menjadi render gambar untuk file foto:
    ```javascript
    // Sebelum (hanya link):
    d.lampiran.forEach(l => { html += `<li><a href="${l.urlFile}" target="_blank">${l.namaFile}</a></li>`; });

    // Sesudah (gambar untuk foto, link untuk non-foto):
    d.lampiran.forEach(l => {
        const isImage = l.tipeFile && l.tipeFile.startsWith('image/');
        if (isImage) {
            html += `<div style="margin-bottom:10px">
                <img src="${l.urlFile}" alt="${l.namaFile}"
                     style="max-width:100%;max-height:300px;border-radius:8px;border:1px solid #e1e1e1;cursor:pointer"
                     onclick="window.open('${l.urlFile}','_blank')"
                     title="Klik untuk buka penuh">
                <div style="font-size:12px;color:#666;margin-top:4px">${l.namaFile}</div>
            </div>`;
        } else {
            html += `<div style="margin-bottom:6px"><a href="${l.urlFile}" target="_blank"><i class="fas fa-paperclip"></i> ${l.namaFile}</a></div>`;
        }
    });
    ```
  - Tambah section header "Foto/Lampiran:" sebelum loop lampiran
  - Lakukan hal yang sama untuk modal detail layanan administrasi (`openDetailLayanan`) jika ada lampiran foto di sana juga

  **Must NOT do**:
  - Jangan ubah struktur modal atau CSS yang sudah ada
  - Jangan hapus link download untuk file non-gambar

  **Recommended Agent Profile**:
  - **Category**: `visual-engineering`
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (dengan Task 5)
  - **Blocks**: None
  - **Blocked By**: Task 2 (butuh data lampiran dari backend)

  **References**:
  - `src/main/resources/templates/admin-dashboard.html:804-833` — fungsi `openDetailPengaduan` yang diubah
  - `src/main/resources/templates/admin-dashboard.html:824-828` — bagian render lampiran yang diubah
  - `src/main/resources/templates/admin-dashboard.html:839-900` — fungsi `openDetailLayanan` (cek apakah ada lampiran juga)
  - `src/main/java/com/smartpelayanan/controller/LayananDetailController.java:79-89` — endpoint detail pengaduan sudah return `tipeFile` di lampiran

  **Acceptance Criteria**:
  - [ ] Foto tampil sebagai `<img>` di modal detail pengaduan (bukan link teks)
  - [ ] Klik gambar membuka foto di tab baru
  - [ ] File non-gambar tetap tampil sebagai link download
  - [ ] Modal detail layanan juga menampilkan foto lampiran sebagai gambar

  **QA Scenarios**:
  ```
  Scenario: Admin melihat foto di modal detail pengaduan
    Tool: Playwright
    Preconditions: Login sebagai admin, ada pengaduan dengan foto lampiran
    Steps:
      1. Buka http://localhost:8082/admin/dashboard
      2. Login dengan kredensial admin
      3. Klik menu "Pengaduan"
      4. Klik tombol mata (detail) pada pengaduan yang punya foto
      5. Verifikasi modal terbuka
      6. Verifikasi elemen img ada di dalam div#detailPengaduanBody
      7. Verifikasi src img berisi "/uploads/"
    Expected Result: Foto tampil sebagai gambar di modal
    Evidence: .sisyphus/evidence/task-4-admin-foto-modal.png

  Scenario: Lampiran non-foto tetap sebagai link
    Tool: Playwright
    Steps:
      1. Buka detail pengaduan yang punya lampiran PDF/DOC
      2. Verifikasi lampiran tampil sebagai link <a>, bukan <img>
    Expected Result: Link download tampil untuk file non-gambar
    Evidence: .sisyphus/evidence/task-4-admin-link-nonfoto.png
  ```

  **Commit**: YES (groups dengan Task 3)
  - Message: `feat(admin): tampilkan foto lampiran sebagai gambar di modal detail`
  - Files: `src/main/resources/templates/admin-dashboard.html`

- [ ] 5. Update LaporanController: embed foto ke PDF pengaduan dan layanan

  **What to do**:
  - Tambah import di `LaporanController.java`:
    ```java
    import com.itextpdf.io.image.ImageDataFactory;
    import com.itextpdf.layout.element.Image;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    ```
  - Di method `exportPengaduanPdf()`, ganti bagian lampiran (sekitar baris 204-211) dari teks menjadi embed gambar:
    ```java
    List<LampiranFile> lampiranList = lampiranFileRepository.findByReferensiId(id.toString());
    if (!lampiranList.isEmpty()) {
        document.add(new Paragraph("Foto/Lampiran").setBold().setFontSize(12));
        for (LampiranFile l : lampiranList) {
            String tipe = l.getTipeFile() != null ? l.getTipeFile() : "";
            if (tipe.startsWith("image/")) {
                try {
                    // Resolve path dari urlFile "/uploads/xxx.jpg" ke path absolut
                    String filename = l.getNamaTersimpan() != null ? l.getNamaTersimpan() : l.getUrlFile().replace("/uploads/", "");
                    Path imgPath = Paths.get("uploads").toAbsolutePath().resolve(filename);
                    if (Files.exists(imgPath)) {
                        ImageData imageData = ImageDataFactory.create(imgPath.toUri().toURL());
                        Image img = new Image(imageData);
                        img.setMaxWidth(400); // max 400pt agar tidak overflow
                        img.setAutoScale(true);
                        document.add(new Paragraph(l.getNamaFile()).setFontSize(10).setItalic());
                        document.add(img);
                        document.add(new Paragraph("\n"));
                    } else {
                        document.add(new Paragraph("- " + l.getNamaFile() + " (file tidak ditemukan)"));
                    }
                } catch (Exception imgEx) {
                    // Jika gagal embed, fallback ke teks
                    document.add(new Paragraph("- " + l.getNamaFile() + " (" + (l.getUrlFile() != null ? l.getUrlFile() : "-") + ")"));
                }
            } else {
                document.add(new Paragraph("- " + l.getNamaFile() + " (" + (l.getUrlFile() != null ? l.getUrlFile() : "-") + ")"));
            }
        }
    }
    ```
  - Lakukan hal yang sama di method `exportLayananPdf()` — cari bagian lampiran dan ganti dengan logika embed gambar yang sama
  - Pastikan `document.close()` tetap dipanggil di finally block atau setelah semua konten ditambahkan

  **Must NOT do**:
  - Jangan ubah struktur tabel info pengaduan/layanan yang sudah ada
  - Jangan crash jika file tidak ada — gunakan try-catch per gambar
  - Jangan embed file non-gambar (PDF, DOC) sebagai gambar

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: []

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 2 (dengan Task 4)
  - **Blocks**: None
  - **Blocked By**: Task 1 (butuh iText io dependency)

  **References**:
  - `src/main/java/com/smartpelayanan/controller/LaporanController.java:150-227` — method `exportPengaduanPdf` yang diubah
  - `src/main/java/com/smartpelayanan/controller/LaporanController.java:45-148` — method `exportLayananPdf` yang diubah
  - `src/main/java/com/smartpelayanan/controller/LaporanController.java:204-211` — bagian lampiran pengaduan (ganti ini)
  - `src/main/java/com/smartpelayanan/entity/LampiranFile.java` — field `namaTersimpan`, `tipeFile`, `urlFile`
  - iText 7 docs: `ImageDataFactory.create(URL)` untuk load gambar dari file path

  **Acceptance Criteria**:
  - [ ] PDF pengaduan mengandung gambar foto yang ter-embed (bukan teks URL)
  - [ ] PDF layanan administrasi mengandung gambar lampiran foto
  - [ ] PDF tidak crash jika file foto tidak ada di disk
  - [ ] File non-gambar tetap ditampilkan sebagai teks di PDF

  **QA Scenarios**:
  ```
  Scenario: Download PDF pengaduan dengan foto
    Tool: Bash (curl)
    Preconditions: Ada pengaduan dengan foto lampiran, aplikasi berjalan
    Steps:
      1. Login sebagai admin, dapatkan token JWT
      2. curl -H "Authorization: Bearer TOKEN" http://localhost:8082/api/v1/laporan/pengaduan/ID/pdf -o test.pdf
      3. Buka test.pdf dan verifikasi gambar ter-embed
    Expected Result: PDF berhasil didownload, mengandung gambar foto
    Evidence: .sisyphus/evidence/task-5-pdf-dengan-foto.pdf

  Scenario: PDF tidak crash jika foto tidak ada di disk
    Tool: Bash (curl)
    Preconditions: Ada pengaduan dengan record LampiranFile tapi file fisik dihapus
    Steps:
      1. Download PDF pengaduan tersebut
      2. Verifikasi HTTP 200 (tidak 500)
      3. PDF mengandung teks "(file tidak ditemukan)" untuk lampiran yang hilang
    Expected Result: PDF berhasil dibuat tanpa crash
    Evidence: .sisyphus/evidence/task-5-pdf-file-hilang.txt
  ```

  **Commit**: YES
  - Message: `feat(pdf): embed foto lampiran ke PDF pengaduan dan layanan`
  - Files: `src/main/java/com/smartpelayanan/controller/LaporanController.java`

---

## Final Verification Wave

- [ ] F1. **Build & Smoke Test** — `unspecified-high`
  Jalankan `mvn clean package -DskipTests` atau `./mvnw clean package -DskipTests`. Pastikan tidak ada compile error. Jalankan aplikasi dan test manual: POST ke `/api/v1/public/upload` dengan file gambar, lalu POST ke `/api/v1/public/pengaduan` dengan foto. Cek admin dashboard modal detail pengaduan menampilkan gambar. Download PDF pengaduan dan verifikasi gambar ter-embed.
  Output: `Build [PASS/FAIL] | Upload [PASS/FAIL] | Admin foto [PASS/FAIL] | PDF foto [PASS/FAIL]`

---

## Commit Strategy

- **1**: `feat(pengaduan): upload foto publik, tampil di admin, embed di PDF`

---

## Success Criteria

### Verification Commands
```bash
# Upload foto tanpa token
curl -X POST http://localhost:8082/api/v1/public/upload \
  -F "file=@foto.jpg" \
  -F "tipeReferensi=PENGADUAN"
# Expected: {"status":200,"data":{"urlFile":"/uploads/xxx.jpg",...}}

# Kirim pengaduan dengan foto
curl -X POST http://localhost:8082/api/v1/public/pengaduan \
  -F "namaPelapor=Test" -F "kontakPelapor=08123" \
  -F "judul=Test Foto" -F "deskripsi=Test deskripsi" \
  -F "foto=@foto.jpg"
# Expected: {"status":200,"data":{"id":"...","judul":"Test Foto",...}}
```

### Final Checklist
- [ ] Upload foto tanpa login berhasil (HTTP 200)
- [ ] Foto muncul sebagai `<img>` di modal admin (bukan hanya link)
- [ ] PDF pengaduan mengandung gambar foto
- [ ] PDF layanan administrasi mengandung gambar lampiran foto
- [ ] Build tidak ada error
