# 🔥 Firebase Setup Guide untuk Blog App

## Langkah 1: Buat Firebase Project

### A. Buka Firebase Console
1. Pergi ke [Firebase Console](https://console.firebase.google.com/)
2. Login dengan akun Google Anda
3. Klik tombol **"Add Project"** atau **"Create a project"**

### B. Isi Detail Project
1. **Project Name**: Masukkan nama project (misal: "BlogApp" atau "MyBlogApp")
2. **Project ID**: akan terbentuk otomatis berdasarkan nama project
3. Uncheck "Enable Google Analytics for this project" (opsional untuk development)
4. Klik **"Create Project"**
5. Tunggu proses pembuatan project selesai (biasanya 1-2 menit)

## Langkah 2: Setup Authentication (Email & Password)

### A. Aktifkan Email/Password Authentication
1. Di Firebase Console, pilih project Anda
2. Klik menu **"Authentication"** di sidebar kiri
3. Klik tab **"Sign-in method"**
4. Cari dan klik **"Email/Password"**
5. Toggle switch menjadi **"Enable"** (berwarna biru)
6. Klik **"Save"**

## Langkah 3: Buat Firestore Database

### A. Buat Firestore Database
1. Klik menu **"Firestore Database"** di sidebar kiri
2. Klik tombol **"Create database"**
3. Pilih region terdekat dengan user (misal: asia-southeast1 untuk Indonesia)
4. Pilih **"Start in test mode"** untuk development
   - ⚠️ Catatan: Test mode TIDAK secure untuk production
5. Klik **"Create"**

### B. Setup Firestore Security Rules (untuk production)
1. Setelah database dibuat, klik tab **"Rules"**
2. Ganti rules dengan yang lebih secure:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection
    match /users/{userId} {
      allow read, write: if request.auth.uid == userId;
      match /saved_blogs/{document=**} {
        allow read, write: if request.auth.uid == userId;
      }
    }
    
    // Blogs collection
    match /blogs/{blogId} {
      allow read: if true;
      allow write: if request.auth != null;
      allow update: if request.auth != null;
    }
  }
}
```

3. Klik **"Publish"**

### C. Buat Collections di Firestore
Struktur database yang dibutuhkan:

**Collection 1: `users`**
- Document ID: `{uid}` (User ID dari Authentication)
- Fields:
  ```
  {
    "uid": "user_id_di_auth",
    "name": "Nama User",
    "email": "email@example.com"
  }
  ```

**Collection 2: `blogs`**
- Document ID: Auto-generated atau custom ID
- Fields:
  ```
  {
    "id": "blog_id",
    "title": "Judul Blog",
    "content": "Konten blog...",
    "authorId": "uid_author",
    "likes": 0,
    "isSaved": false,
    "createdAt": 1702000000000
  }
  ```

**Sub-collection: `users/{userId}/saved_blogs`**
- Document ID: `{blogId}`
- Fields:
  ```
  {
    "savedAt": 1702000000000
  }
  ```

## Langkah 4: Download & Setup google-services.json

### A. Download google-services.json
1. Di Firebase Console, klik ⚙️ (Settings) di sebelah project name
2. Klik **"Project settings"**
3. Pilih tab **"Your apps"**
4. Klik pada app Android Anda (jika belum ada, lihat Langkah 5)
5. Scroll ke bawah, klik **"google-services.json"**
6. File akan ter-download

### B. Letakkan di Project Android
1. Salin file `google-services.json` ke folder: `app/`
2. Path lengkap: `BlogApp/app/google-services.json`
3. File ini SUDAH ada di project dengan struktur template

## Langkah 5: Daftarkan App Android ke Firebase (jika belum ada)

### A. Buka Project Settings
1. Di Firebase Console, klik ⚙️ (Settings) → "Project settings"
2. Klik tab **"Your apps"**

### B. Register Android App
1. Klik tombol **"+ Add app"**
2. Pilih **"Android"**
3. Isi form:
   - **Android package name**: `com.example.blogapp`
   - **App nickname** (opsional): `BlogApp`
   - **SHA-1 certificate hash** (opsional untuk development, wajib untuk production)

### C. Dapatkan SHA-1 Certificate Hash (Opsional)
Untuk mendapatkan SHA-1 hash Anda:

**Di Windows (PowerShell):**
```powershell
keytool -list -v -keystore %USERPROFILE%\.android\debug.keystore -alias androiddebugkey -storepass android -keypass android
```

**Di macOS/Linux:**
```bash
keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Cari baris `SHA1:` dan copy-paste ke form Firebase.

4. Klik **"Register app"**
5. Download `google-services.json`
6. Klik **"Next"** hingga selesai

## Langkah 6: Verifikasi Setup

### A. Build & Run Project
1. Buka Android Studio
2. Klik **"Sync Now"** jika ada prompt
3. Build project: `Build` → `Make Project`
4. Jalankan app di emulator atau device

