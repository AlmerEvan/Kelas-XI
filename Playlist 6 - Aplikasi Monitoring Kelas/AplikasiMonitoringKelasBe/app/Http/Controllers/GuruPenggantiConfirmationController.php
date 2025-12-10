<?php

namespace App\Http\Controllers;

use App\Models\GuruPenggantiConfirmation;
use App\Models\GuruPengganti;
use App\Http\Requests\ConfirmReplacementTeacherRequest;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Carbon\Carbon;

class GuruPenggantiConfirmationController extends Controller
{
    /**
     * Confirm replacement teacher attendance by student
     */
    public function confirm(ConfirmReplacementTeacherRequest $request)
    {
        try {
            $validated = $request->validated();

            // Check if confirmation already exists
            $existing = GuruPenggantiConfirmation::where('guru_pengganti_id', $validated['guru_pengganti_id'])
                ->where('siswa_id', $validated['siswa_id'])
                ->first();

            if ($existing) {
                return response()->json([
                    'success' => false,
                    'message' => 'Anda sudah mengkonfirmasi guru pengganti ini',
                    'data' => null
                ], Response::HTTP_CONFLICT);
            }

            // Get guru_pengganti data
            $guruPengganti = GuruPengganti::findOrFail($validated['guru_pengganti_id']);

            // Create confirmation
            $confirmation = GuruPenggantiConfirmation::create([
                'guru_pengganti_id' => $validated['guru_pengganti_id'],
                'siswa_id' => $validated['siswa_id'],
                'confirmed_at' => Carbon::now()
            ]);

            $confirmation->load([
                'guruPengganti.guruTeacher:id,name',
                'guruPengganti.guruAsliTeacher:id,name',
                'siswa:id,name'
            ]);

            return response()->json([
                'success' => true,
                'message' => 'Terima kasih! Konfirmasi kehadiran guru pengganti telah dikirim.',
                'data' => [
                    'id' => $confirmation->id,
                    'guru_pengganti_id' => $confirmation->guru_pengganti_id,
                    'siswa_id' => $confirmation->siswa_id,
                    'confirmed_at' => $confirmation->confirmed_at,
                    'guru_pengganti' => $confirmation->guruPengganti,
                    'siswa' => $confirmation->siswa
                ]
            ], Response::HTTP_CREATED);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan saat mengkonfirmasi',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get replacement teacher confirmations report
     * Accessible by Kurikulum only
     */
    public function getReport(Request $request)
    {
        try {
            $query = GuruPenggantiConfirmation::with([
                'guruPengganti.guruTeacher:id,name,email',
                'guruPengganti.guruAsliTeacher:id,name,email',
                'siswa:id,name,email'
            ]);

            // Filter by date
            if ($request->has('tanggal') && $request->tanggal) {
                $query->whereDate('confirmed_at', $request->tanggal);
            }

            // Filter by class
            if ($request->has('kelas') && $request->kelas) {
                $query->whereHas('guruPengganti', function ($q) {
                    $q->where('kelas', request('kelas'));
                });
            }

            // Search by student name or guru name
            if ($request->has('search') && $request->search) {
                $search = $request->search;
                $query->where(function ($q) use ($search) {
                    $q->whereHas('siswa', function ($subQ) use ($search) {
                        $subQ->where('name', 'like', "%{$search}%");
                    })
                    ->orWhereHas('guruPengganti.guruTeacher', function ($subQ) use ($search) {
                        $subQ->where('name', 'like', "%{$search}%");
                    })
                    ->orWhereHas('guruPengganti.guruAsliTeacher', function ($subQ) use ($search) {
                        $subQ->where('name', 'like', "%{$search}%");
                    });
                });
            }

            $confirmations = $query->orderBy('confirmed_at', 'desc')
                ->get()
                ->map(function ($item) {
                    return [
                        'id' => $item->id,
                        'guru_pengganti_id' => $item->guru_pengganti_id,
                        'siswa_id' => $item->siswa_id,
                        'confirmed_at' => $item->confirmed_at->format('Y-m-d H:i:s'),
                        'guru_pengganti_nama' => $item->guruPengganti->guruTeacher->name ?? '-',
                        'guru_asli_nama' => $item->guruPengganti->guruAsliTeacher->name ?? '-',
                        'kelas' => $item->guruPengganti->kelas,
                        'jam_mulai' => $item->guruPengganti->jam_mulai,
                        'jam_selesai' => $item->guruPengganti->jam_selesai,
                        'mata_pelajaran' => $item->guruPengganti->mata_pelajaran,
                        'tanggal' => $item->guruPengganti->tanggal,
                        'siswa_nama' => $item->siswa->name ?? '-'
                    ];
                });

            return response()->json([
                'success' => true,
                'message' => 'Data konfirmasi kehadiran guru pengganti berhasil diambil',
                'data' => $confirmations,
                'count' => count($confirmations)
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan saat mengambil data laporan',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get replacement teacher confirmations for a specific guru_pengganti
     */
    public function getConfirmationsByGuruPengganti($id)
    {
        try {
            $guruPengganti = GuruPengganti::findOrFail($id);

            $confirmations = GuruPenggantiConfirmation::where('guru_pengganti_id', $id)
                ->with('siswa:id,name,email')
                ->orderBy('confirmed_at', 'desc')
                ->get()
                ->map(function ($item) {
                    return [
                        'id' => $item->id,
                        'siswa_nama' => $item->siswa->name,
                        'siswa_email' => $item->siswa->email,
                        'confirmed_at' => $item->confirmed_at->format('Y-m-d H:i:s')
                    ];
                });

            return response()->json([
                'success' => true,
                'message' => 'Data konfirmasi berhasil diambil',
                'data' => [
                    'guru_pengganti' => [
                        'id' => $guruPengganti->id,
                        'guru_pengganti_nama' => $guruPengganti->guruTeacher->name ?? '-',
                        'guru_asli_nama' => $guruPengganti->guruAsliTeacher->name ?? '-',
                        'kelas' => $guruPengganti->kelas,
                        'tanggal' => $guruPengganti->tanggal,
                        'jam_mulai' => $guruPengganti->jam_mulai,
                        'jam_selesai' => $guruPengganti->jam_selesai
                    ],
                    'confirmations' => $confirmations,
                    'total_confirmations' => count($confirmations)
                ]
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
}
