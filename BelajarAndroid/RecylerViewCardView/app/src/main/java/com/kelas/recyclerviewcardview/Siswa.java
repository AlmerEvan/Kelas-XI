package com.kelas.recyclerviewcardview;

public class Siswa {
    private String nama;
    private String alamat;

    // Constructor
    public Siswa(String nama, String alamat) {
        this.nama = nama;
        this.alamat = alamat;
    }

    // Getter
    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    // Setter (opsional, bisa dihapus kalau tidak digunakan)
    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    // Optional: untuk debug/log
    @Override
    public String toString() {
        return "Siswa{" +
                "nama='" + nama + '\'' +
                ", alamat='" + alamat + '\'' +
                '}';
    }
}
