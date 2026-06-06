# Penjelasan Enum - RoleEnum, StatusPengaduanEnum, StatusLayananEnum, PrioritasEnum

## RoleEnum.java
Enum yang mendefinisikan peran pengguna dalam sistem SmartPelayanan:
- **WARGA** - Pengguna biasa yang dapat mengajukan pengaduan dan permohonan layanan
- **ADMIN** - Petugas yang memproses pengaduan dan layanan

## StatusPengaduanEnum.java
Enum yang mendefinisikan status progres pengaduan warga:
- **BARU** - Pengaduan baru diajukan
- **DIPROSES** - Pengaduan sedang ditangani admin
- **SELESAI** - Pengaduan telah selesai ditangani
- **DITOLAK** - Pengaduan ditolak

## StatusLayananEnum.java
Enum yang mendefinisikan status progres permohonan layanan administrasi:
- **MENUNGGU** - Permohonan menunggu diproses
- **DIPROSES** - Permohonan sedang diproses
- **SELESAI** - Permohonan selesai
- **DITOLAK** - Permohonan ditolak

## PrioritasEnum.java
Enum yang mendefinisikan tingkat urgensi pengaduan:
- **RENDAH** - Prioritas rendah
- **SEDANG** - Prioritas sedang (default)
- **TINGGI** - Prioritas tinggi

## Penerapan OOP
- **Encapsulation**: Enum menyembunyikan implementasi detail dan hanya mengekspos konstanta yang aman digunakan
- **Type Safety**: Menggunakan enum mencegah invalid values masuk ke database
