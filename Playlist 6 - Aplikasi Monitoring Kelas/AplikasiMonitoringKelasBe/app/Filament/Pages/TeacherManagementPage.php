<?php

namespace App\Filament\Pages;

use App\Models\Schedule;
use App\Models\TeacherAttendance;
use App\Models\Teacher;
use App\Models\GuruPengganti;
use App\Filament\Widgets\TeacherStatsOverview;
use App\Filament\Resources\TeacherAttendanceResource;
use Filament\Pages\Page;
use Filament\Tables\Columns\BadgeColumn;
use Filament\Tables\Columns\TextColumn;
use Filament\Tables\Columns\Layout\Stack;
use Filament\Tables\Concerns\InteractsWithTable;
use Filament\Tables\Filters\SelectFilter;
use Filament\Tables\Contracts\HasTable;
use Filament\Tables\Table;
use Filament\Actions\Action;
use Filament\Widgets\Concerns\InteractsWithPageFilters;
use Illuminate\Database\Eloquent\Builder;
use Carbon\Carbon;
use UnitEnum;
use BackedEnum;

class TeacherManagementPage extends Page implements HasTable
{
    use InteractsWithTable;
    use InteractsWithPageFilters;

    protected static BackedEnum|string|null $navigationIcon = 'heroicon-o-chart-bar';
    protected static ?string $navigationLabel = 'Attendance';
    protected static ?string $title = 'Teacher Attendance';
    protected static ?int $navigationSort = 1;
    protected static UnitEnum|string|null $navigationGroup = 'Academic';
    protected string $view = 'filament.pages.teacher-management-page';

    public ?string $filterStatus = null;
    public ?string $filterKelas = null;
    public ?string $filterGuru = null;

    protected function getHeaderWidgets(): array
    {
        return [];
    }

    public function table(Table $table): Table
    {
        return $table
            ->query(
                TeacherAttendance::query()
                    // ->whereDate('tanggal', Carbon::today())
                    ->with(['schedule', 'guruAsliTeacher'])
                    ->join('schedules', 'teacher_attendances.schedule_id', '=', 'schedules.id')
                    ->select('teacher_attendances.*')
                    ->whereHas('schedule', function (Builder $query) {
                        // Filter guru berdasarkan hari kerja mereka
                        $query->whereRaw("
                            NOT EXISTS (
                                SELECT 1 FROM teachers
                                WHERE teachers.id = teacher_attendances.guru_asli_id
                                AND (
                                    teachers.working_days IS NOT NULL
                                    AND JSON_CONTAINS(teachers.working_days, JSON_QUOTE(schedules.hari)) = 0
                                )
                            )
                        ");
                    })
            )
            ->columns([
                TextColumn::make('schedule.mata_pelajaran')
                    ->label('Mata Pelajaran')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('schedule.kelas')
                    ->label('Kelas')
                    ->searchable()
                    ->sortable(),
                TextColumn::make('tanggal')
                    ->label('Tanggal')
                    ->date('d/m/Y')
                    ->sortable(),
                TextColumn::make('schedule.hari')
                    ->label('Hari')
                    ->badge()
                    ->color('info')
                    ->sortable('schedules.hari'),
                TextColumn::make('schedule.jam_mulai')
                    ->label('Jam')
                    ->formatStateUsing(fn($state, $record) => 
                        $state . ' - ' . $record->schedule->jam_selesai
                    )
                    ->sortable(),
                TextColumn::make('guruAsliTeacher.name')
                    ->label('Guru Asli')
                    ->searchable()
                    ->default('-'),
                TextColumn::make('guru_pengganti_name')
                    ->label('Guru Pengganti')
                    ->getStateUsing(function ($record) {
                        // Only show guru pengganti if status is tidak_masuk or izin
                        if (!in_array($record->status, ['tidak_masuk', 'izin'])) {
                            return '-';
                        }
                        
                        $pengganti = GuruPengganti::where('guru_asli_id', $record->guru_asli_id)
                            ->whereDate('tanggal', $record->tanggal)
                            ->with('substituteTeacher')
                            ->first();
                        return $pengganti?->substituteTeacher?->name ?? '-';
                    }),
                BadgeColumn::make('status')
                    ->label('Status Kehadiran')
                    ->colors([
                        'success' => 'masuk',
                        'danger' => 'tidak_masuk',
                        'warning' => 'izin',
                    ])
                    ->formatStateUsing(fn($state) => ucfirst(str_replace('_', ' ', $state)))
                    ->sortable(),
                TextColumn::make('jam_masuk')
                    ->label('Jam Masuk')
                    ->formatStateUsing(fn($state) => $state ? (is_object($state) ? $state->format('H:i') : $state) : '-')
                    ->default('-'),
                TextColumn::make('keterangan')
                    ->label('Keterangan')
                    ->limit(30)
                    ->default('-'),
            ])
            ->filters([
                SelectFilter::make('status')
                    ->options([
                        'masuk' => 'Masuk',
                        'tidak_masuk' => 'Tidak Masuk',
                        'izin' => 'Izin',
                    ]),
            ])
            ->actions([
                Action::make('edit')
                    ->label('Edit')
                    ->icon('heroicon-m-pencil')
                    ->url(fn($record) => TeacherAttendanceResource::getUrl('edit', ['record' => $record->id])),
                Action::make('assign_pengganti')
                    ->label('Assign Pengganti')
                    ->icon('heroicon-m-arrow-path')
                    ->url(fn($record) => '/admin/guru-penggantis/create?guru_asli_id=' . $record->guru_asli_id . '&tanggal=' . $record->tanggal->format('Y-m-d'))
                    ->visible(function ($record) {
                        $hasPengganti = GuruPengganti::where('guru_asli_id', $record->guru_asli_id)
                            ->whereDate('tanggal', $record->tanggal)
                            ->exists();
                        return in_array($record->status, ['tidak_masuk', 'izin']) && !$hasPengganti;
                    }),
                Action::make('delete')
                    ->label('Hapus')
                    ->icon('heroicon-m-trash')
                    ->requiresConfirmation()
                    ->action(fn($record) => $record->delete()),
            ])
            ->defaultSort('schedule.jam_mulai', 'asc')
            ->paginated([10, 25, 50]);
    }

    protected function getHeaderActions(): array
    {
        return [
            Action::make('create')
                ->label('Tambah Data')
                ->url(TeacherAttendanceResource::getUrl('create')),
        ];
    }
}

