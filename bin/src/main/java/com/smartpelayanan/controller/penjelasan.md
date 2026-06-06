# Penjelasan Controller Layer

Controller layer menangani HTTP request dan response.

## PengaduanController.java
REST API untuk modul pengaduan (`/api/v1/pengaduan`):
- **GET** `/` - Ambil semua pengaduan (filter: status, prioritas, page, size)
- **GET** `/{id}` - Ambil detail pengaduan by ID
- **POST** `/` - Buat pengaduan baru (201 Created)
- **PUT** `/{id}` - Update pengaduan (full update)
- **PATCH** `/{id}/status` - Update status pengaduan
- **DELETE** `/{id}` - Hapus pengaduan
- **GET** `/{id}/respon` - Ambil respon pengaduan
- **POST** `/{id}/respon` - Tambah respon (admin)

Inner classes:
- `StatusUpdateRequest` - Request body untuk update status
- `ResponRequest` - Request body untuk tambah respon

## AuthController.java
REST API untuk autentikasi (`/api/v1/auth`):
- **POST** `/register` - Registrasi user baru
- **POST** `/login` - Login dan dapatkan JWT token
- **POST** `/refresh` - Refresh token
- **GET** `/profile` - Ambil profil user

Inner classes:
- `RegisterRequest` - Body registrasi
- `LoginRequest` - Body login
- `RefreshRequest` - Body refresh token

## LayananController.java
REST API untuk layanan administrasi (`/api/v1/layanan`):
- **GET** `/` - Ambil semua layanan (filter: status, kategori)
- **GET** `/{id}` - Ambil detail layanan
- **POST** `/` - Buat permohonan layanan baru
- **PATCH** `/{id}/status` - Update status layanan
- **DELETE** `/{id}` - Batalkan permohonan

## KategoriController.java
REST API untuk kategori layanan (`/api/v1/kategori`) - Admin only:
- **GET** `/` - Ambil semua kategori
- **POST** `/` - Buat kategori baru
- **PUT** `/{id}` - Update kategori
- **DELETE** `/{id}` - Hapus kategori

## DashboardController.java
REST API untuk statistik (`/api/v1/dashboard`):
- **GET** `/stats` - Ambil ringkasan statistik

## RiwayatController.java
REST API untuk riwayat status (`/api/v1/riwayat-status`):
- **GET** `/{id}` - Ambil riwayat perubahan status

## Penerapan OOP
- **Abstraction**: Controller hanya berinteraksi dengan Service interface, tidak tahu detail implementasi
- **Dependency Injection**: @Autowired menyuntikkan service ke controller
- **Encapsulation**: Request/Response body menggunakan inner classes yang mengkapsulasi data
