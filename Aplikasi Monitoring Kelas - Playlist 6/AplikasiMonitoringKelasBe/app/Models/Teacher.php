<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Teacher extends Model
{
    use HasFactory;

    protected $fillable = [
        'name',
        'email',
        'email_verified_at',
        'password',
        'mata_pelajaran',
        'is_banned',
        'working_days',
        'remember_token',
    ];

    protected $hidden = [
        'password',
        'remember_token',
    ];

    protected $casts = [
        'email_verified_at' => 'datetime',
        'is_banned' => 'boolean',
        'working_days' => 'array',
    ];

    /**
     * Get all schedules for this teacher
     */
    public function schedules(): HasMany
    {
        return $this->hasMany(Schedule::class, 'guru_id');
    }

    /**
     * Get all attendance records for this teacher
     */
    public function attendances(): HasMany
    {
        return $this->hasMany(TeacherAttendance::class, 'guru_asli_id');
    }

    /**
     * Get all substitute assignments for this teacher
     */
    public function substitutes(): HasMany
    {
        return $this->hasMany(GuruPengganti::class, 'guru_pengganti_id');
    }

    /**
     * Get name attribute for display
     */
    public function getDisplayNameAttribute(): string
    {
        return "{$this->name} ({$this->mata_pelajaran})";
    }

    /**
     * Check if teacher is available on a specific day
     */
    public function isWorkingDay($day): bool
    {
        if (!$this->working_days) {
            return true; // Default: works all days
        }
        return in_array($day, $this->working_days);
    }

    /**
     * Get working days as comma-separated string
     */
    public function getWorkingDaysStringAttribute(): string
    {
        if (!$this->working_days) {
            return 'Semua hari';
        }
        return implode(', ', $this->working_days);
    }
}
