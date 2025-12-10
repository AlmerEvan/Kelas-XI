<?php

namespace Database\Seeders;

use App\Models\Schedule;
use Illuminate\Database\Seeder;
use Carbon\Carbon;

class ScheduleSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Days: Monday to Friday
        $days = [
            'Monday' => 'Senin',
            'Tuesday' => 'Selasa',
            'Wednesday' => 'Rabu',
            'Thursday' => 'Kamis',
            'Friday' => 'Jumat',
        ];

        foreach ($days as $eng => $hari) {

            // 1. Bahasa Daerah – Pak Heru – Kelas 7A
            Schedule::create([
                'guru_id' => 1,
                'mata_pelajaran' => 'Bahasa Daerah',
                'kelas' => '7A',
                'hari' => $hari,
                'jam_mulai' => '08:00',
                'jam_selesai' => '09:00',
                'ruang' => 'Ruang 101',
            ]);

            // 2. Bahasa Indonesia – Bu Susi – Kelas 7B
            Schedule::create([
                'guru_id' => 2,
                'mata_pelajaran' => 'Bahasa Indonesia',
                'kelas' => '7B',
                'hari' => $hari,
                'jam_mulai' => '09:00',
                'jam_selesai' => '10:00',
                'ruang' => 'Ruang 102',
            ]);

            // 3. Matematika – Pak Budi – Kelas 7C
            Schedule::create([
                'guru_id' => 3,
                'mata_pelajaran' => 'Matematika',
                'kelas' => '7C',
                'hari' => $hari,
                'jam_mulai' => '10:00',
                'jam_selesai' => '11:00',
                'ruang' => 'Ruang 201',
            ]);

            // 4. IPA – Bu Rina – Kelas 8A
            Schedule::create([
                'guru_id' => 4,
                'mata_pelajaran' => 'IPA',
                'kelas' => '8A',
                'hari' => $hari,
                'jam_mulai' => '11:00',
                'jam_selesai' => '12:00',
                'ruang' => 'Ruang 203',
            ]);

            // 5. IPS – Pak Joko – Kelas 8B
            Schedule::create([
                'guru_id' => 5,
                'mata_pelajaran' => 'IPS',
                'kelas' => '8B',
                'hari' => $hari,
                'jam_mulai' => '13:00',
                'jam_selesai' => '14:00',
                'ruang' => 'Ruang 104',
            ]);

            // 6. PPKn – Ibu Ratna – Kelas 8C
            Schedule::create([
                'guru_id' => 6,
                'mata_pelajaran' => 'PPKn',
                'kelas' => '8C',
                'hari' => $hari,
                'jam_mulai' => '14:00',
                'jam_selesai' => '15:00',
                'ruang' => 'Ruang 105',
            ]);

            // 7. Bahasa Inggris – Bu Maya – Kelas 9A
            Schedule::create([
                'guru_id' => 7,
                'mata_pelajaran' => 'Bahasa Inggris',
                'kelas' => '9A',
                'hari' => $hari,
                'jam_mulai' => '15:00',
                'jam_selesai' => '16:00',
                'ruang' => 'Ruang 106',
            ]);

            // 8. PJOK – Pak Anton – Kelas 9B
            Schedule::create([
                'guru_id' => 8,
                'mata_pelajaran' => 'PJOK',
                'kelas' => '9B',
                'hari' => $hari,
                'jam_mulai' => '16:00',
                'jam_selesai' => '17:00',
                'ruang' => 'Lapangan',
            ]);
        }
    }
}
