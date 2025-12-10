package com.example.mycalculatorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvHasil;
    private EditText etBil_1, etBil_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        load();
    }

    private void load() {
        tvHasil = findViewById(R.id.tvHasil);
        etBil_1 = findViewById(R.id.etBil_1);
        etBil_2 = findViewById(R.id.etBil_2);
    }

    public void onClickJumlah(View view) {
        hitung("+");
    }

    public void onClickKurang(View view) {
        hitung("-");
    }

    public void onClickKali(View view) {
        hitung("*");
    }

    public void onClickBagi(View view) {
        hitung("/");
    }

    private void hitung(String operasi) {
        String input1 = etBil_1.getText().toString();
        String input2 = etBil_2.getText().toString();

        if (input1.equals("") || input2.equals("")) {
            Toast.makeText(this, "Ada Bilangan Yang Kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        double bil1 = Double.parseDouble(input1);
        double bil2 = Double.parseDouble(input2);
        double hasil = 0;

        switch (operasi) {
            case "+":
                hasil = bil1 + bil2;
                break;
            case "-":
                hasil = bil1 - bil2;
                break;
            case "*":
                hasil = bil1 * bil2;
                break;
            case "/":
                if (bil2 == 0) {
                    Toast.makeText(this, "Tidak bisa dibagi 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                hasil = bil1 / bil2;
                break;
        }

        // String.valueOf digunakan agar hasil double bisa diubah ke String untuk ditampilkan di TextView
        tvHasil.setText(String.valueOf(hasil));
    }
}