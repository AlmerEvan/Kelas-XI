package com.example.helloword;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Output ke Logcat
        System.out.println("onCreate dijalankan!");

        // Memanggil fungsi kustom
        kustom();
    }

    // Fungsi kustom
    public void kustom() {
        System.out.println("Fungsi kustom berhasil dipanggil!");
    }
}