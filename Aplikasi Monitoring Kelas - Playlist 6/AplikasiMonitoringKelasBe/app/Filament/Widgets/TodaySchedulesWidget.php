<?php

namespace App\Filament\Widgets;

use App\Models\Schedule;
use Filament\Tables;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Table;
use Filament\Widgets\TableWidget as BaseWidget;
use Carbon\Carbon;

class TodaySchedulesWidget extends BaseWidget
{
    protected static ?int $sort = 4;
    protected static ?string $heading = 'Today\'s Schedules';
    protected int | string | array $columnSpan = 'md:col-span-2';

    public function table(Table $table): Table
    {
        $today = Carbon::today();
        $dayName = $today->format('l');
        $dayMap = [
            'Monday' => 'Senin',
            'Tuesday' => 'Selasa',
            'Wednesday' => 'Rabu',
            'Thursday' => 'Kamis',
            'Friday' => 'Jumat',
            'Saturday' => 'Sabtu',
        ];
        $hari = $dayMap[$dayName] ?? 'Senin';

        return $table
            ->query(
                Schedule::query()
                    ->where('hari', $hari)
                    ->with('teacher')
                    ->orderBy('jam_mulai')
            )
            ->columns([
                TextColumn::make('jam_mulai')
                    ->label('Time')
                    ->time('H:i')
                    ->sortable(),
                TextColumn::make('teacher.name')
                    ->label('Teacher')
                    ->sortable(),
                TextColumn::make('mata_pelajaran')
                    ->label('Subject')
                    ->sortable(),
                TextColumn::make('kelas')
                    ->label('Class')
                    ->sortable(),
            ])
            ->paginated(false);
    }
}
