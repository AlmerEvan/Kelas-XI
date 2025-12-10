<?php

namespace App\Filament\Resources\GuruPenggantiResource\Pages;

use App\Filament\Resources\GuruPenggantiResource;
use Filament\Actions;
use Filament\Resources\Pages\ListRecords;

class ListGuruPengganti extends ListRecords
{
    protected static string $resource = GuruPenggantiResource::class;

    protected function getHeaderActions(): array
    {
        return [
            Actions\CreateAction::make(),
        ];
    }
}
