<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Relations\BelongsTo;

class GuruPenggantiConfirmation extends Model
{
    use HasFactory;

    protected $table = 'guru_pengganti_confirmations';

    protected $fillable = [
        'guru_pengganti_id',
        'siswa_id',
        'confirmed_at'
    ];

    protected $casts = [
        'confirmed_at' => 'datetime',
    ];

    /**
     * Get the replacement teacher record
     */
    public function guruPengganti(): BelongsTo
    {
        return $this->belongsTo(GuruPengganti::class);
    }

    /**
     * Get the student who confirmed
     */
    public function siswa(): BelongsTo
    {
        return $this->belongsTo(User::class, 'siswa_id');
    }
}
