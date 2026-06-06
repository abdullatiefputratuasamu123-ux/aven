@echo off
chcp 65001 >nul
echo ================================================
echo   Update Tubes_OOP.docx - SmartPelayanan
echo ================================================
python "%~dp0update_docx.py"
if errorlevel 1 (
    echo.
    echo [ERROR] Gagal! Pastikan Python sudah terinstall.
    echo Coba jalankan: python --version
) else (
    echo.
    echo [SUKSES] Dokumen berhasil diperbarui!
)
echo.
pause
