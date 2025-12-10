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
        Schema::create('guru_pengganti_confirmations', function (Blueprint $table) {
            $table->id();
            $table->foreignId('guru_pengganti_id')->constrained('guru_pengganti')->onDelete('cascade');
            $table->foreignId('siswa_id')->constrained('users')->onDelete('cascade');
            $table->timestamp('confirmed_at');
            $table->timestamps();
            
            // Unique constraint to prevent duplicate confirmations per student per guru_pengganti
            $table->unique(['guru_pengganti_id', 'siswa_id']);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('guru_pengganti_confirmations');
    }
};
