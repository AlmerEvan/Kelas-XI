@echo off
REM Set JAVA_HOME
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
set Path=%Path%;%JAVA_HOME%\bin

REM Navigate to project
cd /d "D:\Aplikasi Monitoring Kelas\AplikasiMonitoringKelasFe"

REM Stop gradle daemon
call gradlew --stop

REM Clean and build
echo Building project...
call gradlew clean build -x test --warning-mode all

echo.
echo Build complete!
pause
