<?php

namespace App\Filament\Widgets;

use App\Models\GuruPengganti;
use Filament\Widgets\Widget;
use Carbon\Carbon;

class SubstituteTeacherStatsOverview extends Widget
{
    protected string $view = 'filament.widgets.substitute-teacher-stats-overview';

    public function getViewData(): array
    {
        $today = Carbon::today();

        $totalPengganti = GuruPengganti::count();
        $statusAktif = GuruPengganti::where('status', 'aktif')
            ->where('tanggal', '<=', $today)
            ->where(function ($q) use ($today) {
                $q->whereNull('tanggal_selesai')
                    ->orWhere('tanggal_selesai', '>=', $today);
            })
            ->count();
        $statusSelesai = GuruPengganti::where('status', 'selesai')->count();
        $hariIni = GuruPengganti::where('status', 'aktif')
            ->whereDate('tanggal', $today)
            ->count();

        return [
            'totalPengganti' => $totalPengganti,
            'statusAktif' => $statusAktif,
            'statusSelesai' => $statusSelesai,
            'hariIni' => $hariIni,
        ];
    }
}
