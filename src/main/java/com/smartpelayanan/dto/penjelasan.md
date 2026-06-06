# Penjelasan DTO (Data Transfer Object)

## PengaduanDTO.java
DTO digunakan untuk mentransfer data antara Layer tanpa mengekspos Entity langsung:
- **id** (UUID) - ID pengaduan
- **judul** - Judul pengaduan
- **deskripsi** - Isi pengaduan
- **lokasi** - Lokasi kejadian
- **status** - StatusPengaduanEnum
- **prioritas** - PrioritasEnum
- **fotoBukti** - Path foto bukti
- **tanggalKejadian** - Tanggal kejadian
- **catatanAdmin** - Catatan admin
- **namaUser** - Nama user (flattened dari relasi)

DTO memisahkan representasi data API dari struktur database Entity.

## ApiResponse.java
Generic wrapper class untuk standarisasi response API:
- **status** (int) - HTTP status code
- **message** (String) - Pesan response
- **data** (T) - Payload data (generic type)
- **timestamp** (LocalDateTime) - Waktu response

Static methods:
- **success(T data)** - Membuat response sukses dengan data
- **error(String message)** - Membuat response error

Format response standar:
```json
{
  "status": 200,
  "message": "Success",
  "data": { ... },
  "timestamp": "2026-05-03T20:30:00"
}
```

## Penerapan OOP
- **Encapsulation**: Field private dengan getter/setter
- **Generics**: ApiResponse<T> menggunakan generic type untuk fleksibilitas
