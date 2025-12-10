<?php

namespace App\Filament\Resources\GuruPenggantiResource\Pages;

use App\Filament\Resources\GuruPenggantiResource;
use Filament\Actions;
use Filament\Resources\Pages\EditRecord;

class EditGuruPengganti extends EditRecord
{
    protected static string $resource = GuruPenggantiResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\DeleteAction::make(),
        ];
    }

    protected function getRedirectUrl(): string
    {
        return $this->getResource()::getUrl('index');
    }
}
