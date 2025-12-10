<?php

namespace Database\Seeders;

use App\Models\TeacherAttendance;
use App\Models\Schedule;
use Illuminate\Database\Seeder;
use Carbon\Carbon;

class TeacherAttendanceSeeder extends Seeder
{
    /**
     * Run the database seeds.
     */
    public function run(): void
    {
        // Get all schedules
        $schedules = Schedule::all();
        
        // Get today's date
        $todayDate = Carbon::today();
        
        // Create 1 attendance record per schedule for today
        $schedules->each(function($schedule, $index) use ($todayDate) {
            // Check if already exists
            $exists = TeacherAttendance::where('schedule_id', $schedule->id)
                ->where('tanggal', $todayDate->format('Y-m-d'))
                ->exists();
            
            if ($exists) {
                return; // Skip
            }

            // Create varied statuses: masuk, tidak_masuk, izin
            // Pattern: 60% masuk, 20% tidak_masuk, 20% izin
            $rand = rand(1, 100);
            
            if ($rand <= 60) {
                $status = 'masuk';
                $keterangan = '-';
                $jam_masuk = Carbon::now()->setHour(7)->setMinute(30);
            } elseif ($rand <= 80) {
                $status = 'tidak_masuk';
                $keterangan = $index % 2 == 0 ? 'Sakit' : 'Urusan Pribadi';
                $jam_masuk = null;
            } else {
                $status = 'izin';
                $keterangan = $index % 3 == 0 ? 'Rapat Sekolah' : 'Acara Keluarga';
                $jam_masuk = null;
            }

            TeacherAttendance::create([
                'schedule_id'   => $schedule->id,
                'guru_id'       => $schedule->guru_id,
                'guru_asli_id'  => $schedule->guru_id,
                'tanggal'       => $todayDate,
                'jam_masuk'     => $jam_masuk,
                'status'        => $status,
                'keterangan'    => $keterangan,
            ]);
        });
        
        echo "Created " . TeacherAttendance::count() . " attendance records\n";
    }
}
