<?php

namespace App\Filament\Resources\TeacherAttendanceResource\Pages;

use App\Filament\Resources\TeacherAttendanceResource;
use App\Filament\Pages\TeacherManagementPage;
use Filament\Actions;
use Filament\Resources\Pages\EditRecord;

class EditTeacherAttendance extends EditRecord
{
    protected static string $resource = TeacherAttendanceResource::class;

    protected function mutateFormDataBeforeFill(array $data): array
    {
        if (isset($data['jam_masuk']) && $data['jam_masuk']) {
            $data['jam_masuk'] = is_object($data['jam_masuk']) 
                ? $data['jam_masuk']->format('H:i') 
                : $data['jam_masuk'];
        }

        return $data;
    }

    protected function mutateFormDataBeforeSave(array $data): array
    {
        if (isset($data['jam_masuk']) && $data['jam_masuk']) {
            $data['jam_masuk'] = $data['jam_masuk'] . ':00';
        }

        return $data;
    }

    protected function getHeaderActions(): array
    {
        return [
            Actions\DeleteAction::make(),
        ];
    }

    protected function getRedirectUrl(): string
    {
        return TeacherManagementPage::getUrl();
    }
}
