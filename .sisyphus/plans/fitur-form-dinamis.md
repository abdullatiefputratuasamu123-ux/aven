# Form Dinamis per Kategori Layanan + Detail Laporan Admin

## TL;DR

> **Quick Summary**: Menambahkan sistem form dinamis per kategori layanan (admin konfigurasi field, warga isi form sesuai kategori), plus fitur detail laporan lengkap untuk admin (lihat form, preview file, catatan internal, export PDF, riwayat status).
>
> **Deliverables**:
> - Entity `FormField` — definisi field per kategori (tipe, label, required, urutan)
> - Entity `JawabanForm` — jawaban warga per field per permohonan layanan
> - Entity `RiwayatStatus` — audit trail perubahan status
> - API CRUD field form untuk admin/superadmin
> - API submit layanan dengan jawaban form + upload file per field
> - API detail laporan: pengaduan lengkap + layanan lengkap
> - API export PDF laporan
> - UI admin: konfigurasi field form per kategori, modal detail laporan
> - UI warga: form dinamis saat ajukan layanan (field sesuai kategori)
> - Seed data: 6 kategori default dengan field masing-masing
>
> **Estimated Effort**: Large
> **Parallel Execution**: YES — 3 waves
> **Critical Path**: Wave 1 (entity) → Wave 2 (API) → Wave 3 (frontend)

---

## Context

### Original Request
- Setiap kategori layanan punya form dan file upload tersendiri yang dikonfigurasi admin
- Admin bisa cek full laporan pengaduan dan layanan (detail form, file, riwayat)

### Key Decisions
- **Form dinamis**: Entity `FormField` menyimpan definisi field (tipe: text/number/textarea/select/date/file, label, required, urutan, opsi untuk select). Admin CRUD field dari dashboard.
- **Jawaban form**: Entity `JawabanForm` menyimpan jawaban warga (field_id + layanan_id + nilai). File upload tetap pakai `LampiranFile` yang sudah ada, tapi dikaitkan ke field_id juga.
- **Detail laporan**: Endpoint baru yang return data lengkap termasuk jawaban form, lampiran, riwayat status
- **Export PDF**: Pakai iText 7 (dependency baru di pom.xml). Generate PDF server-side dengan semua data laporan.
- **Riwayat status**: Entity `RiwayatStatus` sudah ada tapi belum dipakai — aktifkan dan isi saat status berubah.
- **Seed data**: 6 kategori (KTP, KK, Surat Keterangan, Izin Usaha, Surat Kematian, Akta Kelahiran) dengan field default di DataInitializer.

### Existing Code
- `KategoriLayanan.java` — entity kategori, pakai Integer ID
- `LayananAdministrasi.java` — entity permohonan layanan, punya `keperluan` dan `dokumenPendukung`
- `LampiranFile.java` — sudah ada, simpan file dengan referensiId + tipeReferensi
- `RiwayatStatus.java` — sudah ada entity tapi belum diisi
- `PengaduanServiceImpl.convertPengaduanToMap()` — ada bug: `pengaduan.getUser()` bisa null (pengaduan publik)

---

## Work Objectives

### Core Objective
Admin bisa konfigurasi field form per kategori layanan. Warga mengisi form sesuai kategori saat mengajukan layanan. Admin bisa lihat detail lengkap semua laporan dan export PDF.

### Concrete Deliverables
- `FormField.java` + `FormFieldRepository.java`
- `JawabanForm.java` + `JawabanFormRepository.java`
- `RiwayatStatusLayanan.java` + repository (terpisah dari RiwayatStatus pengaduan)
- `FormFieldController.java` — CRUD field form (admin/superadmin only)
- `LayananDetailController.java` — detail layanan + jawaban form + lampiran
- `LaporanController.java` — detail pengaduan + export PDF
- Update `WebController.java` — submit layanan dengan jawaban form, fix null user bug
- Update `SecurityConfig.java` — permit endpoint baru
- Update `PageController.java` — route detail modal (jika perlu)
- Update `pom.xml` — tambah iText 7 dependency
- Update `admin-dashboard.html` — modal detail laporan pengaduan + layanan, konfigurasi field form
- Update `warga-dashboard.html` — form dinamis saat ajukan layanan
- Update `DataInitializer.java` — seed 6 kategori + field default

