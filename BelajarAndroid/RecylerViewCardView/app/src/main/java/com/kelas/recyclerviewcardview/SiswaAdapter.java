package com.kelas.recyclerviewcardview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SiswaAdapter extends RecyclerView.Adapter<SiswaAdapter.ViewHolder> {

    private final Context context;
    private final List<Siswa> listSiswa;

    public SiswaAdapter(List<Siswa> listSiswa, Context context) {
        this.listSiswa = listSiswa;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.items_siswa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Siswa siswa = listSiswa.get(position);
        holder.tvNama.setText(siswa.getNama());
        holder.tvAlamat.setText(siswa.getAlamat());

        // Klik item -> tampilkan detail dalam Toast
        holder.itemView.setOnClickListener(v -> {
            String message = "Nama: " + siswa.getNama() + "\nAlamat: " + siswa.getAlamat();
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        });

        // Klik tombol menu ⋮
        holder.tvMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.tvMenu);
            popupMenu.inflate(R.menu.menu_option); // pastikan file menu_option.xml ada di res/menu

            popupMenu.setOnMenuItemClickListener(item -> {
                int currentPosition = holder.getAdapterPosition();

                if (currentPosition != RecyclerView.NO_POSITION) {
                    if (item.getItemId() == R.id.menu_simpan) {
                        Toast.makeText(context, "Menyimpan data " + siswa.getNama(), Toast.LENGTH_SHORT).show();
                        return true;
                    } else if (item.getItemId() == R.id.menu_hapus) {
                        listSiswa.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        Toast.makeText(context, "Menghapus data " + siswa.getNama(), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }

                return false;
            });

            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return listSiswa.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvAlamat, tvMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvMenu = itemView.findViewById(R.id.tvMenu);
        }
    }
}
