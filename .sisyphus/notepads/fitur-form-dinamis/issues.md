# Issues - fitur-form-dinamis

## 2026-05-14

### Known Bug
- `PengaduanServiceImpl.convertPengaduanToMap()` lines 162-164: `pengaduan.getUser().getId()` will NPE for public pengaduan (user is null)
- Fix: wrap in null check before accessing user fields
