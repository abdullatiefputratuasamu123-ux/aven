@echo off
title SmartPelayanan - Backend Server
cd /d "%~dp0"

echo ========================================
echo   SmartPelayanan - Run Application
echo ========================================
echo.
echo  URL: http://localhost:8082/login
echo.
echo  Superadmin: superadmin@smartpelayanan.com / superadmin123
echo  Admin:      admin@smartpelayanan.com / admin123
echo  Warga 1:    warga@gmail.com / warga123
echo  Warga 2:    warga2@gmail.com / warga123
echo ========================================
echo.

echo [1/2] Cek Java...
echo.
java -version >"%TEMP%\java_check.tmp" 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ========================================
    echo  [ERROR] Java tidak ditemukan!
    echo  Download: https://adoptium.net/
    echo  Install lalu restart komputer.
    echo ========================================
    echo.
    pause
    exit /b 1
)
type "%TEMP%\java_check.tmp" | findstr /i "version"
echo.

echo [2/2] Build & Run...
echo.

set "JAR_FILE="
for /f "delims=" %%f in ('dir /b /o-d "target\*.jar" 2^>nul') do (
    if not defined JAR_FILE set "JAR_FILE=target\%%f"
)

if not defined JAR_FILE (
    echo  File JAR belum ada, melakukan build dulu...
    call mvn package -DskipTests -q
    if %ERRORLEVEL% NEQ 0 (
        echo ========================================
        echo  [ERROR] Build gagal!
        echo ========================================
        pause
        exit /b 1
    )
    for /f "delims=" %%f in ('dir /b /o-d "target\*.jar" 2^>nul') do (
        if not defined JAR_FILE set "JAR_FILE=target\%%f"
    )
) else (
    echo  Build ulang untuk memastikan kode terbaru...
    call mvn package -DskipTests -q
    if %ERRORLEVEL% NEQ 0 (
        echo  [WARNING] Build gagal, menggunakan JAR lama...
        set "JAR_FILE="
        for /f "delims=" %%f in ('dir /b /o-d "target\*.jar" 2^>nul') do (
            if not defined JAR_FILE set "JAR_FILE=target\%%f"
        )
    )
)

if not defined JAR_FILE (
    echo ========================================
    echo  [ERROR] File JAR tidak ditemukan!
    echo ========================================
    pause
    exit /b 1
)

echo  File JAR ditemukan: %JAR_FILE%
echo.
echo  Menjalankan server...
echo  Server: http://localhost:8082
echo  Tekan Ctrl+C untuk menghentikan.
echo.

java -jar "%JAR_FILE%"

echo.
echo ========================================
echo  Server telah berhenti.
echo ========================================
echo.
pause
