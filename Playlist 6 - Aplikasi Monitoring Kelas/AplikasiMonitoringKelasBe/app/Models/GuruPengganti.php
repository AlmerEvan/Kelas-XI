<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Relations\BelongsTo;
use Illuminate\Database\Eloquent\Relations\HasMany;
use Carbon\Carbon;

class GuruPengganti extends Model
{
    use HasFactory;

    protected $table = 'guru_pengganti';

    protected $fillable = [
        'guru_pengganti_id',
        'guru_asli_id',
        'schedule_id',
        'kelas',
        'mata_pelajaran',
        'tanggal',
        'tanggal_selesai',
        'jam_mulai',
        'jam_selesai',
        'ruang',
        'keterangan',
        'status',
        'assigned_by'
    ];

    protected $casts = [
        'tanggal' => 'date',
        'tanggal_selesai' => 'date',
    ];

    /**
     * Status constants
     */
    public const STATUS_AKTIF = 'aktif';
    public const STATUS_SELESAI = 'selesai';

    /**
     * Get the substitute teacher
     */
    public function guruPengganti(): BelongsTo
    {
        return $this->belongsTo(User::class, 'guru_pengganti_id');
    }

    /**
     * Get the substitute teacher from teachers table
     */
    public function substituteTeacher(): BelongsTo
    {
        return $this->belongsTo(Teacher::class, 'guru_pengganti_id');
    }

    /**
     * Get the original teacher
     */
    public function guruAsli(): BelongsTo
    {
        return $this->belongsTo(User::class, 'guru_asli_id');
    }

    /**
     * Get the original teacher from teachers table
     */
    public function originalTeacher(): BelongsTo
    {
        return $this->belongsTo(Teacher::class, 'guru_asli_id');
    }

    /**
     * Alias for guruPengganti relation (returns Teacher from Teachers table)
     */
    public function guruTeacher(): BelongsTo
    {
        return $this->belongsTo(Teacher::class, 'guru_pengganti_id');
    }

    /**
     * Alias for guruAsli relation (returns Teacher from Teachers table)
     */
    public function guruAsliTeacher(): BelongsTo
    {
        return $this->belongsTo(Teacher::class, 'guru_asli_id');
    }

    /**
     * Get the schedule associated
     */
    public function schedule(): BelongsTo
    {
        return $this->belongsTo(Schedule::class);
    }

    /**
     * Get the user who assigned this substitute
     */
    public function assignedBy(): BelongsTo
    {
        return $this->belongsTo(User::class, 'assigned_by');
    }

    /**
     * Get all confirmations for this replacement
     */
    public function confirmations(): HasMany
    {
        return $this->hasMany(GuruPenggantiConfirmation::class);
    }

    /**
     * Get status badge color
     */
    public function getStatusColorAttribute(): string
    {
        return match($this->status) {
            self::STATUS_AKTIF => 'success',
            self::STATUS_SELESAI => 'gray',
            default => 'gray',
        };
    }

    /**
     * Auto-update status based on dates
     */
    public function updateStatusIfNeeded(): void
    {
        $endDate = $this->tanggal_selesai ?? $this->tanggal;
        if (Carbon::now()->isAfter($endDate)) {
            $this->update(['status' => self::STATUS_SELESAI]);
        } else {
            $this->update(['status' => self::STATUS_AKTIF]);
        }
    }

    /**
     * Check if substitution is active
     */
    public function isActive(): bool
    {
        $endDate = $this->tanggal_selesai ?? $this->tanggal;
        return $this->status === self::STATUS_AKTIF && Carbon::now()->lte($endDate);
    }

    /**
     * Get duration in days
     */
    public function getDurationDaysAttribute(): int
    {
        $endDate = $this->tanggal_selesai ?? $this->tanggal;
        return $this->tanggal->diffInDays($endDate) + 1;
    }
}
