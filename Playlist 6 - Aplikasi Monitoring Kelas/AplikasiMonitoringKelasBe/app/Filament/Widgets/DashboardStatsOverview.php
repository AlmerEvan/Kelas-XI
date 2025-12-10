<?php

namespace App\Filament\Widgets;

use App\Models\Teacher;
use App\Models\Schedule;
use App\Models\TeacherAttendance;
use App\Models\GuruPengganti;
use App\Models\Kelas;
use App\Models\Subject;
use Filament\Widgets\StatsOverviewWidget as BaseWidget;
use Filament\Widgets\StatsOverviewWidget\Stat;
use Illuminate\Support\Facades\Cache;

class DashboardStatsOverview extends BaseWidget
{
    protected int | string | array $columnSpan = 'full';

    protected function getStats(): array
    {
        // Cache hasil untuk performance - 5 menit
        return Cache::remember('dashboard_stats', 300, function () {
            return [
                Stat::make('Total Guru', Teacher::count())
                    ->description('Jumlah guru terdaftar')
                    ->descriptionIcon('heroicon-m-user-group')
                    ->color('info'),
                
                Stat::make('Total Kelas', Kelas::count())
                    ->description('Jumlah kelas')
                    ->descriptionIcon('heroicon-m-squares-2x2')
                    ->color('success'),
                
                Stat::make('Total Mata Pelajaran', Subject::count())
                    ->description('Jumlah mata pelajaran')
                    ->descriptionIcon('heroicon-m-book-open')
                    ->color('warning'),
                
                Stat::make('Total Jadwal', Schedule::count())
                    ->description('Jumlah jadwal mengajar')
                    ->descriptionIcon('heroicon-m-calendar-days')
                    ->color('primary'),
                
                Stat::make('Kehadiran Hari Ini', TeacherAttendance::whereDate('created_at', today())->count())
                    ->description('Data kehadiran hari ini')
                    ->descriptionIcon('heroicon-m-check-circle')
                    ->color('success'),
                
                Stat::make('Guru Aktif Pengganti', GuruPengganti::where('status', 'aktif')->count())
                    ->description('Guru pengganti yang sedang aktif')
                    ->descriptionIcon('heroicon-m-user-plus')
                    ->color('danger'),
            ];
        });
    }
}
