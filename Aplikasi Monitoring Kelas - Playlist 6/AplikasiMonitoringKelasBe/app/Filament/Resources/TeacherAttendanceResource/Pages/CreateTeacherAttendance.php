<?php

namespace App\Filament\Resources\TeacherAttendanceResource\Pages;

use App\Filament\Resources\TeacherAttendanceResource;
use App\Filament\Pages\TeacherManagementPage;
use Filament\Actions;
use Filament\Resources\Pages\CreateRecord;

class CreateTeacherAttendance extends CreateRecord
{
    protected static string $resource = TeacherAttendanceResource::class;

    protected function getRedirectUrl(): string
    {
        return TeacherManagementPage::getUrl();
    }
}
