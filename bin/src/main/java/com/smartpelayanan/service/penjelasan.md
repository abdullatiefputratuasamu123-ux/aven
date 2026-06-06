# Penjelasan Service Layer

Service layer berisi logika bisnis aplikasi.

## PengaduanService.java (Interface)
Interface/service contract untuk modul pengaduan:
- `findAll()` - Ambil semua pengaduan
- `findById(UUID id)` - Cari pengaduan by ID
- `save(PengaduanDTO)` - Simpan pengaduan baru
- `update(UUID id, PengaduanDTO)` - Update pengaduan
- `updateStatus(UUID id, StatusPengaduanEnum)` - Update status
- `delete(UUID id)` - Hapus pengaduan

## PengaduanServiceImpl.java
Implementasi dari PengaduanService:
- **@Service** - Spring stereotype untuk service layer
- **@Autowired** - Dependency Injection untuk repository

Method utama:
- `findAll()` - Convert entity ke DTO
- `findById()` - Cari dan convert ke DTO
- `save()` - Convert DTO ke entity, simpan, convert balik ke DTO
- `update()` - Update field entity dari DTO
- `updateStatus()` - Update status pengaduan
- `delete()` - Hapus pengaduan by ID
- `validateOwnership()` - Validasi pemilik pengaduan (authorization)

Helper methods (private):
- `convertToDTO()` - Convert Pengaduan entity ke PengaduanDTO
- `convertToEntity()` - Convert PengaduanDTO ke Pengaduan entity

## Penerapan OOP
- **Polymorphism**: Controller hanya kenal interface PengaduanService, Spring inject implementation secara dinamis
- **Abstraction**: Interface menyembunyikan detail implementasi dari Controller
- **Inheritance**: PengaduanServiceImpl implements PengaduanService
- **Dependency Injection**: @Autowired menyuntikkan repository ke service
