<?php

namespace App\Observers;

use App\Models\GuruPengganti;
use Carbon\Carbon;

class GuruPenggantiObserver
{
    /**
     * Handle the GuruPengganti "created" event.
     */
    public function created(GuruPengganti $substitute): void
    {
        // Initialize status as 'aktif' if not set
        if (!$substitute->status) {
            $substitute->update(['status' => 'aktif']);
        }
    }

    /**
     * Handle the GuruPengganti "updated" event.
     */
    public function updated(GuruPengganti $substitute): void
    {
        // Auto-update status based on dates
        $endDate = $substitute->tanggal_selesai ?? $substitute->tanggal;
        if (Carbon::now()->isAfter($endDate) && $substitute->status !== 'selesai') {
            $substitute->update(['status' => 'selesai']);
        }
    }

    /**
     * Check and update status for all substitute records (for scheduled task)
     */
    public static function updateAllStatusIfNeeded(): void
    {
        $today = Carbon::today();
        
        GuruPengganti::where('status', 'aktif')
            ->where(function ($query) use ($today) {
                $query->whereNull('tanggal_selesai')
                    ->orWhere('tanggal_selesai', '<', $today);
            })
            ->update(['status' => 'selesai']);
    }
}
