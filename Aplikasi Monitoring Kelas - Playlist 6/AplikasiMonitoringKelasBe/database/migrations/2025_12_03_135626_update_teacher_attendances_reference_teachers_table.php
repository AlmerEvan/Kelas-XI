<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('teacher_attendances', function (Blueprint $table) {
            // Drop old foreign keys to users table
            $table->dropForeign(['guru_asli_id']);
            // Add new foreign key to teachers table
            $table->foreign('guru_asli_id')->references('id')->on('teachers')->onDelete('set null');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('teacher_attendances', function (Blueprint $table) {
            $table->dropForeign(['guru_asli_id']);
            $table->foreign('guru_asli_id')->references('id')->on('users')->onDelete('set null');
        });
    }
};
