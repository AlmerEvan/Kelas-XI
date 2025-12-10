<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\DB;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // Disable foreign key checks
        DB::statement('SET FOREIGN_KEY_CHECKS=0');

        // Truncate all tables
        DB::table('teacher_attendances')->truncate();
        DB::table('guru_pengganti')->truncate();
        DB::table('schedules')->truncate();
        DB::table('subjects')->truncate();
        DB::table('classes')->truncate();
        DB::table('teachers')->truncate();
        DB::table('users')->truncate();

        // Re-enable foreign key checks
        DB::statement('SET FOREIGN_KEY_CHECKS=1');

        // Run seeders in order
        $this->call([
            UserSeeder::class,
            TeacherSeeder::class,
            ClassSeeder::class,
            SubjectSeeder::class,
            ScheduleSeeder::class,
            TeacherAttendanceSeeder::class,
            GuruPenggantiSeeder::class,
        ]);
    }
}