### Must Have
- Admin bisa tambah/edit/hapus field form per kategori dari dashboard
- Warga melihat form yang berbeda sesuai kategori yang dipilih
- Setiap field tipe `file` menghasilkan input upload terpisah
- Admin bisa klik baris layanan → modal detail dengan semua jawaban form + preview link file
- Admin bisa klik baris pengaduan → modal detail dengan deskripsi lengkap + semua lampiran
- Export PDF: satu laporan per klik, berisi semua data permohonan
- Riwayat status tercatat (siapa, kapan, dari status apa ke apa)

### Must NOT Have
- JANGAN ubah struktur `LampiranFile` yang sudah ada
- JANGAN hapus field `keperluan` di `LayananAdministrasi` — tetap diisi sebagai ringkasan
- JANGAN pakai WebSocket atau real-time
- JANGAN ubah flow login/auth
- JANGAN ubah `RiwayatStatus.java` yang sudah ada (untuk pengaduan) — buat entity terpisah untuk layanan

---

## Verification Strategy

### Test Decision
- **Infrastructure exists**: NO
- **Automated tests**: None
- **Build verification**: `mvn compile` harus BUILD SUCCESS setelah setiap wave

---

## Execution Strategy

```
Wave 1 (Foundation):
├── Task 1: Tambah iText 7 ke pom.xml
├── Task 2: Buat FormField.java + FormFieldRepository.java
├── Task 3: Buat JawabanForm.java + JawabanFormRepository.java
└── Task 4: Buat RiwayatStatusLayanan.java + repository + update DataInitializer

Wave 2 (Backend API):
├── Task 5: FormFieldController.java — CRUD field per kategori
├── Task 6: Update WebController.java — submit layanan dengan jawaban form + fix null user bug
├── Task 7: LayananDetailController.java — GET detail layanan + jawaban + lampiran
├── Task 8: LaporanController.java — GET detail pengaduan + export PDF pengaduan + export PDF layanan
└── Task 9: Update SecurityConfig.java + pom.xml multipart config

Wave 3 (Frontend):
├── Task 10: Update admin-dashboard.html — modal detail pengaduan, modal detail layanan, tab konfigurasi field form
└── Task 11: Update warga-dashboard.html — form dinamis saat ajukan layanan
```

---

## TODOs

- [x] 1. Tambah dependency iText 7 ke `pom.xml`

  **What to do**:
  - Buka `pom.xml`
  - Tambahkan dependency iText 7 sebelum tag `</dependencies>`:
    ```xml
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>itext7-core</artifactId>
        <version>7.2.5</version>
        <type>pom</type>
    </dependency>
    ```
  - Atau alternatif lebih ringan, tambahkan hanya modul yang dibutuhkan:
    ```xml
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>kernel</artifactId>
        <version>7.2.5</version>
    </dependency>
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>layout</artifactId>
        <version>7.2.5</version>
    </dependency>
    ```

  **Recommended Agent Profile**: `quick`
  **Parallelization**: Wave 1, bisa paralel dengan task 2-4. **Blocks**: Task 8. **Blocked By**: None.
  **Acceptance Criteria**: [ ] `mvn compile` BUILD SUCCESS dengan dependency iText

- [x] 2. Buat `FormField.java` + `FormFieldRepository.java`

  **What to do**:
  - Buat `src/main/java/com/smartpelayanan/entity/FormField.java`:
    ```java
    @Entity @Table(name = "tb_form_field")
    public class FormField extends BaseEntity {
        @Column(name = "label", nullable = false) String label;
        @Column(name = "tipe", nullable = false) String tipe; // text|number|textarea|select|date|file
        @Column(name = "required") Boolean required = true;
        @Column(name = "urutan") Integer urutan = 0;
        @Column(name = "opsi", columnDefinition = "TEXT") String opsi; // JSON array untuk select: ["Opsi1","Opsi2"]
        @Column(name = "placeholder") String placeholder;
        @ManyToOne @JoinColumn(name = "kategori_id") KategoriLayanan kategori;
        // getter setter semua field
    }
    ```
  - Buat `FormFieldRepository.java`:
    - `List<FormField> findByKategoriIdOrderByUrutanAsc(Integer kategoriId)`
    - `void deleteByKategoriId(Integer kategoriId)`

  **Recommended Agent Profile**: `quick`
  **Parallelization**: Wave 1 paralel. **Blocks**: Task 5, 6, 10, 11. **Blocked By**: None.
  **Acceptance Criteria**: [ ] File ada, compile sukses, repository method ada

