<?php

namespace App\Http\Controllers;

use App\Models\Schedule;
use App\Models\GuruPengganti;
use Illuminate\Http\Request;
use Illuminate\Http\Response;

class ScheduleController extends Controller
{
    /**
     * Mengambil semua jadwal pelajaran dengan data guru dan guru pengganti jika ada
     */
    public function index(Request $request)
    {
        try {
            // First, get basic schedules with guru
            $schedules = Schedule::with('guru:id,name,email')
                              ->orderBy('hari')
                              ->orderBy('jam_mulai')
                              ->get();

            // Filter berdasarkan hari jika ada
            if ($request->has('hari') && $request->hari) {
                $schedules = $schedules->filter(function($schedule) use ($request) {
                    return $schedule->hari === $request->hari;
                })->values();
            }

            // Filter berdasarkan kelas jika ada
            if ($request->has('kelas') && $request->kelas) {
                $schedules = $schedules->filter(function($schedule) use ($request) {
                    return strpos($schedule->kelas, $request->kelas) !== false;
                })->values();
            }

            // Add guru_pengganti data for each schedule
            $schedules = $schedules->map(function($schedule) {
                try {
                    // Get active replacement for this schedule
                    // Match by kelas and mata_pelajaran with today's date
                    $replacement = GuruPengganti::where('status', 'aktif')
                        ->with(['guruTeacher:id,name,email', 'guruAsliTeacher:id,name,email'])
                        ->where('kelas', $schedule->kelas)
                        ->where('mata_pelajaran', $schedule->mata_pelajaran)
                        ->whereDate('tanggal', today())
                        ->orderBy('created_at', 'desc')
                        ->first();
                    
                    if ($replacement) {
                        $schedule->guru_pengganti = [
                            'id' => $replacement->id,
                            'guru_pengganti' => $replacement->guruTeacher,
                            'guru_asli' => $replacement->guruAsliTeacher,
                            'keterangan' => $replacement->keterangan,
                            'status' => $replacement->status,
                            'tanggal' => $replacement->tanggal,
                            'jam_mulai' => $replacement->jam_mulai,
                            'jam_selesai' => $replacement->jam_selesai
                        ];
                    } else {
                        $schedule->guru_pengganti = null;
                    }
                } catch (\Exception $e) {
                    // If there's an error loading replacement, just set it to null
                    $schedule->guru_pengganti = null;
                }
                return $schedule;
            });

            return response()->json([
                'success' => true,
                'message' => 'Data jadwal berhasil diambil',
                'data' => $schedules
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Menambah jadwal baru
     */
    public function store(Request $request)
    {
        try {
            $request->validate([
                'hari' => 'required|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
                'kelas' => 'required|string|max:10',
                'mata_pelajaran' => 'required|string|max:255',
                'guru_id' => 'required|exists:users,id',
                'jam_mulai' => 'required|date_format:H:i',
                'jam_selesai' => 'required|date_format:H:i|after:jam_mulai',
                'ruang' => 'nullable|string|max:50'
            ]);

            $schedule = Schedule::create($request->all());
            $schedule->load('guru:id,name,email');

            return response()->json([
                'success' => true,
                'message' => 'Jadwal berhasil ditambahkan',
                'data' => $schedule
            ], Response::HTTP_CREATED);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mengupdate jadwal yang sudah ada
     */
    public function update(Request $request, $id)
    {
        try {
            $schedule = Schedule::findOrFail($id);

            $request->validate([
                'hari' => 'sometimes|required|in:Senin,Selasa,Rabu,Kamis,Jumat,Sabtu',
                'kelas' => 'sometimes|required|string|max:10',
                'mata_pelajaran' => 'sometimes|required|string|max:255',
                'guru_id' => 'sometimes|required|exists:users,id',
                'jam_mulai' => 'sometimes|required|date_format:H:i',
                'jam_selesai' => 'sometimes|required|date_format:H:i|after:jam_mulai',
                'ruang' => 'nullable|string|max:50'
            ]);

            $schedule->update($request->all());
            $schedule->load('guru:id,name,email');

            return response()->json([
                'success' => true,
                'message' => 'Jadwal berhasil diupdate',
                'data' => $schedule
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Menghapus jadwal
     */
    public function destroy($id)
    {
        try {
            $schedule = Schedule::findOrFail($id);
            $schedule->delete();

            return response()->json([
                'success' => true,
                'message' => 'Jadwal berhasil dihapus'
            ], Response::HTTP_OK);

        } catch (\Exception $e) {
            return response()->json([
                'success' => false,
                'message' => 'Terjadi kesalahan server',
                'error' => $e->getMessage()
            ], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
    }
}
