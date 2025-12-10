@php
    $data = $this->getViewData();
    $totalPengganti = $data['totalPengganti'] ?? 0;
    $statusAktif = $data['statusAktif'] ?? 0;
    $statusSelesai = $data['statusSelesai'] ?? 0;
    $hariIni = $data['hariIni'] ?? 0;
@endphp

<div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
    <!-- Total Pengganti Card -->
    <div class="overflow-hidden rounded-lg bg-white shadow">
        <div class="px-4 py-5 sm:p-6">
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 20h5v-2a3 3 0 00-5.856-1.487M15 10h.01M11 10h.01M7 10h.01M6 20a6 6 0 1112 0v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2z"></path>
                    </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                    <dl>
                        <dt class="truncate text-sm font-medium text-gray-500">Total Pengganti</dt>
                        <dd>
                            <div class="text-2xl font-semibold text-gray-900">{{ $totalPengganti }}</div>
                            <div class="text-xs text-gray-500">Seluruh data</div>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>

    <!-- Status Aktif Card -->
    <div class="overflow-hidden rounded-lg bg-white shadow">
        <div class="px-4 py-5 sm:p-6">
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-green-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
                    </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                    <dl>
                        <dt class="truncate text-sm font-medium text-gray-500">Status Aktif</dt>
                        <dd>
                            <div class="text-2xl font-semibold text-gray-900">{{ $statusAktif }}</div>
                            <div class="text-xs text-gray-500">Penggantian berlangsung</div>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>

    <!-- Status Selesai Card -->
    <div class="overflow-hidden rounded-lg bg-white shadow">
        <div class="px-4 py-5 sm:p-6">
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4"></path>
                    </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                    <dl>
                        <dt class="truncate text-sm font-medium text-gray-500">Status Selesai</dt>
                        <dd>
                            <div class="text-2xl font-semibold text-gray-900">{{ $statusSelesai }}</div>
                            <div class="text-xs text-gray-500">Penggantian selesai</div>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>

    <!-- Hari Ini Card -->
    <div class="overflow-hidden rounded-lg bg-white shadow">
        <div class="px-4 py-5 sm:p-6">
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                    </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                    <dl>
                        <dt class="truncate text-sm font-medium text-gray-500">Hari Ini</dt>
                        <dd>
                            <div class="text-2xl font-semibold text-gray-900">{{ $hariIni }}</div>
                            <div class="text-xs text-gray-500">Penggantian hari ini</div>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>
</div>