- [x] 3. Buat `JawabanForm.java` + `JawabanFormRepository.java`

  **What to do**:
  - Buat `src/main/java/com/smartpelayanan/entity/JawabanForm.java`:
    ```java
    @Entity @Table(name = "tb_jawaban_form")
    public class JawabanForm extends BaseEntity {
        @ManyToOne @JoinColumn(name = "layanan_id") LayananAdministrasi layanan;
        @ManyToOne @JoinColumn(name = "field_id") FormField field;
        @Column(name = "nilai", columnDefinition = "TEXT") String nilai; // nilai teks atau URL file
        // getter setter
    }
    ```
  - Buat `JawabanFormRepository.java`:
    - `List<JawabanForm> findByLayananId(UUID layananId)`
    - `void deleteByLayananId(UUID layananId)`

  **Recommended Agent Profile**: `quick`
  **Parallelization**: Wave 1 paralel. **Blocks**: Task 6, 7, 10. **Blocked By**: Task 2.
  **Acceptance Criteria**: [ ] File ada, compile sukses

- [x] 4. Update `DataInitializer.java` — seed 6 kategori dengan field default + `RiwayatStatusLayanan`

  **What to do**:
  - Buat `src/main/java/com/smartpelayanan/entity/RiwayatStatusLayanan.java`:
    ```java
    @Entity @Table(name = "tb_riwayat_status_layanan")
    public class RiwayatStatusLayanan extends BaseEntity {
        @ManyToOne @JoinColumn(name = "layanan_id") LayananAdministrasi layanan;
        @Column(name = "status_lama") String statusLama;
        @Column(name = "status_baru") String statusBaru;
        @Column(name = "catatan", columnDefinition = "TEXT") String catatan;
        @Column(name = "diubah_oleh") String diubahOleh; // email admin
        // getter setter
    }
    ```
  - Buat `RiwayatStatusLayananRepository.java` dengan method `findByLayananIdOrderByCreatedAtDesc`
  - Update `DataInitializer.java`: inject `FormFieldRepository`. Setelah membuat kategori, tambahkan field default untuk 6 kategori:
    - **KTP**: NIK (number, required), Nama Lengkap (text, required), Alamat (textarea, required), Keperluan KTP (select: Baru/Perpanjang/Hilang, required), Foto KTP Lama (file, required jika perpanjang — buat optional)
    - **KK**: NIK Kepala Keluarga (number, required), Jenis Perubahan (select: Baru/Tambah Anggota/Pisah KK, required), Nama Anggota Baru (text), Dokumen Pendukung (file, required)
    - **Surat Keterangan**: Jenis Surat (select: Domisili/Tidak Mampu/Usaha/Lainnya, required), Tujuan Surat (text, required), Ditujukan Kepada (text, required), Keperluan (textarea, required)
    - **Izin Usaha**: Nama Usaha (text, required), Jenis Usaha (text, required), Alamat Usaha (textarea, required), Modal Usaha (number), Foto Tempat Usaha (file, required), Dokumen Identitas (file, required)
    - **Surat Kematian**: Nama Almarhum (text, required), NIK Almarhum (number, required), Tanggal Meninggal (date, required), Tempat Meninggal (text, required), Penyebab Kematian (text), Surat Keterangan RS/Dokter (file, required)
    - **Akta Kelahiran**: Nama Bayi (text, required), Tanggal Lahir (date, required), Tempat Lahir (text, required), Jenis Kelamin (select: Laki-laki/Perempuan, required), Nama Ayah (text, required), Nama Ibu (text, required), Surat Keterangan Lahir RS (file, required)
  - Cek dulu apakah kategori sudah ada sebelum insert (pakai `kategoriLayananRepository.count() == 0`)

  **Recommended Agent Profile**: `quick`
  **Parallelization**: Wave 1 paralel. **Blocks**: Task 5, 6, 7. **Blocked By**: Task 2.
  **Acceptance Criteria**: [ ] `RiwayatStatusLayanan.java` ada [ ] DataInitializer seed field untuk 6 kategori [ ] compile sukses

