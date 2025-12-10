<?php

namespace App\Filament\Widgets;

use App\Models\TeacherAttendance;
use Filament\Widgets\ChartWidget;
use Illuminate\Support\Facades\Cache;
use Carbon\Carbon;

class AttendanceChart extends ChartWidget
{
    protected ?string $heading = 'Attendance Summary This Week';
    protected static ?int $sort = 2;
    protected int | string | array $columnSpan = 'full';

    protected function getData(): array
    {
        // Cache chart data - 1 jam
        return Cache::remember('attendance_chart', 3600, function () {
            $days = [];
            $masuk = [];
            $tidakMasuk = [];
            $izin = [];

            // Get data untuk 7 hari terakhir
            for ($i = 6; $i >= 0; $i--) {
                $date = Carbon::today()->subDays($i);
                $days[] = $date->format('D');

                $masuk[] = TeacherAttendance::whereDate('created_at', $date)
                    ->where('status', 'masuk')
                    ->count();

                $tidakMasuk[] = TeacherAttendance::whereDate('created_at', $date)
                    ->where('status', 'tidak_masuk')
                    ->count();

                $izin[] = TeacherAttendance::whereDate('created_at', $date)
                    ->where('status', 'izin')
                    ->count();
            }

            return [
                'datasets' => [
                    [
                        'label' => 'Hadir',
                        'data' => $masuk,
                        'borderColor' => '#10b981',
                        'backgroundColor' => 'rgba(16, 185, 129, 0.1)',
                        'borderWidth' => 2,
                        'tension' => 0.4,
                    ],
                    [
                        'label' => 'Tidak Hadir',
                        'data' => $tidakMasuk,
                        'borderColor' => '#ef4444',
                        'backgroundColor' => 'rgba(239, 68, 68, 0.1)',
                        'borderWidth' => 2,
                        'tension' => 0.4,
                    ],
                    [
                        'label' => 'Izin',
                        'data' => $izin,
                        'borderColor' => '#f59e0b',
                        'backgroundColor' => 'rgba(245, 158, 11, 0.1)',
                        'borderWidth' => 2,
                        'tension' => 0.4,
                    ],
                ],
                'labels' => $days,
            ];
        });
    }

    protected function getType(): string
    {
        return 'line';
    }

    protected function getOptions(): array
    {
        return [
            'plugins' => [
                'legend' => [
                    'display' => true,
                    'position' => 'top',
                ],
            ],
            'scales' => [
                'y' => [
                    'beginAtZero' => true,
                    'ticks' => [
                        'stepSize' => 1,
                    ],
                ],
            ],
        ];
    }
}
