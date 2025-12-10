<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasOne;

class TeacherAttendance extends Model
{
    use HasFactory;

    protected $table = 'teacher_attendances';

    protected $fillable = [
        'schedule_id',
        'guru_id',
        'guru_asli_id',
        'tanggal',
        'jam_masuk',
        'status',
        'keterangan',
        'created_by',
        'assigned_by'
    ];

    protected $casts = [
        'tanggal' => 'date',
        'jam_masuk' => 'datetime:H:i',
    ];

    /**
     * Get the schedule for this attendance record
     */
    public function schedule(): BelongsTo
    {
        return $this->belongsTo(Schedule::class);
    }

    /**
     * Get the guru (backward compatibility with User model)
     */
    public function guru(): BelongsTo
    {
        return $this->belongsTo(User::class, 'guru_id');
    }

    /**
     * Get the guru from teachers table
     */
    public function guruTeacher(): BelongsTo
    {
        return $this->belongsTo(Teacher::class, 'guru_id');
    }

    /**
     * Get the guru asli (original teacher) - from users table
     */
    public function guruAsli(): BelongsTo
    {
        return $this->belongsTo(User::class, 'guru_asli_id');
    }

    /**
     * Get the guru asli from teachers table
     */
    public function guruAsliTeacher(): BelongsTo
    {
        return $this->belongsTo(Teacher::class, 'guru_asli_id');
    }

    /**
     * Get the guru pengganti assignment from guru_pengganti table
     * Matches by guru_asli_id and tanggal
     */
    public function guruPengganti(): HasOne
    {
        return $this->hasOne(GuruPengganti::class, 'guru_asli_id', 'guru_asli_id')
            ->where(function ($query) {
                $query->where('tanggal', '=', $this->tanggal);
            });
    }

    /**
     * Get the user who created this record
     */
    public function createdBy(): BelongsTo
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    /**
     * Get the user who assigned the substitute
     */
    public function assignedBy(): BelongsTo
    {
        return $this->belongsTo(User::class, 'assigned_by');
    }

    /**
     * Status constants
     */
    public const STATUS_MASUK = 'masuk';
    public const STATUS_TIDAK_MASUK = 'tidak_masuk';
    public const STATUS_IZIN = 'izin';

    /**
     * Get status badge color
     */
    public function getStatusColorAttribute(): string
    {
        return match($this->status) {
            self::STATUS_MASUK => 'success',
            self::STATUS_TIDAK_MASUK => 'danger',
            self::STATUS_IZIN => 'warning',
            default => 'gray',
        };
    }

    /**
     * Check if status is "tidak masuk"
     */
    public function isTidakMasuk(): bool
    {
        return $this->status === self::STATUS_TIDAK_MASUK;
    }
}
