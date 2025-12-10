<?php

namespace Database\Seeders;

use App\Models\Teacher;
use App\Models\TeacherAttendance;
use App\Models\GuruPengganti;
use Illuminate\Database\Seeder;
use Carbon\Carbon;

class GuruPenggantiSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Get all attendance records with status 'tidak_masuk' or 'izin' ONLY
        $absentAttendances = TeacherAttendance::whereIn('status', ['tidak_masuk', 'izin'])
            ->with(['guruAsliTeacher', 'schedule'])
            ->orderBy('tanggal', 'asc')
            ->get();

        // Get all available teachers for substitution
        $allTeachers = Teacher::all();
        
        $count = 0;
        $maxSubstitutes = 10; // Create maximum 10 substitutes

        foreach ($absentAttendances as $absent) {
            if ($count >= $maxSubstitutes) {
                break; // Stop after creating max substitutes
            }

            // Skip if guru_asli_id doesn't exist or status is not absent
            if (!$absent->guru_asli_id || !in_array($absent->status, ['tidak_masuk', 'izin'])) {
                continue;
            }

            // Check if substitute already exists
            $existingSubstitute = GuruPengganti::where('guru_asli_id', $absent->guru_asli_id)
                ->where('tanggal', $absent->tanggal)
                ->exists();
            
            if ($existingSubstitute) {
                continue; // Skip if already has substitute
            }

            // Get a random substitute teacher (different from guru_asli_id)
            $guruPengganti = $allTeachers
                ->where('id', '!=', $absent->guru_asli_id)
                ->random();

            // Get schedule info
            $schedule = $absent->schedule;
            $guruAsli = $absent->guruAsliTeacher;

            // Validate data
            if (!$guruAsli || !$guruPengganti) {
                continue;
            }

            // Create guru pengganti record ONLY for tidak_masuk and izin
            GuruPengganti::create([
                'guru_asli_id' => $guruAsli->id,
                'guru_pengganti_id' => $guruPengganti->id,
                'kelas' => $schedule->kelas ?? 'N/A',
                'mata_pelajaran' => $schedule->mata_pelajaran ?? 'Umum',
                'tanggal' => $absent->tanggal,
                'jam_mulai' => $schedule->jam_mulai,
                'jam_selesai' => $schedule->jam_selesai,
                'ruang' => $schedule->ruang ?? 'Ruang ' . ($schedule->kelas ?? 'N/A'),
                'keterangan' => 'Otomatis menggantikan - ' . $absent->keterangan,
                'status' => 'aktif',
                'assigned_by' => 1,
            ]);
            
            $count++;
        }
        
        echo "Created {$count} guru pengganti records (hanya untuk tidak_masuk & izin)\n";
    }
}
