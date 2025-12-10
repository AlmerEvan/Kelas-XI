package com.kelas.recyclerviewcardview;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog; // Menggunakan AppCompat agar kompatibel
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rcvSiswa;
    private SiswaAdapter siswaAdapter;
    private final List<Siswa> listSiswa = new ArrayList<>();
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        setupRecyclerView();
        setupButton();
    }

    private void initView() {
        rcvSiswa = findViewById(R.id.rcvSiswa);
        button = findViewById(R.id.btnTambah); // Perbaikan di sini
    }


    private void setupRecyclerView() {
        listSiswa.add(new Siswa("John Doe", "Jl. Contoh No. 1"));
        listSiswa.add(new Siswa("Jane Smith", "Jl. Anggrek No. 5"));
        listSiswa.add(new Siswa("Peter Jones", "Jl. Mawar No. 10"));
        listSiswa.add(new Siswa("Alice Brown", "Jl. Melati No. 15"));
        listSiswa.add(new Siswa("Bob White", "Jl. Dahlia No. 20"));

        siswaAdapter = new SiswaAdapter(listSiswa, this);
        rcvSiswa.setLayoutManager(new LinearLayoutManager(this));
        rcvSiswa.setAdapter(siswaAdapter);
    }

    private void setupButton() {
        button.setOnClickListener(v -> showAddSiswaDialog());
    }

    private void showAddSiswaDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Siswa");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 10);

        EditText inputNama = new EditText(this);
        inputNama.setHint("Nama Siswa");
        inputNama.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(inputNama);

        EditText inputAlamat = new EditText(this);
        inputAlamat.setHint("Alamat Siswa");
        inputAlamat.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(inputAlamat);

        builder.setView(layout);

        builder.setPositiveButton("Simpan", null);
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override tombol simpan agar bisa validasi lebih dulu
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
            String nama = inputNama.getText().toString().trim();
            String alamat = inputAlamat.getText().toString().trim();

            if (nama.isEmpty()) {
                inputNama.setError("Nama tidak boleh kosong");
                return;
            }
            if (alamat.isEmpty()) {
                inputAlamat.setError("Alamat tidak boleh kosong");
                return;
            }

            listSiswa.add(new Siswa(nama, alamat));
            siswaAdapter.notifyItemInserted(listSiswa.size() - 1);
            rcvSiswa.scrollToPosition(listSiswa.size() - 1);
            Toast.makeText(this, "Siswa ditambahkan", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
}
