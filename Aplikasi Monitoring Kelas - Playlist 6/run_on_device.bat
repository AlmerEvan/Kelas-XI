@echo off
REM One-Command Quick Setup and Run

setlocal enabledelayedexpansion

set SDK_PATH=C:\Users\User\AppData\Local\Android\sdk
set ADB=%SDK_PATH%\platform-tools\adb.exe
set PROJECT_PATH=D:\Aplikasi Monitoring Kelas\AplikasiMonitoringKelasFe

echo.
echo ========================================
echo  Quick Setup and Run
echo ========================================
echo.
echo This script will:
echo 1. Check device connection
echo 2. Build the app (clean)
echo 3. Install on device
echo 4. Launch the app
echo.
echo Make sure:
echo - Device is connected via USB
echo - USB Debugging is enabled
echo.

pause

REM Check device
echo Checking connected devices...
call %ADB% devices
echo.

REM Ask confirmation
set /p confirm="Device listed above? (Y/N): "
if /i not "%confirm%"=="Y" (
    echo Aborted.
    pause
    exit /b 1
)

REM Build and install
echo.
echo Building and installing app...
echo.
cd %PROJECT_PATH%
call gradlew clean installDebug

if errorlevel 1 (
    echo Build failed!
    pause
    exit /b 1
)

echo.
echo Build successful! Launching app...
echo.

REM Launch app
call %ADB% shell am start -n com.kelasxi.aplikasimonitoringkelas/.MainActivity

echo.
echo ========================================
echo  App launched!
echo ========================================
echo.
echo Login with:
echo  Email: siswa@sekolah.com
echo  Password: password123
echo.
echo To view logs:
echo  %ADB% logcat
echo.

pause
