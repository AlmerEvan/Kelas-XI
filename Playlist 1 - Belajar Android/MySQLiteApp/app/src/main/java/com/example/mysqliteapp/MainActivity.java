package com.example.mysqliteapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.PopupMenu;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText etBarang, etStok, etHarga, etCari;
    private Button btnSimpan, btnCari;
    private TextView tvPilihan;
    private ListView listViewBarang;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> daftarBarang;
    private String idBarangEdit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load();
    }

    private void load() {
        dbHelper = new DatabaseHelper(this);
        dbHelper.getWritableDatabase();

        etBarang = findViewById(R.id.etBarang);
        etStok = findViewById(R.id.etStok);
        etHarga = findViewById(R.id.etHarga);
        etCari = findViewById(R.id.etCari);
        btnCari = findViewById(R.id.btnCari);
        btnSimpan = findViewById(R.id.btnSimpan);
        tvPilihan = findViewById(R.id.tvPilihan);
        listViewBarang = findViewById(R.id.listViewBarang);

        daftarBarang = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, daftarBarang);
        listViewBarang.setAdapter(adapter);

        tvPilihan.setText("Form Input Barang (Mode: Insert)");

        btnSimpan.setOnClickListener(v -> simpanData());
        btnCari.setOnClickListener(v -> cariData());

        listViewBarang.setOnItemClickListener((adapterView, view, position, id) -> {
            String item = daftarBarang.get(position);
            String[] parts = item.split("\\.");
            if (parts.length > 0) {
                String idBarang = parts[0].trim();

                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getItemId() == R.id.menu_edit) {
                        editBarang(idBarang);
                        return true;
                    } else if (menuItem.getItemId() == R.id.menu_delete) {
                        konfirmasiHapus(idBarang);
                        return true;
                    }
                    return false;
                });
                popupMenu.show();
            }
        });

        tampilkanData();
    }

    private void simpanData() {
        String nama = etBarang.getText().toString().trim();
        String stokStr = etStok.getText().toString().trim();
        String hargaStr = etHarga.getText().toString().trim();

        if (nama.isEmpty() || stokStr.isEmpty() || hargaStr.isEmpty()) {
            pesan("Semua kolom harus diisi!");
            return;
        }

        double stok, harga;
        try {
            stok = Double.parseDouble(stokStr);
            harga = Double.parseDouble(hargaStr);
        } catch (NumberFormatException e) {
            pesan("Stok dan harga harus berupa angka");
            return;
        }

        if (idBarangEdit == null) {
            String sql = "INSERT INTO " + DatabaseHelper.TABLE_BARANG + " (" +
                    DatabaseHelper.COL_NAMA_BARANG + ", " +
                    DatabaseHelper.COL_STOK + ", " +
                    DatabaseHelper.COL_HARGA + ") VALUES ('" +
                    nama + "', " + stok + ", " + harga + ");";
            dbHelper.runSQL(sql);
            pesan("Data berhasil disimpan");
        } else {
            String sql = "UPDATE " + DatabaseHelper.TABLE_BARANG + " SET " +
                    DatabaseHelper.COL_NAMA_BARANG + " = '" + nama + "', " +
                    DatabaseHelper.COL_STOK + " = " + stok + ", " +
                    DatabaseHelper.COL_HARGA + " = " + harga +
                    " WHERE " + DatabaseHelper.COL_ID_BARANG + " = " + idBarangEdit;
            dbHelper.runSQL(sql);
            pesan("Data berhasil diupdate");
        }

        resetForm();
        tampilkanData();
    }

    private void tampilkanData() {
        daftarBarang.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_BARANG +
                " ORDER BY " + DatabaseHelper.COL_ID_BARANG + " DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_BARANG));
            String nama = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAMA_BARANG));
            double stok = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STOK));
            double harga = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HARGA));

            daftarBarang.add(id + ". " + nama + " | Stok: " + stok + " | Harga: Rp" + harga);
        }

        adapter.notifyDataSetChanged();
        cursor.close();
    }

    private void cariData() {
        String keyword = etCari.getText().toString().trim();
        if (keyword.isEmpty()) {
            pesan("Masukkan kata kunci untuk mencari");
            return;
        }

        daftarBarang.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_BARANG +
                " WHERE " + DatabaseHelper.COL_NAMA_BARANG + " LIKE '%" + keyword + "%'" +
                " ORDER BY " + DatabaseHelper.COL_ID_BARANG + " DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID_BARANG));
            String nama = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAMA_BARANG));
            double stok = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STOK));
            double harga = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HARGA));

            daftarBarang.add(id + ". " + nama + " | Stok: " + stok + " | Harga: Rp" + harga);
        }

        if (daftarBarang.isEmpty()) {
            daftarBarang.add("Data tidak ditemukan.");
        }

        adapter.notifyDataSetChanged();
        cursor.close();
    }

    private void editBarang(String idBarang) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_BARANG +
                " WHERE " + DatabaseHelper.COL_ID_BARANG + " = " + idBarang, null);

        if (cursor.moveToFirst()) {
            etBarang.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAMA_BARANG)));
            etStok.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_STOK))));
            etHarga.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_HARGA))));
            idBarangEdit = idBarang;
            btnSimpan.setText("UPDATE");
            tvPilihan.setText("Form Edit Barang ID: " + idBarang);
        }
        cursor.close();
    }

    private void konfirmasiHapus(String idBarang) {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    hapusBarang(idBarang);
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void hapusBarang(String idBarang) {
        String sql = "DELETE FROM " + DatabaseHelper.TABLE_BARANG +
                " WHERE " + DatabaseHelper.COL_ID_BARANG + " = " + idBarang;
        dbHelper.runSQL(sql);
        pesan("Data berhasil dihapus");
        tampilkanData();
    }

    private void resetForm() {
        etBarang.setText("");
        etStok.setText("");
        etHarga.setText("");
        etBarang.requestFocus();
        btnSimpan.setText("SIMPAN");
        tvPilihan.setText("Form Input Barang (Mode: Insert)");
        idBarangEdit = null;
    }

    private void pesan(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
