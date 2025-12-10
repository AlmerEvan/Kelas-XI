<?php

namespace App\Filament\Resources\GuruPenggantiResource\Pages;

use App\Filament\Resources\GuruPenggantiResource;
use App\Models\TeacherAttendance;
use App\Models\Schedule;
use Filament\Actions;
use Filament\Resources\Pages\CreateRecord;
use Illuminate\Database\Eloquent\Model;

class CreateGuruPengganti extends CreateRecord
{
    protected static string $resource = GuruPenggantiResource::class;

    public function mount(): void
    {
        parent::mount();

        // Ambil parameter dari URL
        $guruAsliId = request()->query('guru_asli_id');
        $tanggal = request()->query('tanggal');

        if ($guruAsliId && $tanggal) {
            // Cari attendance data berdasarkan guru_asli_id dan tanggal
            $attendance = TeacherAttendance::where('guru_asli_id', $guruAsliId)
                ->whereDate('tanggal', $tanggal)
                ->with('schedule')
                ->first();

            if ($attendance) {
                // Isi form dengan data dari attendance
                $this->form->fill([
                    'guru_asli_id' => $attendance->guru_asli_id,
                    'tanggal' => $attendance->tanggal,
                    'mata_pelajaran' => $attendance->schedule->mata_pelajaran,
                    'kelas' => $attendance->schedule->kelas,
                    'ruang' => $attendance->schedule->ruang,
                    'jam_mulai' => $attendance->schedule->jam_mulai ? $attendance->schedule->jam_mulai->format('H:i') : null,
                    'jam_selesai' => $attendance->schedule->jam_selesai ? $attendance->schedule->jam_selesai->format('H:i') : null,
                    'tanggal_selesai' => $attendance->tanggal,
                ]);
            }
        }
    }

    protected function getRedirectUrl(): string
    {
        return $this->getResource()::getUrl('index');
    }
}


