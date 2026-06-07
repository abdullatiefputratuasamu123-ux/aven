@echo off
setlocal EnableExtensions EnableDelayedExpansion
title SmartPelayanan - Backend Server
cd /d "%~dp0"

set "APP_JAR=target\smartpelayanan-1.0.0.jar"
set "APP_URL=http://localhost:8082/login"

echo ========================================
echo   SmartPelayanan - Run Application
echo ========================================
echo.
echo  URL: %APP_URL%
echo.
echo  Superadmin: superadmin@smartpelayanan.com / superadmin123
echo  Admin:      admin@smartpelayanan.com / admin123
echo  Warga 1:    warga@gmail.com / warga123
echo  Warga 2:    warga2@gmail.com / warga123
echo ========================================
echo.

echo [1/4] Cek Java...
echo.
java -version >"%TEMP%\smartpelayanan_java_check.tmp" 2>&1
if errorlevel 1 (
    echo ========================================
    echo  [ERROR] Java tidak ditemukan!
    echo  Download: https://adoptium.net/
    echo  Install lalu restart komputer.
    echo ========================================
    echo.
    pause
    exit /b 1
)
type "%TEMP%\smartpelayanan_java_check.tmp" | findstr /i "version"
echo.

echo [2/4] Cek Maven...
echo.
where mvn >nul 2>&1
if errorlevel 1 (
    echo ========================================
    echo  [ERROR] Maven tidak ditemukan di PATH!
    echo  Install Maven atau jalankan dari terminal yang sudah punya mvn.
    echo ========================================
    echo.
    pause
    exit /b 1
)
mvn -v | findstr /i "Apache Maven Java version"
echo.

echo [3/4] Hentikan server lama jika masih berjalan...
echo.
powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$procs = Get-CimInstance Win32_Process -Filter \"name='java.exe'\" | Where-Object { $_.CommandLine -like '*smartpelayanan-1.0.0.jar*' }; if ($procs) { $procs | ForEach-Object { Write-Host ('  Stop PID ' + $_.ProcessId); Stop-Process -Id $_.ProcessId -Force } } else { Write-Host '  Tidak ada server lama.' }"
if errorlevel 1 (
    echo  [WARNING] Gagal mengecek proses lama. Tutup manual jika build tetap gagal.
)
echo.

echo [4/4] Build dan Run...
echo.
echo  Build ulang untuk memastikan kode terbaru...
call mvn package -DskipTests -q
if errorlevel 1 (
    echo ========================================
    echo  [ERROR] Build gagal!
    echo  Tutup aplikasi Java lama lalu jalankan run.bat lagi.
    echo ========================================
    echo.
    pause
    exit /b 1
)

if not exist "%APP_JAR%" (
    echo ========================================
    echo  [ERROR] File JAR tidak ditemukan: %APP_JAR%
    echo ========================================
    echo.
    pause
    exit /b 1
)

echo  File JAR siap: %APP_JAR%
echo.
echo  Menjalankan server...
echo  Server: http://localhost:8082
echo  Tekan Ctrl+C untuk menghentikan.
echo.

java -jar "%APP_JAR%"

echo.
echo ========================================
echo  Server telah berhenti.
echo ========================================
echo.
pause
