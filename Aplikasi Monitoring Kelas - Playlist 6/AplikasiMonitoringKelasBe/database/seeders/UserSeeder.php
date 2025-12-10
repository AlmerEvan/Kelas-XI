<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class UserSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Admin User
        User::create([
            'name' => 'Admin Sekolah',
            'email' => 'admin@sekolah.com',
            'password' => Hash::make('password123'),
            'role' => 'admin',
            'email_verified_at' => now(),
        ]);

        // Kepala Sekolah
        User::create([
            'name' => 'Kepala Sekolah',
            'email' => 'kepala@sekolah.com',
            'password' => Hash::make('password123'),
            'role' => 'kepala_sekolah',
            'email_verified_at' => now(),
        ]);

        // Kurikulum (Guru Kurikulum)
        User::create([
            'name' => 'Guru Kurikulum',
            'email' => 'kurikulum@sekolah.com',
            'password' => Hash::make('password123'),
            'role' => 'kurikulum',
            'email_verified_at' => now(),
        ]);

        // Student
        User::create([
            'name' => 'Siswa Dummy',
            'email' => 'siswa@sekolah.com',
            'password' => Hash::make('password123'),
            'role' => 'siswa',
            'email_verified_at' => now(),
        ]);
    }
}
