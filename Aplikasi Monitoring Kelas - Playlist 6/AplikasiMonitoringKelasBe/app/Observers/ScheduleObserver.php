<?php

namespace App\Observers;

use App\Models\Schedule;
use App\Models\TeacherAttendance;
use Carbon\Carbon;

class ScheduleObserver
{
    /**
     * Handle the Schedule "created" event.
     */
    public function created(Schedule $schedule): void
    {
        // Map Indonesian day names to English
        $dayNameMap = [
            'Senin' => 'Monday',
            'Selasa' => 'Tuesday',
            'Rabu' => 'Wednesday',
            'Kamis' => 'Thursday',
            'Jumat' => 'Friday',
            'Sabtu' => 'Saturday',
            'Minggu' => 'Sunday',
        ];

        // Get current week's Monday
        $baseDate = Carbon::now()->startOfWeek();

        // Create attendance for this schedule for all matching days in current week
        for ($i = 0; $i < 7; $i++) {
            $currentDate = $baseDate->copy()->addDays($i);
            $currentDayName = $currentDate->englishDayOfWeek;
            
            // Check if this schedule is for this day
            $dayName = $dayNameMap[$schedule->hari] ?? $schedule->hari;
            
            if ($currentDayName === $dayName) {
                // Check if attendance already exists
                $exists = TeacherAttendance::where('schedule_id', $schedule->id)
                    ->where('tanggal', $currentDate->format('Y-m-d'))
                    ->exists();
                
                if (!$exists) {
                    // Create attendance record with default status "masuk"
                    TeacherAttendance::create([
                        'schedule_id'   => $schedule->id,
                        'guru_id'       => $schedule->guru_id,
                        'guru_asli_id'  => $schedule->guru_id,
                        'tanggal'       => $currentDate->copy(),
                        'status'        => 'masuk', // Default status
                        'keterangan'    => '-',
                    ]);
                }
            }
        }
    }

    /**
     * Handle the Schedule "updated" event.
     */
    public function updated(Schedule $schedule): void
    {
        //
    }

    /**
     * Handle the Schedule "deleted" event.
     */
    public function deleted(Schedule $schedule): void
    {
        // Delete associated attendance records when schedule is deleted
        TeacherAttendance::where('schedule_id', $schedule->id)->delete();
    }

    /**
     * Handle the Schedule "restored" event.
     */
    public function restored(Schedule $schedule): void
    {
        //
    }

    /**
     * Handle the Schedule "force deleted" event.
     */
    public function forceDeleted(Schedule $schedule): void
    {
        //
    }
}
