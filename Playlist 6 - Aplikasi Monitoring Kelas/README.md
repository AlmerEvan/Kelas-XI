# Aplikasi Monitoring Kelas - Complete Setup Guide

Sistem monitoring kehadiran siswa dengan backend Laravel dan frontend Android Jetpack Compose.

## 🚀 START HERE: Baca File Ini Dulu!

**→ [MULAI_DARI_SINI.md](./MULAI_DARI_SINI.md)** ← **BACA INI UNTUK SETUP LENGKAP**

File ini berisi:
- Setup backend step-by-step
- Setup handphone & USB debugging
- Build & install app
- Login & test fitur
- Troubleshooting lengkap
- Tips & tricks

**Waktu:** ~30 menit setup, ~2-5 menit run ulang

---

## 📚 Dokumentasi Lainnya

### Backend Setup
```powershell
# 1. Navigate to backend
cd "AplikasiMonitoringKelasBe"

# 2. Install dependencies
composer install

# 3. Setup environment
copy .env.example .env
php artisan key:generate
php artisan migrate --seed

# 4. Start server
php artisan serve --host=0.0.0.0 --port=8000
```

### Frontend Setup - Emulator
```powershell
# 1. Open Android Studio
# 2. Open AplikasiMonitoringKelasFe folder as project
# 3. Create/start emulator
# 4. Click Run (Shift+F10)
# 5. Select emulator
# 6. App runs and you can login
```

### Frontend Setup - Physical Device via USB
```powershell
# 1. Enable USB Debugging on phone (see PANDUAN_USB_DEBUGGING.md)
# 2. Connect phone via USB cable
# 3. Run this:
cd AplikasiMonitoringKelasFe
./gradlew installDebug
adb shell am start -n com.kelasxi.aplikasimonitoringkelas/.MainActivity
```

## 📚 Documentation

### Lengkap (Indonesian)
- **[PANDUAN_USB_DEBUGGING.md](./PANDUAN_USB_DEBUGGING.md)** ← START HERE untuk physical device
  - Aktivasi USB Debugging
  - Koneksi handphone
  - Troubleshooting

- **[PENJELASAN_PRESENTASI.md](./PENJELASAN_PRESENTASI.md)**
  - Penjelasan project secara menyeluruh
  - Arsitektur sistem
  - Fitur per role

### English
- **[USB_DEBUGGING_SETUP.md](./USB_DEBUGGING_SETUP.md)**
  - USB Debugging in English
  - Step-by-step guide
  - Common issues

- **[QUICK_RUN_PHYSICAL_DEVICE.md](./QUICK_RUN_PHYSICAL_DEVICE.md)**
  - Quick reference for running on physical device
  - Network configuration
  - Troubleshooting

### Backend Documentation
- **[AplikasiMonitoringKelasBe/README.md](./AplikasiMonitoringKelasBe/README.md)** - Backend setup
- **[AplikasiMonitoringKelasBe/QUICK_START.md](./AplikasiMonitoringKelasBe/QUICK_START.md)** - Backend quick start
- **[AplikasiMonitoringKelasBe/HOW_TO_INSTALL.md](./AplikasiMonitoringKelasBe/HOW_TO_INSTALL.md)** - Detailed installation
- **[AplikasiMonitoringKelasBe/FILAMENT_SETUP.md](./AplikasiMonitoringKelasBe/FILAMENT_SETUP.md)** - Filament admin panel
- **[AplikasiMonitoringKelasBe/INTEGRATION_SUMMARY.md](./AplikasiMonitoringKelasBe/INTEGRATION_SUMMARY.md)** - API integration notes

## 🔑 Demo Accounts (Backend Seeded)

All passwords: `password123`

| Role | Email | Can Do |
|------|-------|--------|
| Siswa | siswa@sekolah.com | View schedule, check attendance |
| Kurikulum | kurikulum@sekolah.com | Dashboard, attendance report, substitute teachers |
| Admin | admin@sekolah.com | Create users, create schedules, manage system |
| Kepala Sekolah | kepala@sekolah.com | View trends, ranking, reports |

## 📦 What's Included

### Backend (Laravel 11 + Filament v3)
- ✅ Role-based authentication (4 roles)
- ✅ User management (admin can create users)
- ✅ Schedule/jadwal management
- ✅ Attendance tracking
- ✅ Substitute teacher management
- ✅ Statistics & reporting APIs
- ✅ Filament admin dashboard

### Frontend (Android Jetpack Compose)
- ✅ **Siswa App** - Student features
  - View schedule
  - Mark attendance
  - View teacher list
  - Profile

- ✅ **Kurikulum App** - Curriculum coordinator features
  - Dashboard with stats
  - Attendance tracking
  - Reports with date range
  - Substitute teacher list
  - Teacher list
  - Schedule view

- ✅ **Admin App** - Administrator features
  - Create new users (any role)
  - Create schedules
  - List existing schedules
  - Delete schedules (ready)
  - Profile

