<?php

namespace App\Observers;

use App\Models\TeacherAttendance;
use App\Models\GuruPengganti;

class TeacherAttendanceObserver
{
    /**
     * Handle the TeacherAttendance "created" event.
     */
    public function created(TeacherAttendance $attendance): void
    {
        // Optional: Auto-create placeholder in GuruPengganti if status is "tidak_masuk"
        // This allows tracking and later assignment of substitute teacher
        if ($attendance->isTidakMasuk()) {
            // Can be extended to auto-notify or create draft records
        }
    }

    /**
     * Handle the TeacherAttendance "updated" event.
     * Sync changes to GuruPengganti records
     */
    public function updated(TeacherAttendance $attendance): void
    {
        // If status changed to "tidak_masuk", prepare for substitute
        if ($attendance->isTidakMasuk() && $attendance->wasChanged('status')) {
            // Mark as needs substitute assignment
        }
    }

    /**
     * Handle the TeacherAttendance "deleted" event.
     */
    public function deleted(TeacherAttendance $attendance): void
    {
        // Delete related GuruPengganti record if exists
        // Note: Since teacher_attendance_id column doesn't exist in guru_pengganti table,
        // we rely on cascading deletes through foreign key constraints if implemented
    }
}
