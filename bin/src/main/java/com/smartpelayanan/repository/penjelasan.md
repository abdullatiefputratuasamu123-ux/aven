# Penjelasan Repository Layer

Repository layer menggunakan Spring Data JPA untuk akses database.

## UserRepository.java
Interface untuk tabel `tb_user`:
- Extend: `JpaRepository<User, UUID>`
- Method tambahan:
  - `findByEmail(String email)` - Cari user by email
  - `existsByEmail(String email)` - Cek email exists

## PengaduanRepository.java
Interface untuk tabel `tb_pengaduan`:
- Extend: `JpaRepository<Pengaduan, UUID>`
- Method tambahan:
  - `findByUserId(UUID userId)` - Cari pengaduan by user ID
  - `findByStatus(StatusPengaduanEnum status)` - Filter by status
  - `findByPrioritas(PrioritasEnum prioritas)` - Filter by prioritas

## LayananAdministrasiRepository.java
Interface untuk tabel `tb_layanan_administrasi`:
- Extend: `JpaRepository<LayananAdministrasi, UUID>`
- Method tambahan:
  - `findByUserId(UUID userId)` - Cari layanan by user ID
  - `findByStatus(StatusLayananEnum status)` - Filter by status
  - `findByKategoriId(Integer kategoriId)` - Filter by kategori

## KategoriLayananRepository.java
Interface untuk tabel `tb_kategori_layanan`:
- Extend: `JpaRepository<KategoriLayanan, Integer>`
- Method tambahan:
  - `existsByNamaKategori(String namaKategori)` - Cek kategori exists

## ResponPengaduanRepository.java
Interface untuk tabel `tb_respon_pengaduan`:
- Extend: `JpaRepository<ResponPengaduan, UUID>`
- Method tambahan:
  - `findByPengaduanId(UUID pengaduanId)` - Cari respon by pengaduan ID

## RiwayatStatusRepository.java
Interface untuk tabel `tb_riwayat_status`:
- Extend: `JpaRepository<RiwayatStatus, UUID>`
- Method tambahan:
  - `findByReferensiId(UUID referensiId)` - Cari riwayat by referensi ID

## Penerapan OOP
- **Inheritance**: Semua repository extend JpaRepository (Spring Data JPA)
- **Abstraction**: Repository menyembunyikan detail query SQL, developer hanya perlu mendefinisikan method signature
