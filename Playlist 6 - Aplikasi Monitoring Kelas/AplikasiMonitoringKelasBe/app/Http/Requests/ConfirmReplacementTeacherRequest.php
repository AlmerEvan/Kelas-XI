<?php

namespace App\Http\Requests;

use Illuminate\Foundation\Http\FormRequest;

class ConfirmReplacementTeacherRequest extends FormRequest
{
    /**
     * Determine if the user is authorized to make this request.
     */
    public function authorize(): bool
    {
        return true;
    }

    /**
     * Get the validation rules that apply to the request.
     *
     * @return array<string, \Illuminate\Contracts\Validation\ValidationRule|array|string>
     */
    public function rules(): array
    {
        return [
            'guru_pengganti_id' => 'required|integer|exists:guru_pengganti,id',
            'siswa_id' => 'required|integer|exists:users,id'
        ];
    }

    /**
     * Get the error messages for the defined validation rules.
     *
     * @return array<string, string>
     */
    public function messages(): array
    {
        return [
            'guru_pengganti_id.required' => 'ID guru pengganti wajib diisi',
            'guru_pengganti_id.exists' => 'Guru pengganti tidak ditemukan',
            'siswa_id.required' => 'ID siswa wajib diisi',
            'siswa_id.exists' => 'Siswa tidak ditemukan'
        ];
    }
}
