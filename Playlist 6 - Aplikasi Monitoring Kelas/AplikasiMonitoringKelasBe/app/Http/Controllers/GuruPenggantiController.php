<?php

namespace App\Http\Controllers;

use App\Models\GuruPengganti;
use Illuminate\Http\Request;
use Illuminate\Http\Response;
use Carbon\Carbon;

class GuruPenggantiController extends Controller
{
    /**
     * Menampilkan daftar guru pengganti
     */
    public function index(Request $request)
    {
        try {
            $query = GuruPengganti::with(['guruTeacher:id,name,email,mata_pelajaran', 'guruAsliTeacher:id,name,email,mata_pelajaran', 'assignedBy:id,name']);

            // Filter berdasarkan kelas
            if ($request->has('kelas') && $request->kelas) {
                $query->where('kelas', $request->kelas);
            }

            $data = $query->orderBy('tanggal', 'desc')
                          ->orderBy('jam_mulai', 'asc')
                          ->get()
                          ->map(function($item) {
                              // Transform relation names to match frontend expectations
                              return [
                                  'id' => $item->id,
                                  'schedule_id' => $item->schedule_id,
                                  'guru_pengganti' => $item->guruTeacher,
                                  'guru_asli' => $item->guruAsliTeacher,
                                  'kelas' => $item->kelas,
                                  'mata_pelajaran' => $item->mata_pelajaran,
                                  'tanggal' => $item->tanggal,
                                  'tanggal_selesai' => $item->tanggal_selesai,
                                  'jam_mulai' => $item->jam_mulai,
                                  'jam_selesai' => $item->jam_selesai,
                                  'ruang' => $item->ruang,
                                  'keterangan' => $item->keterangan,
                                  'status' => $item->status,
                                  'assigned_by' => $item->assignedBy,
                                  'created_at' => $item->created_at,
                                  'updated_at' => $item->updated_at
                              ];
                          });

            return response()->json([
                'success' => true,
                'message' => 'Data guru pengganti berhasil diambil',
                'data' => $data
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
     * Menyimpan data guru pengganti baru
     */
    public function store(Request $request)
    {
        try {
            $request->validate([
                'guru_pengganti_id' => 'required|exists:users,id',
                'guru_asli_id' => 'nullable|exists:users,id',
                'kelas' => 'required|string',
                'mata_pelajaran' => 'required|string',
                'tanggal' => 'required|date',
                'jam_mulai' => 'required|date_format:H:i',
                'jam_selesai' => 'required|date_format:H:i|after:jam_mulai',
                'ruang' => 'nullable|string',
                'keterangan' => 'nullable|string'
            ]);

            $guruPengganti = GuruPengganti::create([
                'guru_pengganti_id' => $request->guru_pengganti_id,
                'guru_asli_id' => $request->guru_asli_id,
                'kelas' => $request->kelas,
                'mata_pelajaran' => $request->mata_pelajaran,
                'tanggal' => $request->tanggal,
                'jam_mulai' => $request->jam_mulai,
                'jam_selesai' => $request->jam_selesai,
                'ruang' => $request->ruang,
                'keterangan' => $request->keterangan,
                'assigned_by' => $request->user()->id
            ]);

            $guruPengganti->load(['guruTeacher:id,name,email,mata_pelajaran', 'guruAsliTeacher:id,name,email,mata_pelajaran']);

            return response()->json([
                'success' => true,
                'message' => 'Guru pengganti berhasil ditambahkan',
                'data' => [
                    'id' => $guruPengganti->id,
                    'schedule_id' => $guruPengganti->schedule_id,
                    'guru_pengganti' => $guruPengganti->guruTeacher,
                    'guru_asli' => $guruPengganti->guruAsliTeacher,
                    'kelas' => $guruPengganti->kelas,
                    'mata_pelajaran' => $guruPengganti->mata_pelajaran,
                    'tanggal' => $guruPengganti->tanggal,
                    'tanggal_selesai' => $guruPengganti->tanggal_selesai,
                    'jam_mulai' => $guruPengganti->jam_mulai,
                    'jam_selesai' => $guruPengganti->jam_selesai,
                    'ruang' => $guruPengganti->ruang,
                    'keterangan' => $guruPengganti->keterangan,
                    'status' => $guruPengganti->status,
                    'assigned_by' => $guruPengganti->assignedBy,
                    'created_at' => $guruPengganti->created_at,
                    'updated_at' => $guruPengganti->updated_at
                ]
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
     * Mengupdate data guru pengganti
     */
    public function update(Request $request, $id)
    {
        try {
            $guruPengganti = GuruPengganti::findOrFail($id);

            $request->validate([
                'guru_pengganti_id' => 'sometimes|required|exists:users,id',
                'guru_asli_id' => 'nullable|exists:users,id',
                'kelas' => 'sometimes|required|string',
                'mata_pelajaran' => 'sometimes|required|string',
                'tanggal' => 'sometimes|required|date',
                'jam_mulai' => 'sometimes|required|date_format:H:i',
                'jam_selesai' => 'sometimes|required|date_format:H:i',
                'ruang' => 'nullable|string',
                'keterangan' => 'nullable|string'
            ]);

            $guruPengganti->update($request->all());
            $guruPengganti->load(['guruTeacher:id,name,email,mata_pelajaran', 'guruAsliTeacher:id,name,email,mata_pelajaran']);

            return response()->json([
                'success' => true,
                'message' => 'Guru pengganti berhasil diupdate',
                'data' => [
                    'id' => $guruPengganti->id,
                    'schedule_id' => $guruPengganti->schedule_id,
                    'guru_pengganti' => $guruPengganti->guruTeacher,
                    'guru_asli' => $guruPengganti->guruAsliTeacher,
                    'kelas' => $guruPengganti->kelas,
                    'mata_pelajaran' => $guruPengganti->mata_pelajaran,
                    'tanggal' => $guruPengganti->tanggal,
                    'tanggal_selesai' => $guruPengganti->tanggal_selesai,
                    'jam_mulai' => $guruPengganti->jam_mulai,
                    'jam_selesai' => $guruPengganti->jam_selesai,
                    'ruang' => $guruPengganti->ruang,
                    'keterangan' => $guruPengganti->keterangan,
                    'status' => $guruPengganti->status,
                    'assigned_by' => $guruPengganti->assignedBy,
                    'created_at' => $guruPengganti->created_at,
                    'updated_at' => $guruPengganti->updated_at
                ]
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
     * Menghapus data guru pengganti
     */
    public function destroy($id)
    {
        try {
            $guruPengganti = GuruPengganti::findOrFail($id);
            $guruPengganti->delete();

            return response()->json([
                'success' => true,
                'message' => 'Guru pengganti berhasil dihapus'
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
