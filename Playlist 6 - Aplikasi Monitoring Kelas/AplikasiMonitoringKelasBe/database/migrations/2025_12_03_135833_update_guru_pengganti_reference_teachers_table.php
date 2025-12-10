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
        Schema::table('guru_pengganti', function (Blueprint $table) {
            // Drop old foreign keys to users table
            $table->dropForeign(['guru_asli_id']);
            $table->dropForeign(['guru_pengganti_id']);
            // Add new foreign keys to teachers table
            $table->foreign('guru_asli_id')->references('id')->on('teachers')->onDelete('set null');
            $table->foreign('guru_pengganti_id')->references('id')->on('teachers')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('guru_pengganti', function (Blueprint $table) {
            $table->dropForeign(['guru_asli_id']);
            $table->dropForeign(['guru_pengganti_id']);
            $table->foreign('guru_asli_id')->references('id')->on('users')->onDelete('set null');
            $table->foreign('guru_pengganti_id')->references('id')->on('users')->onDelete('cascade');
        });
    }
};
