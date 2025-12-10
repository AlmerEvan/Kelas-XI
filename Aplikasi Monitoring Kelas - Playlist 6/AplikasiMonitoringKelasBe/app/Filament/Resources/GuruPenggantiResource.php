<?php

namespace App\Filament\Resources;

use App\Models\GuruPengganti;
use App\Models\Teacher;
use App\Models\Schedule;
use Filament\Forms;
use Filament\Resources\Resource;
use Filament\Schemas\Schema;
use Filament\Tables;
use Filament\Tables\Table;
use Illuminate\Database\Eloquent\Builder;
use Carbon\Carbon;
use BackedEnum;
use UnitEnum;

class GuruPenggantiResource extends Resource
{
    protected static ?string $model = GuruPengganti::class;
    protected static ?string $navigationLabel = 'Substitutes';
    protected static BackedEnum|string|null $navigationIcon = 'heroicon-o-user-plus';
    protected static bool $shouldRegisterNavigation = false;
    protected static UnitEnum|string|null $navigationGroup = 'Academic';

    public static function form(Schema $schema): Schema
    {
        return $schema
            ->schema([
                \Filament\Schemas\Components\Section::make('Data Guru')
                    ->schema([
                        \Filament\Forms\Components\Select::make('guru_asli_id')
                            ->label('Nama Guru Asli')
                            ->options(Teacher::pluck('name', 'id'))
                            ->searchable()
                            ->required(),
                        \Filament\Forms\Components\Select::make('guru_pengganti_id')
                            ->label('Nama Guru Pengganti')
                            ->options(Teacher::pluck('name', 'id'))
                            ->searchable()
                            ->required(),
                    ]),

                \Filament\Schemas\Components\Section::make('Informasi Pelajaran')
                    ->schema([
                        \Filament\Forms\Components\TextInput::make('mata_pelajaran')
                            ->label('Mata Pelajaran')
                            ->required(),
                        \Filament\Forms\Components\TextInput::make('kelas')
                            ->label('Kelas')
                            ->required(),
                        \Filament\Forms\Components\TextInput::make('ruang')
                            ->label('Ruang/Kelas')
                            ->nullable(),
                    ]),

                \Filament\Schemas\Components\Section::make('Jadwal Penggantian')
                    ->schema([
                        \Filament\Forms\Components\DatePicker::make('tanggal')
                            ->label('Tanggal Mulai')
                            ->required(),
                        \Filament\Forms\Components\DatePicker::make('tanggal_selesai')
                            ->label('Tanggal Selesai')
                            ->helperText('Kosongkan jika hanya satu hari'),
                        \Filament\Forms\Components\TextInput::make('jam_mulai')
                            ->label('Jam Mulai')
                            ->type('time')
                            ->nullable(),
                        \Filament\Forms\Components\TextInput::make('jam_selesai')
                            ->label('Jam Selesai')
                            ->type('time')
                            ->nullable(),
                    ]),

                \Filament\Schemas\Components\Section::make('Status & Keterangan')
                    ->schema([
                        \Filament\Forms\Components\Select::make('status')
                            ->label('Status')
                            ->options([
                                'aktif' => 'Aktif',
                                'selesai' => 'Selesai',
                            ])
                            ->required()
                            ->default('aktif'),
                        \Filament\Forms\Components\Textarea::make('keterangan')
                            ->label('Alasan/Keterangan')
                            ->placeholder('Contoh: Guru sakit, dinas, training, dsb...')
                            ->nullable()
                            ->rows(3),
                    ]),
            ]);
    }

    public static function table(Table $table): Table
    {
        return $table
            ->modifyQueryUsing(fn(Builder $query) => $query->where('status', 'aktif'))
            ->columns([
                Tables\Columns\TextColumn::make('originalTeacher.name')
                    ->label('Guru Asli')
                    ->searchable()
                    ->sortable(),
                Tables\Columns\TextColumn::make('substituteTeacher.name')
                    ->label('Guru Pengganti')
                    ->searchable()
                    ->sortable(),
                Tables\Columns\TextColumn::make('mata_pelajaran')
                    ->label('Mata Pelajaran')
                    ->searchable()
                    ->sortable(),
                Tables\Columns\TextColumn::make('kelas')
                    ->label('Kelas')
                    ->badge()
                    ->color('info'),
                Tables\Columns\TextColumn::make('tanggal')
                    ->label('Durasi')
                    ->formatStateUsing(function ($state, $record) {
                        $endDate = $record->tanggal_selesai ?? $record->tanggal;
                        if ($state->eq($endDate)) {
                            return $state->format('d/m/Y');
                        }
                        return $state->format('d/m/Y') . ' - ' . $endDate->format('d/m/Y');
                    })
                    ->sortable(),
                Tables\Columns\BadgeColumn::make('status')
                    ->label('Status')
                    ->colors([
                        'success' => 'aktif',
                        'gray' => 'selesai',
                    ])
                    ->formatStateUsing(fn($state) => ucfirst($state))
                    ->sortable(),
                Tables\Columns\TextColumn::make('keterangan')
                    ->label('Keterangan')
                    ->limit(25)
                    ->default('-'),
            ])
            ->filters([
                Tables\Filters\SelectFilter::make('status')
                    ->options([
                        'aktif' => 'Aktif',
                        'selesai' => 'Selesai',
                    ]),
                Tables\Filters\Filter::make('tanggal')
                    ->form([
                        Forms\Components\DatePicker::make('tanggal_dari')
                            ->label('Dari Tanggal'),
                        Forms\Components\DatePicker::make('tanggal_sampai')
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
            ->defaultSort('tanggal', 'desc');
    }

    public static function getPages(): array
    {
        return [
            'index' => \App\Filament\Resources\GuruPenggantiResource\Pages\ListGuruPengganti::route('/'),
            'create' => \App\Filament\Resources\GuruPenggantiResource\Pages\CreateGuruPengganti::route('/create'),
            'edit' => \App\Filament\Resources\GuruPenggantiResource\Pages\EditGuruPengganti::route('/{record}/edit'),
        ];
    }
}
