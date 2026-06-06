# Decisions - fitur-form-dinamis

## 2026-05-14

### Wave 1 Status
- Tasks 1-3 already complete (entities, repos, pom.xml)
- Task 4 partial: RiwayatStatusLayanan entity done, but DataInitializer NOT updated

### Task Execution Order
- Wave 1 remaining: Task 4 (DataInitializer seed)
- Wave 2 parallel: Tasks 5, 6, 7, 8, 9
- Wave 3 parallel: Tasks 10, 11

### Security
- `/api/v1/kategori/{id}/fields` (GET) should be permitAll or authenticated with warga token
- Plan says permitAll for GET fields endpoint
- All admin endpoints need authenticated()
