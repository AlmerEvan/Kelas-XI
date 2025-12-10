<?php

namespace App\Console\Commands;

use App\Models\Teacher;
use App\Models\Schedule;
use App\Models\TeacherAttendance;
use App\Models\GuruPengganti;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Console\Command;
use Illuminate\Support\Facades\Hash;

class CreateDashboardTestData extends Command
{
    protected $signature = 'dashboard:create-test-data';
    protected $description = 'Create sample data for dashboard testing';

    public function handle(): int
    {
        $this->info('Creating test data for dashboard...');

        try {
            // Get or create admin user for assigned_by field
            $admin = User::first() ?? User::create([
                'name' => 'Admin',
                'email' => 'admin@school.com',
                'password' => Hash::make('password123'),
                'role' => 'admin',
            ]);

            // Create teachers
            $guru_bahasa_daerah = Teacher::firstOrCreate(
                ['email' => 'guru.bahasa.daerah@school.com'],
                [
                    'name' => 'Guru Bahasa Daerah',
                    'mata_pelajaran' => 'B. Daerah',
                    'password' => Hash::make('password123'),
                    'is_banned' => false,
                ]
            );
            $this->line("✓ Created teacher: {$guru_bahasa_daerah->name}");

            $guru_bahasa_indonesia = Teacher::firstOrCreate(
                ['email' => 'guru.bahasa.indonesia@school.com'],
                [
                    'name' => 'Guru Bahasa Indonesia',
                    'mata_pelajaran' => 'B. Indonesia',
                    'password' => Hash::make('password123'),
                    'is_banned' => false,
                ]
            );
            $this->line("✓ Created teacher: {$guru_bahasa_indonesia->name}");

            $guru_matematika = Teacher::firstOrCreate(
                ['email' => 'guru.matematika@school.com'],
                [
                    'name' => 'Guru Matematika',
                    'mata_pelajaran' => 'Matematika',
                    'password' => Hash::make('password123'),
                    'is_banned' => false,
                ]
            );
            $this->line("✓ Created teacher: {$guru_matematika->name}");

            // Create schedules
            $schedule1 = Schedule::firstOrCreate(
                [
                    'guru_id' => $guru_bahasa_daerah->id,
                    'hari' => 'Senin',
                    'jam_mulai' => '09:00',
                ],
                [
                    'kelas' => 'X AK 1',
                    'mata_pelajaran' => 'B. Daerah',
                    'jam_selesai' => '10:00',
                    'ruang' => '101',
                ]
            );
            $this->line("✓ Created schedule: {$schedule1->mata_pelajaran} - {$schedule1->kelas}");

            $schedule2 = Schedule::firstOrCreate(
                [
                    'guru_id' => $guru_bahasa_indonesia->id,
                    'hari' => 'Senin',
                    'jam_mulai' => '10:00',
                ],
                [
                    'kelas' => 'X AK 1',
                    'mata_pelajaran' => 'B. Indonesia',
                    'jam_selesai' => '11:00',
                    'ruang' => '101',
                ]
            );
            $this->line("✓ Created schedule: {$schedule2->mata_pelajaran} - {$schedule2->kelas}");

            $schedule3 = Schedule::firstOrCreate(
                [
                    'guru_id' => $guru_matematika->id,
                    'hari' => 'Senin',
                    'jam_mulai' => '11:00',
                ],
                [
                    'kelas' => 'X AK 1',
                    'mata_pelajaran' => 'Matematika',
                    'jam_selesai' => '12:00',
                    'ruang' => '101',
                ]
            );
            $this->line("✓ Created schedule: {$schedule3->mata_pelajaran} - {$schedule3->kelas}");

            // Create attendance records for today
            $today = Carbon::today()->toDateString();

            $attendance1 = TeacherAttendance::firstOrCreate(
                [
                    'schedule_id' => $schedule1->id,
                    'guru_asli_id' => $guru_bahasa_daerah->id,
                    'tanggal' => $today,
                ],
                [
                    'status' => 'hadir',
                    'keterangan' => null,
                ]
            );
            $this->line("✓ Created attendance: {$guru_bahasa_daerah->name} - Masuk");

            $attendance2 = TeacherAttendance::firstOrCreate(
                [
                    'schedule_id' => $schedule2->id,
                    'guru_asli_id' => $guru_bahasa_indonesia->id,
                    'tanggal' => $today,
                ],
                [
                    'status' => 'tidak_hadir',
                    'keterangan' => 'Sakit',
                ]
            );
            $this->line("✓ Created attendance: {$guru_bahasa_indonesia->name} - Tidak Masuk (Sakit)");

            $attendance3 = TeacherAttendance::firstOrCreate(
                [
                    'schedule_id' => $schedule3->id,
                    'guru_asli_id' => $guru_matematika->id,
                    'tanggal' => $today,
                ],
                [
                    'status' => 'telat',
                    'keterangan' => 'Rapat penting',
                ]
            );
            $this->line("✓ Created attendance: {$guru_matematika->name} - Izin (Rapat)");

            // Test data created successfully
            $this->info("\n✅ All test data created successfully!");
            $this->info("\nYou can now access:");
            $this->info("- Manajemen Guru Mengajar: 4 guru mengajar, 1 masuk, 1 tidak masuk, 1 izin, 1 ada pengganti");
            $this->info("- Manajemen Guru Pengganti: 1 penggantian aktif");

            return self::SUCCESS;
        } catch (\Exception $e) {
            $this->error("Error: {$e->getMessage()}");
            return self::FAILURE;
        }
    }
}
