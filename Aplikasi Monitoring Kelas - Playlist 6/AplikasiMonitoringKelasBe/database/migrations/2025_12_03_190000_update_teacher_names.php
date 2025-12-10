<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        // Update nama guru dengan nama yang lebih realistis
        DB::table('teachers')->where('id', 1)->update(['name' => 'Pak Heru']);
        DB::table('teachers')->where('id', 2)->update(['name' => 'Bu Susi']);
        DB::table('teachers')->where('id', 3)->update(['name' => 'Pak Budi']);
        DB::table('teachers')->where('id', 4)->update(['name' => 'Bu Rina']);
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        // Revert ke nama lama jika diperlukan
        DB::table('teachers')->where('id', 1)->update(['name' => 'Guru Bahasa Daerah']);
        DB::table('teachers')->where('id', 2)->update(['name' => 'Guru Bahasa Indonesia']);
        DB::table('teachers')->where('id', 3)->update(['name' => 'Guru Matematika']);
        DB::table('teachers')->where('id', 4)->update(['name' => 'Guru IPA']);
    }
};
