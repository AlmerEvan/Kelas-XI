package com.example.mysqliteapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nama database dan versi
    private static final String DATABASE_NAME = "dbtoko.db";
    private static final int DATABASE_VERSION = 1;

    // Nama tabel dan kolom
    public static final String TABLE_BARANG = "tblbarang";
    public static final String COL_ID_BARANG = "idbarang";
    public static final String COL_NAMA_BARANG = "barang";
    public static final String COL_STOK = "stok";
    public static final String COL_HARGA = "harga";

    // SQL untuk membuat tabel
    private static final String CREATE_TABLE_BARANG = "CREATE TABLE " + TABLE_BARANG + " (" +
            COL_ID_BARANG + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NAMA_BARANG + " TEXT, " +
            COL_STOK + " REAL, " +
            COL_HARGA + " REAL" +
            ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("DatabaseHelper", "Constructor dipanggil");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("DatabaseHelper", "onCreate dipanggil");
        db.execSQL(CREATE_TABLE_BARANG);
        Log.d("DatabaseHelper", "Tabel tblbarang berhasil dibuat");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "onUpgrade dipanggil");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BARANG);
        onCreate(db);
    }

    // Menjalankan perintah SQL manual (INSERT, UPDATE, DELETE)
    public void runSQL(String sql) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(sql);
            Log.d("DatabaseHelper", "SQL berhasil dijalankan: " + sql);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error menjalankan SQL: " + sql, e);
        } finally {
            db.close();
        }
    }

    public List<String> getAllBarang() {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BARANG, null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COL_ID_BARANG));
                String nama = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAMA_BARANG));
                double stok = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_STOK));
                double harga = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_HARGA));

                list.add(id + ". " + nama + " - Stok: " + stok + ", Harga: " + harga);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return list;
    }

    public List<String> searchBarang(String keyword) {
        List<String> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BARANG +
                " WHERE " + COL_NAMA_BARANG + " LIKE '%" + keyword + "%'" +
                " ORDER BY " + COL_ID_BARANG + " DESC";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_BARANG));
            String nama = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAMA_BARANG));
            double stok = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_STOK));
            double harga = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_HARGA));
            list.add(id + ". " + nama + " - Stok: " + stok + " - Harga: " + harga);
        }
        cursor.close();
        db.close();
        return list;
    }

    public void deleteById(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_BARANG, COL_ID_BARANG + "=?", new String[]{String.valueOf(id)});
        db.close();
    }


}