- [x] 5. Buat `FormFieldController.java` — CRUD field per kategori

  **What to do**:
  - Buat `src/main/java/com/smartpelayanan/controller/FormFieldController.java`
  - `GET /api/v1/kategori/{id}/fields` — list semua field untuk kategori (public, butuh token)
  - `POST /api/v1/admin/kategori/{id}/fields` — tambah field baru (admin/superadmin only)
    - Body: `{ label, tipe, required, urutan, opsi, placeholder }`
  - `PUT /api/v1/admin/fields/{fieldId}` — update field
  - `DELETE /api/v1/admin/fields/{fieldId}` — hapus field
  - `PUT /api/v1/admin/kategori/{id}/fields/reorder` — update urutan semua field sekaligus
    - Body: `[{ id, urutan }]`
  - Validasi: tipe harus salah satu dari text/number/textarea/select/date/file
  - Untuk tipe `select`, `opsi` wajib diisi (JSON array string)

  **Recommended Agent Profile**: `quick`
  **Parallelization**: Wave 2 paralel. **Blocks**: Task 10. **Blocked By**: Task 2.
  **References**: `src/main/java/com/smartpelayanan/controller/WebController.java` — pola controller
  **Acceptance Criteria**: [ ] GET /api/v1/kategori/{id}/fields mengembalikan list field [ ] POST/PUT/DELETE berfungsi

- [x] 6. Update `WebController.java` — submit layanan dengan jawaban form + fix null user bug

  **What to do**:
  - **Fix bug null user**: Di `convertPengaduanToMap()` di `PengaduanServiceImpl.java`, baris `pengaduan.getUser().getId()` akan NPE untuk pengaduan publik. Fix:
    ```java
    if (pengaduan.getUser() != null) {
        userMap.put("id", pengaduan.getUser().getId());
        userMap.put("namaLengkap", pengaduan.getUser().getNamaLengkap());
        userMap.put("email", pengaduan.getUser().getEmail());
    } else {
        userMap.put("namaLengkap", pengaduan.getNamaPelapor());
        userMap.put("email", pengaduan.getKontakPelapor());
    }
    map.put("namaPelapor", pengaduan.getNamaPelapor());
    map.put("kontakPelapor", pengaduan.getKontakPelapor());
    ```
  - **Update `POST /api/v1/warga/layanan`** di `WebController.java`:
    - Request body sekarang berisi `jawaban` (Map<String, String> fieldId → nilai) selain `kategoriId` dan `keperluan`
    - Setelah simpan `LayananAdministrasi`, loop `jawaban` dan simpan setiap `JawabanForm`
    - Inject `JawabanFormRepository` dan `FormFieldRepository` ke constructor
    - Set `keperluan` dari jawaban field pertama yang bukan file, atau dari field `keperluan` jika ada
  - **Update `PATCH /api/v1/admin/layanan/{id}/status`**: setelah update status, simpan `RiwayatStatusLayanan` dengan statusLama, statusBaru, catatan, dan email admin (dari JWT)
  - Inject `RiwayatStatusLayananRepository` ke constructor

  **Recommended Agent Profile**: `quick`
  **Parallelization**: Wave 2 paralel. **Blocks**: Task 10, 11. **Blocked By**: Task 3, 4.
  **References**: `src/main/java/com/smartpelayanan/controller/WebController.java`, `src/main/java/com/smartpelayanan/service/impl/PengaduanServiceImpl.java`
  **Acceptance Criteria**: [ ] Submit layanan dengan jawaban form tersimpan [ ] Null user bug fixed [ ] Riwayat status tersimpan

- [x] 7. Buat `LayananDetailController.java` — detail layanan lengkap

  **What to do**:
  - Buat `src/main/java/com/smartpelayanan/controller/LayananDetailController.java`
  - `GET /api/v1/layanan/{id}/detail` — return detail lengkap satu permohonan layanan:
    ```json
    {
      "id": "...",
      "nomorPermohonan": "PLY-...",
      "status": "MENUNGGU",
      "tglDiajukan": "2024-01-01",
      "user": { "namaLengkap": "...", "email": "...", "noTelp": "..." },
      "kategori": { "id": 1, "namaKategori": "KTP" },
      "jawaban": [
        { "fieldId": "...", "label": "NIK", "tipe": "number", "nilai": "3201..." },
        { "fieldId": "...", "label": "Foto KTP", "tipe": "file", "nilai": "/uploads/xxx.jpg" }
      ],
      "lampiran": [ { "id": "...", "namaFile": "ktp.jpg", "urlFile": "/uploads/..." } ],
      "riwayatStatus": [ { "statusLama": "MENUNGGU", "statusBaru": "DIPROSES", "diubahOleh": "admin@...", "tanggal": "..." } ],
      "catatanPetugas": "..."
    }
    ```
  - `GET /api/v1/pengaduan/{id}/detail` — return detail lengkap satu pengaduan:
    ```json
    {
      "id": "...", "judul": "...", "deskripsi": "...", "lokasi": "...",
      "status": "BARU", "prioritas": "SEDANG", "tanggalKejadian": "...",
      "pelapor": { "nama": "...", "kontak": "..." },
      "lampiran": [...],
      "catatanAdmin": "...",
      "riwayatStatus": [...]
    }
    ```
  - Butuh autentikasi (admin/superadmin)

  **Recommended Agent Profile**: `quick`
  **Parallelization**: Wave 2 paralel. **Blocks**: Task 10. **Blocked By**: Task 3, 4.
  **Acceptance Criteria**: [ ] GET /api/v1/layanan/{id}/detail return data lengkap [ ] GET /api/v1/pengaduan/{id}/detail return data lengkap

