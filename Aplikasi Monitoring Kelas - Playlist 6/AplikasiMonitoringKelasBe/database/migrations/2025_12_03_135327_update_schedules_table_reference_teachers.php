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
        Schema::table('schedules', function (Blueprint $table) {
            // Drop old foreign key
            $table->dropForeign(['guru_id']);
            // Add new foreign key to teachers table
            $table->foreign('guru_id')->references('id')->on('teachers')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('schedules', function (Blueprint $table) {
            $table->dropForeign(['guru_id']);
            $table->foreign('guru_id')->references('id')->on('users')->onDelete('cascade');
        });
    }
};
