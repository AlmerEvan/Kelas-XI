package com.kelasxi.aplikasimonitoringkelas.data.model

import com.google.gson.annotations.SerializedName

// Models untuk Jadwal Pelajaran
data class ScheduleResponse(
    val success: Boolean,
    val message: String,
    val data: List<Schedule>
)

data class Schedule(
    val id: Int,
    val hari: String,
    val kelas: String,
    val mata_pelajaran: String,
    val guru_id: Int,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String?,
    val created_at: String,
    val updated_at: String,
    val guru: Guru? = null,
    val guru_pengganti: ScheduleReplacement? = null
)

data class ScheduleReplacement(
    val id: Int? = null,
    @SerializedName("guru_pengganti")
    val guruPengganti: Guru? = null,
    @SerializedName("guru_asli")
    val guruAsli: Guru? = null,
    val keterangan: String? = null,
    val status: String? = null,
    val tanggal: String? = null,
    val jam_mulai: String? = null,
    val jam_selesai: String? = null
)

data class ScheduleRequest(
    val hari: String,
    val kelas: String,
    val mata_pelajaran: String,
    val guru_id: Int,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String?
)

data class Guru(
    val id: Int,
    val name: String,
    val email: String,
    val mata_pelajaran: String? = null
)

// Alias untuk Teacher (sama dengan Guru)
typealias Teacher = Guru

data class TeacherResponse(
    val success: Boolean,
    val message: String,
    val data: List<Teacher>
)

// Models untuk Users Management (Admin)
data class UsersResponse(
    val success: Boolean,
    val message: String,
    val data: List<User>
)

// Models untuk Monitoring
data class MonitoringResponse(
    val success: Boolean,
    val message: String,
    val data: Monitoring?
)

data class MonitoringListResponse(
    val success: Boolean,
    val message: String,
    val data: List<Monitoring>
)

data class Monitoring(
    val id: Int,
    val guru_id: Int,
    val pelapor_id: Int,
    val status_hadir: String, // "Hadir", "Terlambat", "Tidak Hadir"
    val catatan: String?,
    val kelas: String,
    val mata_pelajaran: String,
    val tanggal: String,
    val jam_laporan: String,
    val created_at: String,
    val updated_at: String,
    val guru: Guru,
    val pelapor: Guru
)

data class MonitoringRequest(
    val guru_id: Int,
    val status_hadir: String,
    val catatan: String?,
    val kelas: String,
    val mata_pelajaran: String,
    val tanggal: String? = null,
    val jam_laporan: String? = null
)

data class UpdateRoleRequest(
    val role: String
)