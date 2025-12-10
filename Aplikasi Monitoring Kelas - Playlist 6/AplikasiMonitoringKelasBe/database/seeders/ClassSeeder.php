<?php

namespace Database\Seeders;

use App\Models\Kelas;
use Illuminate\Database\Seeder;

class ClassSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create classes
        Kelas::create([
            'nama_kelas' => '7A',
            'kode_kelas' => '7a',
        ]);

        Kelas::create([
            'nama_kelas' => '7B',
            'kode_kelas' => '7b',
        ]);

        Kelas::create([
            'nama_kelas' => '7C',
            'kode_kelas' => '7c',
        ]);

        Kelas::create([
            'nama_kelas' => '8A',
            'kode_kelas' => '8a',
        ]);

        Kelas::create([
            'nama_kelas' => '8B',
            'kode_kelas' => '8b',
        ]);

        Kelas::create([
            'nama_kelas' => '8C',
            'kode_kelas' => '8c',
        ]);

        Kelas::create([
            'nama_kelas' => '9A',
            'kode_kelas' => '9a',
        ]);

        Kelas::create([
            'nama_kelas' => '9B',
            'kode_kelas' => '9b',
        ]);

        Kelas::create([
            'nama_kelas' => '9C',
            'kode_kelas' => '9c',
        ]);
    }
}