- ✅ **Kepala Sekolah App** - Principal features
  - Executive dashboard
  - Attendance trends
  - Teacher ranking/leaderboard
  - Monthly reports
  - Profile

### Shared Features
- ✅ Modular screen architecture
- ✅ Role-based routing from login
- ✅ Token-based authentication
- ✅ All data from backend APIs
- ✅ Proper error handling
- ✅ Loading states
- ✅ Material Design 3 UI
- ✅ Zero unused code
- ✅ Zero errors

## 🛠️ Technology Stack

**Backend:**
- Laravel 11
- Filament v3 (Admin Dashboard)
- MySQL database
- RESTful APIs

**Frontend:**
- Android Jetpack Compose
- Retrofit + OkHttp
- Material Design 3
- Kotlin coroutines

**DevOps:**
- Git/GitHub
- Android Gradle build system
- Composer package manager

## 📋 Running on Different Devices

### Option 1: Android Studio Emulator (Fastest)
```powershell
# 1. Open Android Studio
# 2. Create/start emulator
# 3. Open AplikasiMonitoringKelasFe folder
# 4. Click Run (Shift+F10)
```

### Option 2: Physical Device (USB)
```powershell
# See: PANDUAN_USB_DEBUGGING.md
# Or: QUICK_RUN_PHYSICAL_DEVICE.md

# Quick:
cd AplikasiMonitoringKelasFe
./gradlew installDebug
adb shell am start -n com.kelasxi.aplikasimonitoringkelas/.MainActivity
```

### Option 3: Android Studio (Device Selection Dialog)
```
1. Connect device
2. In Android Studio: Run → Run 'app'
3. Select your device
4. Click OK
```

## 🔧 Scripts Provided

### Windows Batch Files
- `run_on_device.bat` - One-click build, install, and run
- `quick_commands.bat` - Menu of common ADB commands
- `setup_adb_path.bat` - Setup ADB environment

### PowerShell Scripts
- `setup_usb_debug.ps1` - USB debugging environment setup

### Python Scripts
- `test_device_connectivity.py` - Test device↔backend connectivity

## 🐛 Troubleshooting

### App won't run on device
1. Check PANDUAN_USB_DEBUGGING.md
2. Run: `adb devices` - device should show up
3. Enable USB Debugging on phone
4. Accept RSA fingerprint prompt

### Backend unreachable
1. Ensure backend running: `php artisan serve --host=0.0.0.0`
2. Check firewall allows port 8000
3. For physical device: update IP in RetrofitClient.kt if needed
4. Test with: `adb logcat | Select-String "API"`

### Build fails
```powershell
cd AplikasiMonitoringKelasFe
./gradlew clean
./gradlew installDebug
```

### Device not detected
```powershell
adb kill-server
adb start-server
adb devices
```

See specific docs for more detailed troubleshooting.

## 📱 First Test Steps

1. **Start Backend:**
   ```powershell
   cd AplikasiMonitoringKelasBe
   php artisan serve --host=0.0.0.0 --port=8000
   ```

2. **Start App** (emulator or device)

3. **Login** with one of the demo accounts

4. **Test Features:**
   - Siswa: Check schedule, attendance
   - Kurikulum: View dashboard, reports
   - Admin: Create user, create schedule
   - Kepala Sekolah: View trends, ranking

5. **Check Backend:** Visit http://localhost:8000 in browser for Filament admin panel

## 📊 Project Stats

- **Backend:** 25 migrations, 4 seeders, 10+ API endpoints
- **Frontend:** 5 activities, 19 modular screens, 100% API integration
- **Code Quality:** Zero errors, zero unused code, clean architecture
- **Documentation:** 10+ comprehensive guides (Indonesian & English)

## 🎯 Next Steps

1. **First Time Setup:**
   - Read: PANDUAN_USB_DEBUGGING.md OR QUICK_RUN_PHYSICAL_DEVICE.md
   - Setup backend
   - Setup emulator or physical device
   - Run app and test

2. **For Development:**
   - Check API endpoints in AplikasiMonitoringKelasBe/routes/api.php
   - Modify screens in AplikasiMonitoringKelasFe/app/src/main/java/com/kelasxi/aplikasimonitoringkelas/ui/screens/
   - Add new features to backend and wire to frontend

3. **For Deployment:**
   - Build release APK: `./gradlew assembleRelease`
   - Deploy Laravel to server
   - Update API base URL
   - Test on production infrastructure

## 🤝 Support

Check the documentation files:
- PANDUAN_USB_DEBUGGING.md (Indonesian, recommended for device setup)
- PENJELASAN_PRESENTASI.md (Complete project explanation)
- Each folder has specific README.md files

## 📝 License & Notes

- Educational project for school monitoring system
- All demo data and accounts are for testing purposes
- Customize as needed for your school

---

**Last Updated:** December 6, 2025
**Status:** ✅ Production Ready (Emulator & Physical Device)
