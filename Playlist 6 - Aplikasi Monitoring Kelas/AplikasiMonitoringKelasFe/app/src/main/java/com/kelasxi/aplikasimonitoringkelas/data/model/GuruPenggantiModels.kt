package com.kelasxi.aplikasimonitoringkelas.data.model

import com.google.gson.annotations.SerializedName

// Models untuk Teacher Replacement (Guru Pengganti) - Updated System
data class TeacherReplacementResponse(
    val success: Boolean,
    val message: String,
    val data: List<TeacherReplacement>
)

data class TeacherReplacement(
    val id: Int,
    val schedule_id: Int,
    val guru_pengganti: Guru,
    val guru_asli: Guru?,
    val kelas: String,
    val mata_pelajaran: String,
    val tanggal: String,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String?,
    val keterangan: String?,
    val assigned_by: User?,
    val created_at: String,
    val updated_at: String
)

data class AssignReplacementRequest(
    val attendance_id: Int,
    val guru_pengganti_id: Int,
    val keterangan: String?
)

// Models untuk Guru Pengganti (OLD - Keep for backward compatibility)
data class GuruPenggantiResponse(
    val success: Boolean,
    val message: String,
    val data: List<GuruPengganti>
)

data class GuruPengganti(
    val id: Int,
    val schedule_id: Int,
    @SerializedName("guru_pengganti")
    val guruPengganti: Guru?,
    @SerializedName("guru_asli")
    val guruAsli: Guru?,
    val kelas: String,
    val mata_pelajaran: String,
    val tanggal: String,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String?,
    val keterangan: String?,
    val assigned_by: AssignedBy?,
    val created_at: String,
    val updated_at: String
)

data class AssignedBy(
    val id: Int,
    val name: String
)

data class GuruPenggantiRequest(
    val guru_pengganti_id: Int,
    val guru_asli_id: Int?,
    val kelas: String,
    val mata_pelajaran: String,
    val tanggal: String,
    val jam_mulai: String,
    val jam_selesai: String,
    val ruang: String?,
    val keterangan: String?
)

// Models untuk Kelas Kosong
data class KelasKosongResponse(
    val success: Boolean,
    val message: String,
    val data: List<KelasKosong>,
    val summary: KelasKosongSummary
)

data class KelasKosong(
    val jadwal_id: Int?,
    val attendance_id: Int?, // ID dari teacher_attendance
    val kelas: String,
    val mata_pelajaran: String,
    val guru: Guru,
    val jam_mulai: String?,
    val jam_selesai: String?,
    val ruang: String?,
    val tanggal: String,
    val hari: String,
    val status: String, // "Tidak Hadir"
    val keterangan: String? // Keterangan dari teacher attendance
)

data class KelasKosongSummary(
    val total_jadwal: Int,
    val total_kelas_kosong: Int,
    val tanggal: String,
    val hari: String
)

// ==================== Guru Pengganti Confirmation Models ====================

data class ConfirmReplacementTeacherRequest(
    val guru_pengganti_id: Int,
    val siswa_id: Int
)

data class ReplacementConfirmation(
    val id: Int,
    val guru_pengganti_id: Int,
    val siswa_id: Int,
    val confirmed_at: String,
    val guru_pengganti: GuruPenggantiData?,
    val siswa: SiswaData?
)

data class GuruPenggantiData(
    val id: Int,
    val name: String
)

data class SiswaData(
    val id: Int,
    val name: String
)

// ==================== Replacement Teacher Report Models ====================

data class ReplacementTeacherReportResponse(
    val success: Boolean,
    val message: String,
    val data: List<ReplacementTeacherReportItem>,
    val count: Int
)

data class ReplacementTeacherReportItem(
    val id: Int,
    val guru_pengganti_id: Int,
    val siswa_id: Int,
    val confirmed_at: String,
    val guru_pengganti_nama: String,
    val guru_asli_nama: String,
    val kelas: String,
    val jam_mulai: String,
    val jam_selesai: String,
    val mata_pelajaran: String,
    val tanggal: String,
    val siswa_nama: String
)

data class SpecificGuruPenggantiConfirmationResponse(
    val success: Boolean,
    val message: String,
    val data: GuruPenggantiConfirmationDetails
)

data class GuruPenggantiConfirmationDetails(
    val guru_pengganti: GuruPenggantiInfo,
    val confirmations: List<StudentConfirmationInfo>,
    val total_confirmations: Int
)

data class GuruPenggantiInfo(
    val id: Int,
    val guru_pengganti_nama: String,
    val guru_asli_nama: String,
    val kelas: String,
    val tanggal: String,
    val jam_mulai: String,
    val jam_selesai: String
)

data class StudentConfirmationInfo(
    val id: Int,
    val siswa_nama: String,
    val siswa_email: String,
    val confirmed_at: String
)

