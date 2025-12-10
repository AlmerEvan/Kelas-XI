<?php

namespace Database\Seeders;

use App\Models\Teacher;
use App\Models\User;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class TeacherSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Define teachers data (only 8 teachers matching those used in schedules)
        $teachers = [
            ['name' => 'Pak Heru', 'email' => 'guru1@sekolah.com', 'mata_pelajaran' => 'Bahasa Daerah'],
            ['name' => 'Bu Susi', 'email' => 'guru2@sekolah.com', 'mata_pelajaran' => 'Bahasa Indonesia'],
            ['name' => 'Pak Budi', 'email' => 'guru3@sekolah.com', 'mata_pelajaran' => 'Matematika'],
            ['name' => 'Bu Rina', 'email' => 'guru4@sekolah.com', 'mata_pelajaran' => 'IPA'],
            ['name' => 'Pak Joko', 'email' => 'guru5@sekolah.com', 'mata_pelajaran' => 'IPS'],
            ['name' => 'Ibu Ratna', 'email' => 'guru6@sekolah.com', 'mata_pelajaran' => 'PPKn'],
            ['name' => 'Bu Maya', 'email' => 'guru7@sekolah.com', 'mata_pelajaran' => 'Bahasa Inggris'],
            ['name' => 'Pak Anton', 'email' => 'guru8@sekolah.com', 'mata_pelajaran' => 'PJOK'],
        ];

        foreach ($teachers as $teacherData) {
            // Create user in users table (for authentication & attendance references)
            User::create([
                'name' => $teacherData['name'],
                'email' => $teacherData['email'],
                'password' => Hash::make('password123'),
                'role' => 'guru',
                'email_verified_at' => now(),
                'mata_pelajaran' => $teacherData['mata_pelajaran'],
            ]);

            // Create teacher in teachers table (for new system)
            Teacher::create([
                'name' => $teacherData['name'],
                'email' => $teacherData['email'],
                'password' => Hash::make('password123'),
                'mata_pelajaran' => $teacherData['mata_pelajaran'],
            ]);
        }
    }
}
