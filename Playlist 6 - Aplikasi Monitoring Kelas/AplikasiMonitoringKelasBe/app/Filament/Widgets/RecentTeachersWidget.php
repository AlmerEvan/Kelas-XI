<?php

namespace App\Filament\Widgets;

use App\Models\Teacher;
use Filament\Tables;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Table;
use Filament\Widgets\TableWidget as BaseWidget;
use Illuminate\Database\Eloquent\Builder;

class RecentTeachersWidget extends BaseWidget
{
    protected static ?int $sort = 3;
    protected static ?string $heading = 'Recent Teachers';
    protected int | string | array $columnSpan = 'md:col-span-2';

    public function table(Table $table): Table
    {
        return $table
            ->query(Teacher::query()->latest()->limit(5))
            ->columns([
                TextColumn::make('name')
                    ->label('Name')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('mata_pelajaran')
                    ->label('Subject')
                    ->sortable(),
                TextColumn::make('created_at')
                    ->label('Created')
                    ->dateTime('d/m/Y')
                    ->sortable(),
            ])
            ->paginated(false);
    }
}
