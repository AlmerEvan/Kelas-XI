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
        // First change to VARCHAR to allow any value
        Schema::table('teacher_attendances', function (Blueprint $table) {
            $table->string('status')->change();
        });

        // Now update the data
        DB::statement("UPDATE teacher_attendances SET status = 'masuk' WHERE status IN ('hadir', 'diganti')");
        DB::statement("UPDATE teacher_attendances SET status = 'tidak_masuk' WHERE status = 'tidak_hadir'");
        DB::statement("UPDATE teacher_attendances SET status = 'izin' WHERE status = 'telat'");

        // Finally, change back to enum with new values
        Schema::table('teacher_attendances', function (Blueprint $table) {
            $table->enum('status', ['masuk', 'tidak_masuk', 'izin'])->default('masuk')->change();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('teacher_attendances', function (Blueprint $table) {
            $table->enum('status', ['hadir', 'telat', 'tidak_hadir', 'diganti'])->default('hadir')->change();
        });
    }
};


