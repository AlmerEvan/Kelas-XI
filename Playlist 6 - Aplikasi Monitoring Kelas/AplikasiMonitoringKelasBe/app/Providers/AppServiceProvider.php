<?php

namespace App\Providers;

use App\Models\TeacherAttendance;
use App\Models\GuruPengganti;
use App\Models\Schedule;
use App\Observers\TeacherAttendanceObserver;
use App\Observers\GuruPenggantiObserver;
use App\Observers\ScheduleObserver;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        //
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        // Register model observers for automatic synchronization
        TeacherAttendance::observe(TeacherAttendanceObserver::class);
        GuruPengganti::observe(GuruPenggantiObserver::class);
        Schedule::observe(ScheduleObserver::class);
    }
}