- [x] 8. Buat `LaporanController.java` — export PDF

  **What to do**:
  - Buat `src/main/java/com/smartpelayanan/controller/LaporanController.java`
  - `GET /api/v1/laporan/layanan/{id}/pdf` — generate dan download PDF permohonan layanan
    - PDF berisi: header "SmartPelayanan", nomor permohonan, tanggal, data pemohon, kategori, semua jawaban form (label: nilai), status, catatan petugas
    - Pakai iText 7: `PdfWriter`, `PdfDocument`, `Document`, `Paragraph`, `Table`
    - Response: `application/pdf` dengan header `Content-Disposition: attachment; filename="laporan-PLY-xxx.pdf"`
  - `GET /api/v1/laporan/pengaduan/{id}/pdf` — generate dan download PDF pengaduan
    - PDF berisi: header, ID pengaduan, tanggal, data pelapor, judul, deskripsi, lokasi, prioritas, status, catatan admin
  - Butuh autentikasi

  **Recommended Agent Profile**: `quick`
  **Parallelization**: Wave 2 paralel. **Blocks**: Task 10. **Blocked By**: Task 1 (iText dependency).
  **References**: iText 7 API: `com.itextpdf.kernel.pdf.*`, `com.itextpdf.layout.*`
  **Acceptance Criteria**: [ ] GET /api/v1/laporan/layanan/{id}/pdf mengembalikan file PDF [ ] PDF berisi data lengkap

- [x] 9. Update `SecurityConfig.java` — permit endpoint baru

  **What to do**:
  - Tambahkan ke `authenticated()`:
    - `/api/v1/layanan/**`
    - `/api/v1/pengaduan/**/detail`
    - `/api/v1/laporan/**`
    - `/api/v1/admin/fields/**`
    - `/api/v1/admin/kategori/**/fields`
  - Tambahkan ke `permitAll()`:
    - `/api/v1/kategori/**/fields` (GET list field untuk warga saat isi form)

  **Recommended Agent Profile**: `quick`
  **Parallelization**: Wave 2 paralel. **Blocks**: Task 10, 11. **Blocked By**: None.
  **Acceptance Criteria**: [ ] GET /api/v1/kategori/{id}/fields bisa diakses dengan token warga [ ] PDF endpoint butuh auth

- [x] 10. Update `admin-dashboard.html` — modal detail + konfigurasi field form

  **What to do**:
  - **Modal Detail Pengaduan**: Tambahkan tombol "Detail" di kolom aksi tabel pengaduan. Klik → fetch `GET /api/v1/pengaduan/{id}/detail` → tampilkan modal dengan semua data: deskripsi lengkap, lampiran (link preview), riwayat status, catatan admin. Tombol "Export PDF" → `window.open('/api/v1/laporan/pengaduan/{id}/pdf?token='+token)`.
  - **Modal Detail Layanan**: Tambahkan tombol "Detail" di kolom aksi tabel layanan. Klik → fetch `GET /api/v1/layanan/{id}/detail` → tampilkan modal dengan: semua jawaban form (label: nilai), lampiran file (link preview/download), riwayat status, catatan petugas. Tombol "Export PDF".
  - **Tab/Section Konfigurasi Field Form**: Tambahkan menu sidebar "Konfigurasi Form". Section ini menampilkan:
    - Dropdown pilih kategori
    - Tabel field yang sudah ada (label, tipe, required, urutan) dengan tombol edit/hapus
    - Tombol "Tambah Field" → modal form: label, tipe (dropdown), required (checkbox), urutan (number), placeholder, opsi (textarea, muncul jika tipe=select)
    - Tombol "Simpan Urutan" untuk drag-and-drop atau input urutan manual
  - Semua fetch ke endpoint baru menggunakan `Authorization: Bearer {token}`

  **Recommended Agent Profile**: `quick`
  **Parallelization**: Wave 3. **Blocks**: None. **Blocked By**: Task 5, 7, 8, 9.
  **References**: `src/main/resources/templates/admin-dashboard.html` — ikuti pola modal yang sudah ada
  **Acceptance Criteria**: [ ] Tombol Detail di tabel pengaduan dan layanan berfungsi [ ] Modal menampilkan data lengkap [ ] Export PDF berfungsi [ ] Konfigurasi field form bisa tambah/edit/hapus field

