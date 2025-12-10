<?php

namespace App\Filament\Widgets;

use App\Models\TeacherAttendance;
use App\Models\GuruPengganti;
use Filament\Widgets\Widget;
use Carbon\Carbon;

class TeacherStatsOverview extends Widget
{
    protected string $view = 'filament.widgets.teacher-stats-overview';

    public function getViewData(): array
    {
        $today = Carbon::today();

        // Total jadwal hari ini
        $totalMengajar = TeacherAttendance::whereDate('tanggal', $today)->count();
        
        // Guru yang masuk hari ini
        $masuk = TeacherAttendance::whereDate('tanggal', $today)
            ->where('status', 'masuk')
            ->count();
        
        // Guru yang tidak masuk (tidak_masuk + izin)
        $tidakMasuk = TeacherAttendance::whereDate('tanggal', $today)
            ->whereIn('status', ['tidak_masuk', 'izin'])
            ->count();
        
        // Guru yang sudah memiliki pengganti hari ini
        $adaPengganti = GuruPengganti::whereDate('tanggal', $today)->count();

        return [
            'totalMengajar' => $totalMengajar,
            'masuk' => $masuk,
            'tidakMasuk' => $tidakMasuk,
            'adaPengganti' => $adaPengganti,
        ];
    }
}
