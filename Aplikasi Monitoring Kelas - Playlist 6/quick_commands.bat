@echo off
REM Quick Reference for Running App on Physical Device
REM ====================================================

setlocal enabledelayedexpansion

set SDK_PATH=C:\Users\User\AppData\Local\Android\sdk
set ADB=%SDK_PATH%\platform-tools\adb.exe
set PROJECT_PATH=D:\Aplikasi Monitoring Kelas\AplikasiMonitoringKelasFe
set APP_PACKAGE=com.kelasxi.aplikasimonitoringkelas
set APP_ACTIVITY=MainActivity

cls
echo.
echo ========================================
echo  Android USB Debugging - Quick Commands
echo ========================================
echo.
echo 1. Check connected devices
echo    %ADB% devices
echo.
echo 2. Build and install (debug version)
echo    cd %PROJECT_PATH%
echo    gradlew installDebug
echo.
echo 3. Uninstall app
echo    %ADB% uninstall %APP_PACKAGE%
echo.
echo 4. Run app
echo    %ADB% shell am start -n %APP_PACKAGE%/.%APP_ACTIVITY%
echo.
echo 5. View logs
echo    %ADB% logcat
echo.
echo 6. Clear app data
echo    %ADB% shell pm clear %APP_PACKAGE%
echo.
echo 7. Restart ADB daemon
echo    %ADB% kill-server
echo    %ADB% start-server
echo.
echo 8. Full workflow (build, install, run)
echo    cd %PROJECT_PATH%
echo    gradlew clean installDebug
echo    %ADB% shell am start -n %APP_PACKAGE%/.%APP_ACTIVITY%
echo.
echo ========================================
echo.
echo Select an option (1-8):
set /p choice="Enter choice: "

if "%choice%"=="1" (
    call %ADB% devices
) else if "%choice%"=="2" (
    cd %PROJECT_PATH%
    call gradlew installDebug
) else if "%choice%"=="3" (
    call %ADB% uninstall %APP_PACKAGE%
) else if "%choice%"=="4" (
    call %ADB% shell am start -n %APP_PACKAGE%/.%APP_ACTIVITY%
) else if "%choice%"=="5" (
    call %ADB% logcat
) else if "%choice%"=="6" (
    call %ADB% shell pm clear %APP_PACKAGE%
) else if "%choice%"=="7" (
    call %ADB% kill-server
    timeout /t 2
    call %ADB% start-server
) else if "%choice%"=="8" (
    cd %PROJECT_PATH%
    call gradlew clean installDebug
    timeout /t 5
    call %ADB% shell am start -n %APP_PACKAGE%/.%APP_ACTIVITY%
) else (
    echo Invalid choice
)

echo.
pause