- [x] 11. Update `warga-dashboard.html` — form dinamis saat ajukan layanan

  **What to do**:
  - Update fungsi `showLayananModal()`:
    1. Fetch `GET /api/v1/kategori/list` (dengan token) untuk dropdown kategori
    2. Saat warga pilih kategori dari dropdown, fetch `GET /api/v1/kategori/{id}/fields` (dengan token)
    3. Render field-field secara dinamis di dalam modal berdasarkan response:
       - `text` → `<input type="text">`
       - `number` → `<input type="number">`
       - `textarea` → `<textarea>`
       - `select` → `<select>` dengan opsi dari field.opsi (parse JSON)
       - `date` → `<input type="date">`
       - `file` → `<input type="file">` dengan label field tersebut
    4. Field dengan `required=true` diberi atribut `required`
  - Update fungsi `submitLayanan()`:
    1. Kumpulkan semua jawaban field (kecuali tipe file) ke object `jawaban: { fieldId: nilai }`
    2. POST ke `/api/v1/warga/layanan` dengan body `{ kategoriId, keperluan: ringkasan, jawaban }`
    3. Setelah dapat `layananId`, upload setiap file (field tipe file) ke `/api/v1/upload` dengan `referensiId=layananId`, `tipeReferensi=LAYANAN`, dan tambahkan `fieldId` sebagai parameter
    4. Tampilkan progress upload jika ada banyak file
  - Tambahkan event listener `onchange` di dropdown kategori untuk re-render form

  **Recommended Agent Profile**: `quick`
  **Parallelization**: Wave 3. **Blocks**: None. **Blocked By**: Task 5, 6, 9.
  **References**: `src/main/resources/templates/warga-dashboard.html` — ikuti pola modal yang sudah ada
  **Acceptance Criteria**: [ ] Pilih kategori → form berubah sesuai field [ ] Field file menghasilkan input upload terpisah [ ] Submit menyimpan jawaban dan upload file

---

## Final Verification Wave

- [x] F1. **Compile & Smoke Test** — `quick`
  Jalankan `C:\tools\apache-maven-3.9.15\bin\mvn.cmd compile`. Harus BUILD SUCCESS.
  Test: GET /api/v1/kategori/1/fields dengan token → harus return list field KTP.
  Test: POST /api/v1/warga/layanan dengan jawaban form → harus tersimpan.
  Output: `Build [PASS/FAIL] | Fields API [PASS/FAIL] | Submit Layanan [PASS/FAIL]`

---

## Commit Strategy

- **Wave 1**: `feat(entity): add FormField, JawabanForm, RiwayatStatusLayanan entities + seed data`
- **Wave 2**: `feat(api): add form field CRUD, layanan detail, PDF export endpoints`
- **Wave 3**: `feat(frontend): add dynamic form per category, admin detail modal, PDF export`

---

## Success Criteria

```bash
# Compile
C:\tools\apache-maven-3.9.15\bin\mvn.cmd compile
# Expected: BUILD SUCCESS

# Get fields for kategori KTP (id=1)
curl http://localhost:8082/api/v1/kategori/1/fields -H "Authorization: Bearer {token}"
# Expected: list field NIK, Nama Lengkap, Alamat, Keperluan KTP, Foto KTP Lama

# Export PDF
curl http://localhost:8082/api/v1/laporan/layanan/{id}/pdf -H "Authorization: Bearer {token}" -o laporan.pdf
# Expected: file PDF terdownload
```