### B. Test Authentication
1. Klik tombol **"Register"**
2. Isi form:
   - Name: "Test User"
   - Email: "test@example.com"
   - Password: "123456"
   - Confirm Password: "123456"
3. Klik "Register"
4. Jika berhasil, Anda akan di-redirect ke Home page

### C. Verifikasi di Firebase Console
1. Buka Firebase Console
2. Klik **"Authentication"** → Tab **"Users"**
3. Seharusnya ada user baru yang terdaftar dengan email `test@example.com`

## Langkah 7: Test Firestore

### A. Publish Blog
1. Di app, klik **"Add Blog"**
2. Isi:
   - Title: "Test Blog"
   - Content: "This is my first blog"
3. Klik **"Publish"**
4. Jika berhasil, toast akan muncul "Blog published successfully"

### B. Verifikasi di Firestore
1. Buka Firebase Console
2. Klik **"Firestore Database"**
3. Seharusnya ada:
   - Collection `blogs` dengan document berisi blog yang baru dipublish
   - Collection `users` dengan document user yang terdaftar

## Troubleshooting

### ❌ Error: "Calls to CloudFirestore.getInstance() fail"
**Solusi:**
- Pastikan `google-services.json` sudah di folder `app/`
- Sync gradle: File → Sync with Gradle Files
- Rebuild project

### ❌ Error: "Permission denied for blogs collection"
**Solusi:**
- Buka Firestore Rules (seperti di Langkah 3.B)
- Pastikan rules sudah di-update dengan yang benar
- Klik "Publish"

### ❌ Error: "Authentication failed"
**Solusi:**
- Pastikan Email/Password authentication sudah di-enable (Langkah 2)
- Pastikan email belum terdaftar sebelumnya
- Cek password minimal 6 karakter

### ❌ Saved Blogs tidak muncul
**Solusi:**
- Pastikan user sudah like/save blog
- Refresh app atau buka fragment lagi
- Cek di Firestore apakah sub-collection `saved_blogs` ada di user

## Fitur-Fitur yang Sudah Diimplementasikan ✅

- ✅ Firebase Authentication (Register & Login)
- ✅ Firestore Database (CRUD blogs)
- ✅ MVVM Architecture (ViewModel + LiveData)
- ✅ RecyclerView dengan CardView
- ✅ Like functionality (update likes di Firestore)
- ✅ Save/Bookmark blogs
- ✅ Navigation Component
- ✅ Fragment-based UI
- ✅ Error handling & Loading state

## Struktur Project

```
BlogApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/blogapp/
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Blog.kt
│   │   │   │   │   └── User.kt
│   │   │   │   └── repository/
│   │   │   │       ├── AuthRepository.kt
│   │   │   │       └── BlogRepository.kt
│   │   │   └── ui/
│   │   │       ├── activity/
│   │   │       │   ├── WelcomeActivity.kt
│   │   │       │   └── MainActivity.kt
│   │   │       ├── fragment/
│   │   │       │   ├── LoginFragment.kt
│   │   │       │   ├── RegisterFragment.kt
│   │   │       │   ├── HomeFragment.kt
│   │   │       │   ├── AddBlogFragment.kt
│   │   │       │   └── SavedFragment.kt
│   │   │       ├── adapter/
│   │   │       │   └── BlogAdapter.kt
│   │   │       ├── viewmodel/
│   │   │       │   ├── AuthViewModel.kt
│   │   │       │   └── BlogViewModel.kt
│   │   │       └── theme/
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_welcome.xml
│   │   │   │   ├── activity_main.xml
│   │   │   │   ├── fragment_login.xml
│   │   │   │   ├── fragment_register.xml
│   │   │   │   ├── fragment_home.xml
│   │   │   │   ├── fragment_add_blog.xml
│   │   │   │   ├── fragment_saved.xml
│   │   │   │   └── item_blog.xml
│   │   │   ├── navigation/
│   │   │   │   └── nav_graph.xml
│   │   │   ├── drawable/
│   │   │   │   └── rounded_edit_text.xml
│   │   │   └── values/
│   │   │       └── strings.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── google-services.json (tambahkan setelah download)
├── gradle/
│   └── libs.versions.toml
├── settings.gradle.kts
└── build.gradle.kts
```

## Next Steps 🚀

1. ✅ Setup Firebase Project
2. ✅ Konfigurasi Authentication
3. ✅ Setup Firestore Database
4. ✅ Download google-services.json
5. ✅ Jalankan app dan test semua fitur
6. 🔄 (Opsional) Tambahkan User Profile
7. 🔄 (Opsional) Tambahkan Comment system
8. 🔄 (Opsional) Tambahkan Real-time Notifications

---
**Selamat! App Anda sudah siap dengan Firebase! 🎉**
