# Penjelasan Entity Classes

## BaseEntity.java
Abstract class yang menjadi superclass untuk entitas lainnya (Inheritance):
- **id** (UUID) - Primary key dengan auto generation
- **createdAt** (LocalDateTime) - Waktu pembuatan record, diisi otomatis oleh @PrePersist
- **updatedAt** (LocalDateTime) - Waktu update record, diisi otomatis oleh @PreUpdate

Mengimplementasikan konsep **Inheritance** - class User, Pengaduan, LayananAdministrasi, ResponPengaduan, dan RiwayatStatus semuanya extends BaseEntity.

## User.java
Entity untuk tabel `tb_user`:
- **namaLengkap** - Nama lengkap pengguna
- **email** - Email unik untuk login
- **password** - Password (BCrypt hash)
- **noTelp** - Nomor telepon
- **alamat** - Alamat user
- **role** - Enum RoleEnum (WARGA/ADMIN)
- **statusAktif** - Status aktif user (default: true)

Relasi:
- One-to-Many ke Pengaduan
- One-to-Many ke LayananAdministrasi
- One-to-Many ke ResponPengaduan

## KategoriLayanan.java
Entity untuk tabel `tb_kategori_layanan`:
- **id** (Integer, AUTO_INCREMENT) - Primary key
- **namaKategori** - Nama kategori layanan
- **deskripsi** - Deskripsi kategori
- **isActive** - Status aktif kategori

Relasi: One-to-Many ke LayananAdministrasi

## Pengaduan.java
Entity untuk tabel `tb_pengaduan` (extends BaseEntity):
- **judul** - Judul pengaduan
- **deskripsi** - Isi pengaduan
- **lokasi** - Lokasi kejadian
- **status** - StatusPengaduanEnum (default: BARU)
- **prioritas** - PrioritasEnum (default: SEDANG)
- **fotoBukti** - Path/URL foto bukti
- **tanggalKejadian** - Tanggal kejadian
- **catatanAdmin** - Catatan dari admin

Relasi:
- Many-to-One ke User
- One-to-Many ke ResponPengaduan

Method: **isOpen()** - Mengecek apakah status masih BARU

## LayananAdministrasi.java
Entity untuk tabel `tb_layanan_administrasi` (extends BaseEntity):
- **nomorPermohonan** - Nomor unik permohonan (auto-generate: LAY-timestamp)
- **keperluan** - Keperluan layanan
- **dokumenPendukung** - Path/URL dokumen
- **status** - StatusLayananEnum (default: MENUNGGU)
- **catatanPetugas** - Catatan petugas
- **tglDiajukan** - Tanggal diajukan
- **tglSelesai** - Tanggal selesai

Relasi:
- Many-to-One ke User
- Many-to-One ke KategoriLayanan

Method: **generateNomorPermohonan()** - Generate nomor permohonan otomatis

## ResponPengaduan.java
Entity untuk tabel `tb_respon_pengaduan` (extends BaseEntity):
- **isiRespon** - Isi tanggapan admin
- **tglRespon** - Tanggal respon

Relasi:
- Many-to-One ke Pengaduan
- Many-to-One ke User (admin)

## RiwayatStatus.java
Entity untuk tabel `tb_riwayat_status` (extends BaseEntity):
- **referensiId** (UUID) - ID pengaduan atau layanan
- **tipeReferensi** - Enum PENGADUAN/LAYANAN (polymorphic)
- **statusLama** - Status sebelum diubah
- **statusBaru** - Status setelah diubah
- **alasanPerubahan** - Alasan perubahan status
- **changedAt** - Waktu perubahan

Relasi: Many-to-One ke User (admin)

Inner Enum: **TipeReferensiEnum** - PENGADUAN, LAYANAN

## Penerapan OOP
- **Inheritance**: BaseEntity sebagai abstract superclass
- **Encapsulation**: Field private dengan getter/setter
- **Polymorphism**: Melalui service interface (dijelaskan di service layer)
