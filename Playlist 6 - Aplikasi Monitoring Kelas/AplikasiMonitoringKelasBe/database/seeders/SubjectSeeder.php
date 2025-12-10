<?php

namespace Database\Seeders;

use App\Models\Subject;
use Illuminate\Database\Seeder;

class SubjectSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Create subjects
        Subject::create([
            'nama' => 'Bahasa Daerah',
            'kode' => 'bahasa-daerah',
        ]);

        Subject::create([
            'nama' => 'Bahasa Indonesia',
            'kode' => 'bahasa-indonesia',
        ]);

        Subject::create([
            'nama' => 'Matematika',
            'kode' => 'matematika',
        ]);

        Subject::create([
            'nama' => 'IPA',
            'kode' => 'ipa',
        ]);

        Subject::create([
            'nama' => 'IPS',
            'kode' => 'ips',
        ]);

        Subject::create([
            'nama' => 'PPKn',
            'kode' => 'ppkn',
        ]);

        Subject::create([
            'nama' => 'Bahasa Inggris',
            'kode' => 'bahasa-inggris',
        ]);

        Subject::create([
            'nama' => 'PJOK',
            'kode' => 'pjok',
        ]);

        Subject::create([
            'nama' => 'Seni Budaya',
            'kode' => 'seni-budaya',
        ]);

        Subject::create([
            'nama' => 'Prakarya',
            'kode' => 'prakarya',
        ]);

        Subject::create([
            'nama' => 'Pendidikan Agama',
            'kode' => 'pendidikan-agama',
        ]);

        Subject::create([
            'nama' => 'Bimbingan Konseling',
            'kode' => 'bk',
        ]);

        Subject::create([
            'nama' => 'Informatika',
            'kode' => 'informatika',
        ]);

        Subject::create([
            'nama' => 'TIK',
            'kode' => 'tik',
        ]);
    }
}
