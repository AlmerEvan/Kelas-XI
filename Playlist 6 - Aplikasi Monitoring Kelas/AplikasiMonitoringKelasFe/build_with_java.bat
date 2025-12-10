@echo off
setlocal enabledelayedexpansion

REM Set JAVA_HOME to Android Studio bundled JDK
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "PATH=!JAVA_HOME!\bin;!PATH!"

REM Verify Java
echo Checking Java installation...
java -version

if errorlevel 1 (
    echo ERROR: Java not found!
    pause
    exit /b 1
)

echo.
echo ===== Building Android App =====
echo.

REM Navigate to app folder
cd /d "D:\Aplikasi Monitoring Kelas\AplikasiMonitoringKelasFe"

REM Clean
echo Cleaning build...
call gradlew clean

REM Build
echo.
echo Building app (this may take 5-10 minutes on first build)...
call gradlew build -x test --warning-mode all

if errorlevel 1 (
    echo.
    echo BUILD FAILED
    echo Try:
    echo 1. Close Android Studio
    echo 2. Delete: .gradle folder
    echo 3. Run this script again
    pause
    exit /b 1
)

echo.
echo ===== BUILD SUCCESSFUL =====
echo.
echo You can now:
echo 1. Open project in Android Studio
echo 2. Click Run (Shift+F10)
echo 3. Select connected device
echo.
pause
