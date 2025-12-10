<?php

namespace App\Filament\Resources;

use App\Models\TeacherAttendance;
use App\Models\Schedule;
use App\Models\Teacher;
use Filament\Forms;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Tables;
use Filament\Tables\Table;
use Illuminate\Database\Eloquent\Builder;
use Carbon\Carbon;
use BackedEnum;
use UnitEnum;

class TeacherAttendanceResource extends Resource
{
    protected static ?string $model = TeacherAttendance::class;
    protected static ?string $navigationLabel = 'Attendance';
    protected static BackedEnum|string|null $navigationIcon = 'heroicon-o-check-circle';
    protected static bool $shouldRegisterNavigation = false;
    protected static UnitEnum|string|null $navigationGroup = 'Academic';

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->schema([
                \Filament\Schemas\Components\Section::make('Informasi Jadwal')
                    ->schema([
                        \Filament\Forms\Components\Select::make('schedule_id')
                            ->label('Jadwal Pelajaran')
                            ->options(
                                Schedule::with('teacher')
                                    ->get()
                                    ->mapWithKeys(function ($schedule) {
                                        $label = "{$schedule->mata_pelajaran} - {$schedule->kelas} ({$schedule->hari} {$schedule->jam_mulai})";
                                        return [$schedule->id => $label];
                                    })
                            )
                            ->searchable()
                            ->required(),
                        \Filament\Forms\Components\DatePicker::make('tanggal')
                            ->label('Tanggal')
                            ->required()
                            ->default(today()),
                    ]),

                \Filament\Schemas\Components\Section::make('Data Guru')
                    ->schema([
                        \Filament\Forms\Components\Select::make('guru_asli_id')
                            ->label('Guru Asli (Seharusnya Mengajar)')
                            ->options(Teacher::pluck('name', 'id'))
                            ->searchable()
                            ->required(),
                        \Filament\Forms\Components\Select::make('guru_id')
                            ->label('Guru Pengganti (Opsional)')
                            ->options(Teacher::pluck('name', 'id'))
                            ->searchable()
                            ->nullable()
                            ->helperText('Isi jika ada guru pengganti'),
                    ]),

                \Filament\Schemas\Components\Section::make('Status & Keterangan')
                    ->schema([
                        \Filament\Forms\Components\Select::make('status')
                            ->label('Status Kehadiran')
                            ->options([
                                'masuk' => 'Masuk',
                                'tidak_masuk' => 'Tidak Masuk',
                                'izin' => 'Izin',
                            ])
                            ->required()
                            ->default('masuk'),
                        \Filament\Forms\Components\TextInput::make('jam_masuk')
                            ->label('Jam Masuk')
                            ->type('time')
                            ->nullable(),
                        \Filament\Forms\Components\Textarea::make('keterangan')
                            ->label('Keterangan/Alasan')
                            ->placeholder('Contoh: Sakit, Rapat, dsb...')
                            ->nullable()
                            ->rows(3),
                    ]),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->columns([
                Tables\Columns\TextColumn::make('schedule.mata_pelajaran')
                    ->label('Mata Pelajaran')
                    ->searchable()
                    ->sortable(),
                Tables\Columns\TextColumn::make('schedule.kelas')
                    ->label('Kelas')
                    ->searchable()
                    ->sortable(),
                Tables\Columns\TextColumn::make('tanggal')
                    ->label('Tanggal')
                    ->date('d/m/Y')
                    ->sortable(),
                Tables\Columns\TextColumn::make('schedule.hari')
                    ->label('Hari')
                    ->badge()
                    ->color('info'),
                Tables\Columns\TextColumn::make('schedule.jam_mulai')
                    ->label('Jam')
                    ->formatStateUsing(fn($state, $record) => 
                        $state . ' - ' . $record->schedule->jam_selesai
                    )
                    ->sortable(),
                Tables\Columns\TextColumn::make('guruAsliTeacher.name')
                    ->label('Guru Asli')
                    ->searchable()
                    ->default('-'),
                Tables\Columns\TextColumn::make('guru_pengganti_name')
                    ->label('Guru Pengganti')
                    ->getStateUsing(function ($record) {
                        $pengganti = \App\Models\GuruPengganti::where('guru_asli_id', $record->guru_asli_id)
                            ->whereDate('tanggal', $record->tanggal)
                            ->with('substituteTeacher')
                            ->first();
                        return $pengganti?->substituteTeacher?->name ?? '-';
                    }),
                Tables\Columns\BadgeColumn::make('status')
                    ->label('Status Kehadiran')
                    ->colors([
                        'success' => 'masuk',
                        'danger' => 'tidak_masuk',
                        'warning' => 'izin',
                    ])
                    ->formatStateUsing(fn($state) => ucfirst(str_replace('_', ' ', $state)))
                    ->sortable(),
                Tables\Columns\TextColumn::make('jam_masuk')
                    ->label('Jam Masuk')
                    ->formatStateUsing(fn($state) => $state ? (is_object($state) ? $state->format('H:i') : $state) : '-')
                    ->default('-'),
                Tables\Columns\TextColumn::make('keterangan')
                    ->label('Keterangan')
                    ->limit(30)
                    ->default('-'),
            ])
            ->filters([
                Tables\Filters\SelectFilter::make('status')
                    ->options([
                        'masuk' => 'Masuk',
                        'tidak_masuk' => 'Tidak Masuk',
                        'izin' => 'Izin',
                    ]),
            ])
            ->defaultSort('schedule.jam_mulai', 'asc')
            ->paginated([10, 25, 50]);
    }

    public static function getPages(): array
    {
        return [
            'index' => \App\Filament\Resources\TeacherAttendanceResource\Pages\ListTeacherAttendances::route('/'),
            'create' => \App\Filament\Resources\TeacherAttendanceResource\Pages\CreateTeacherAttendance::route('/create'),
            'edit' => \App\Filament\Resources\TeacherAttendanceResource\Pages\EditTeacherAttendance::route('/{record}/edit'),
        ];
    }
}
