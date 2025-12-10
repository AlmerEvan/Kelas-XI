<?php

namespace App\Filament\Pages;

use App\Models\GuruPengganti;
use App\Models\TeacherAttendance;
use App\Models\Teacher;
use App\Models\Schedule;
use App\Filament\Widgets\SubstituteTeacherStatsOverview;
use Filament\Pages\Page;
use Filament\Tables\Columns\BadgeColumn;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Columns\Layout\Stack;
use Filament\Tables\Concerns\InteractsWithTable;
use Filament\Tables\Contracts\HasTable;
use Filament\Tables\Table;
use Filament\Tables\Filters\SelectFilter;
use Filament\Tables\Filters\Filter;
use Filament\Actions\Action;
use Filament\Widgets\Concerns\InteractsWithPageFilters;
use Illuminate\Database\Eloquent\Builder;
use Carbon\Carbon;
use UnitEnum;
use BackedEnum;

class SubstituteTeacherManagementPage extends Page implements HasTable
{
    use InteractsWithTable;
    use InteractsWithPageFilters;

    protected static BackedEnum|string|null $navigationIcon = 'heroicon-o-user-plus';
    protected static ?string $navigationLabel = 'Substitutes';
    protected static ?string $title = 'Teacher Substitutes';
    protected static ?int $navigationSort = 2;
    protected static UnitEnum|string|null $navigationGroup = 'Academic';
    protected string $view = 'filament.pages.substitute-teacher-management-page';

    public ?string $filterStatus = null;
    public ?string $filterTeacher = null;

    protected function getHeaderWidgets(): array
    {
        return [];
    }

    public function table(Table $table): Table
    {
        return $table
            ->query(GuruPengganti::query()
                ->where('status', 'aktif')
                ->with(['originalTeacher', 'substituteTeacher', 'schedule']))
            ->columns([
                TextColumn::make('originalTeacher.name')
                    ->label('Guru Asli')
                    ->searchable()
                    ->sortable()
                    ->default('-'),
                TextColumn::make('substituteTeacher.name')
                    ->label('Guru Pengganti')
                    ->searchable()
                    ->sortable()
                    ->default('-'),
                TextColumn::make('mata_pelajaran')
                    ->label('Mata Pelajaran')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('kelas')
                    ->label('Kelas')
                    ->badge()
                    ->color('info'),
                TextColumn::make('tanggal')
                    ->label('Durasi')
                    ->formatStateUsing(function ($state, $record) {
                        $startDate = $record->tanggal;
                        $endDate = $record->tanggal_selesai ?? $record->tanggal;
                        
                        if ($startDate instanceof \Carbon\Carbon) {
                            $startDate = $startDate->format('d/m/Y');
                        }
                        if ($endDate instanceof \Carbon\Carbon) {
                            $endDate = $endDate->format('d/m/Y');
                        }
                        
                        if ($startDate === $endDate) {
                            return $startDate;
                        }
                        return $startDate . ' - ' . $endDate;
                    })
                    ->sortable(),
                BadgeColumn::make('status')
                    ->label('Status')
                    ->colors([
                        'success' => 'aktif',
                        'gray' => 'selesai',
                    ])
                    ->formatStateUsing(fn($state) => ucfirst($state))
                    ->sortable(),
                TextColumn::make('keterangan')
                    ->label('Alasan/Keterangan')
                    ->limit(30)
                    ->default('-'),
            ])
            ->actions([
                Action::make('edit')
                    ->label('Edit')
                    ->icon('heroicon-m-pencil')
                    ->url(fn(GuruPengganti $record) => '/admin/guru-penggantis/' . $record->id . '/edit'),
                Action::make('delete')
                    ->label('Hapus')
                    ->icon('heroicon-m-trash')
                    ->color('danger')
                    ->requiresConfirmation()
                    ->action(function (GuruPengganti $record): void {
                        // Hapus guru_id dari TeacherAttendance
                        TeacherAttendance::where('guru_asli_id', $record->guru_asli_id)
                            ->whereDate('tanggal', $record->tanggal)
                            ->update(['guru_id' => null]);
                        
                        // Hapus guru_pengganti record
                        $record->delete();
                    }),
            ])
            ->filters([
                SelectFilter::make('status')
                    ->options([
                        'aktif' => 'Aktif',
                        'selesai' => 'Selesai',
                    ]),
                Filter::make('tanggal')
                    ->form([
                        \Filament\Forms\Components\DatePicker::make('tanggal_dari')
                            ->label('Dari Tanggal'),
                        \Filament\Forms\Components\DatePicker::make('tanggal_sampai')
                            ->label('Sampai Tanggal'),
                    ])
                    ->query(function (Builder $query, array $data): Builder {
                        return $query
                            ->when(
                                $data['tanggal_dari'],
                                fn(Builder $q, $date) => $q->whereDate('tanggal', '>=', $date),
                            )
                            ->when(
                                $data['tanggal_sampai'],
                                fn(Builder $q, $date) => $q->whereDate('tanggal', '<=', $date),
                            );
                    }),
            ])
            ->defaultSort('tanggal', 'desc')
            ->paginated([10, 25, 50]);
    }
}
