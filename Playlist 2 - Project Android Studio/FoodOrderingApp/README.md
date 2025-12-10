# Food Ordering App - Android

Aplikasi mobile Android untuk pemesanan makanan dengan fitur lengkap pelanggan dan admin.

## 📋 Fitur Aplikasi

### User App:
- ✅ Authentication (Login & Register)
- ✅ Splash Screen
- ✅ Browse Menu Makanan
- ✅ Search & Filter
- ✅ Detail Produk
- ✅ Keranjang Belanja
- ✅ Checkout & Pemesanan
- ✅ Profil Pengguna
- ✅ Riwayat Pesanan

### Tech Stack:
- **Language**: Kotlin
- **Backend**: Firebase (Auth, Firestore, Storage)
- **UI**: XML Layouts + RecyclerView
- **Libraries**: 
  - Picasso (Image Loading)
  - Firebase SDK
  - Material Design

## 🚀 Struktur Project

```
FoodOrderingApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/foodorderingapp/
│   │   │   ├── activities/
│   │   │   │   ├── SplashActivity.kt
│   │   │   │   ├── LoginActivity.kt
│   │   │   │   ├── RegisterActivity.kt
│   │   │   │   ├── DetailActivity.kt
│   │   │   │   ├── CartActivity.kt
│   │   │   │   ├── CheckoutActivity.kt
│   │   │   │   ├── ProfileActivity.kt
│   │   │   │   └── OrderHistoryActivity.kt
│   │   │   ├── adapters/
│   │   │   │   ├── MenuAdapter.kt
│   │   │   │   ├── CartAdapter.kt
│   │   │   │   └── OrderAdapter.kt
│   │   │   ├── models/
│   │   │   │   ├── MenuItem.kt
│   │   │   │   ├── User.kt
│   │   │   │   ├── CartItem.kt
│   │   │   │   └── Order.kt
│   │   │   ├── utils/
│   │   │   │   └── CartManager.kt
│   │   │   └── MainActivity.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── drawable/
│   │   │   ├── values/
│   │   │   └── ...
│   │   └── AndroidManifest.xml
│   ├── google-services.json (⬅️ Tambah dari Firebase)
│   └── build.gradle.kts
├── build.gradle.kts
├── FIREBASE_SETUP.md
└── README.md
```

## 🔧 Setup & Installation

### Prerequisites:
- Android Studio (Bumblebee atau lebih baru)
- JDK 11+
- Android SDK 26+
- Firebase Account

### Langkah Install:

1. **Clone/Download Project**
   ```bash
   cd FoodOrderingApp
   ```

2. **Firebase Setup** (PENTING!)
   - Ikuti panduan di `FIREBASE_SETUP.md`
   - Download `google-services.json`
   - Letakkan di folder `app/`

3. **Sync Gradle**
   ```
   File → Sync Now
   ```

4. **Build Project**
   ```
   Build → Build Bundle(s) / APK(s)
   ```

5. **Run di Emulator/Device**
   ```
   Run → Run 'app'
   ```

## 📱 User Flow

### 1. Authentication
```
Splash Screen (3s) → Login/Register → Home
```

### 2. Browsing
```
Home (Menu List) → Detail Produk → Add to Cart
```

### 3. Ordering
```
Cart → Checkout → Konfirmasi → Pesanan Saved to Firestore
```

### 4. History
```
Profile → Order History → View Order Details
```

## 🔒 Security Notes

- Update `google-services.json` dengan project Firebase Anda
- Jangan commit `google-services.json` ke repository
- Update Firestore Security Rules sebelum production
- Gunakan HTTPS untuk images

## 📊 Database Schema

### Users Collection:
```json
{
  "uid": "user123",
  "email": "user@example.com",
  "name": "John Doe",
  "phone": "08123456789",
  "address": "Jalan Merdeka 123",
  "city": "Jakarta",
  "postalCode": "12345",
  "isAdmin": false
}
```

### Menu Collection:
```json
{
  "id": "menu123",
  "name": "Nasi Goreng",
  "description": "Nasi goreng spesial dengan telur",
  "price": 25000,
  "image": "https://...",
  "category": "Nasi",
  "available": true
}
```

### Orders Collection:
```json
{
  "orderId": "order123",
  "userId": "user123",
  "items": [...],
  "total": 75000,
  "status": "Pending",
  "deliveryAddress": "...",
  "timestamp": "2024-12-10T10:30:00"
}
```

## 🧪 Testing

### Test Scenarios:

1. **Auth Flow**
   - Register akun baru
   - Login dengan email/password
   - Logout

2. **Menu & Cart**
   - Load menu dari Firestore
   - Search & filter menu
   - Add/remove items dari cart
   - Update quantity

3. **Ordering**
   - Checkout dengan alamat
   - Save order ke Firestore
   - View order history

## 🚨 Troubleshooting

### Error: "google-services.json not found"
- Download dari Firebase Console
- Letakkan di `app/` folder
- Sync gradle

### Error: "Authentication failed"
- Check Firebase Authentication di Console
- Pastikan Email/Password sudah enabled
- Test dengan email baru

### Error: "Firestore permission denied"
- Update Security Rules
- Test rules di Firebase Console
- Clear app cache

### Error: "Image not loading"
- Check image URL di database
- Verify Firebase Storage rules
- Check network connection

## 📄 Dependencies

```gradle
// Firebase
implementation(platform("com.google.firebase:firebase-bom:32.2.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")
implementation("com.google.firebase:firebase-storage-ktx")

// UI
implementation("androidx.appcompat:appcompat:1.6.1")
implementation("androidx.recyclerview:recyclerview:1.3.2")
implementation("com.google.android.material:material:1.12.0")

// Image
implementation("com.squareup.picasso:picasso:2.8")
```

## 🎯 Future Features

- [ ] Admin Dashboard
- [ ] Real-time Order Tracking
- [ ] Payment Integration
- [ ] Wishlist
- [ ] Product Reviews & Ratings
- [ ] Push Notifications
- [ ] Multi-language Support

## 📞 Support

Untuk bantuan lebih lanjut:
1. Check `FIREBASE_SETUP.md`
2. Review Firestore rules di Console
3. Check logcat untuk error details

---

**Last Updated**: December 10, 2024
**Version**: 1.0.0 (Beta)
