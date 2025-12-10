<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Schedule extends Model
{
    use HasFactory;

    protected $fillable = [
        'hari',
        'kelas',
        'mata_pelajaran',
        'guru_id',
        'jam_mulai',
        'jam_selesai',
        'ruang'
    ];

    protected $casts = [
        'jam_mulai' => 'datetime:H:i',
        'jam_selesai' => 'datetime:H:i',
    ];

    /**
     * Get the teacher for this schedule
     */
    public function guru(): BelongsTo
    {
        return $this->belongsTo(User::class, 'guru_id');
    }

    /**
     * Get the teacher record (teachers table)
     */
    public function teacher(): BelongsTo
    {
        return $this->belongsTo(Teacher::class, 'guru_id');
    }

    /**
     * Get all attendance records for this schedule
     */
    public function attendances(): HasMany
    {
        return $this->hasMany(TeacherAttendance::class);
    }

    /**
     * Get class information
     */
    public function kelas(): BelongsTo
    {
        return $this->belongsTo(Kelas::class, 'kelas', 'kode_kelas');
    }

    /**
     * Get subject information
     */
    public function subject(): BelongsTo
    {
        return $this->belongsTo(Subject::class, 'mata_pelajaran', 'kode');
    }

    /**
     * Get teacher replacement (guru pengganti) - only active/current one
     */
    public function guruPengganti(): HasMany
    {
        return $this->hasMany(GuruPengganti::class, 'schedule_id');
    }

    /**
     * Get the current/active teacher replacement
     */
    public function currentReplacement()
    {
        return $this->hasMany(GuruPengganti::class, 'schedule_id')
                    ->where('status', GuruPengganti::STATUS_AKTIF)
                    ->latest()
                    ->first();
    }

    /**
     * Helper: Get schedule display name
     */
    public function getDisplayNameAttribute(): string
    {
        return "{$this->mata_pelajaran} - {$this->kelas} ({$this->hari})";
    }
}
