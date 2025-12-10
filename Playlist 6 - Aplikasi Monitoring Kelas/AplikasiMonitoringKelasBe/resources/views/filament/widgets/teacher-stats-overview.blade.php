@php
    $totalMengajar = $this->getViewData()['totalMengajar'] ?? 0;
    $masuk = $this->getViewData()['masuk'] ?? 0;
    $tidakMasuk = $this->getViewData()['tidakMasuk'] ?? 0;
    $adaPengganti = $this->getViewData()['adaPengganti'] ?? 0;
@endphp

<div class="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
    <!-- Total Mengajar Card -->
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
                        <dt class="truncate text-sm font-medium text-gray-500">Total Mengajar</dt>
                        <dd>
                            <div class="text-2xl font-semibold text-gray-900">{{ $totalMengajar }}</div>
                            <div class="text-xs text-gray-500">Jadwal hari ini</div>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>

    <!-- Guru Masuk Card -->
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
                        <dt class="truncate text-sm font-medium text-gray-500">Guru Masuk</dt>
                        <dd>
                            <div class="text-2xl font-semibold text-gray-900">{{ $masuk }}</div>
                            <div class="text-xs text-gray-500">Hadir hari ini</div>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>

    <!-- Guru Tidak Masuk Card -->
    <div class="overflow-hidden rounded-lg bg-white shadow">
        <div class="px-4 py-5 sm:p-6">
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M10 14l-2-2m0 0l-2-2m2 2l2-2m-2 2l-2 2m8-2l2 2m0 0l2 2m-2-2l-2-2m2 2l2 2M9 12a3 3 0 11-6 0 3 3 0 016 0zm12 0a3 3 0 11-6 0 3 3 0 016 0z"></path>
                    </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                    <dl>
                        <dt class="truncate text-sm font-medium text-gray-500">Guru Tidak Masuk</dt>
                        <dd>
                            <div class="text-2xl font-semibold text-gray-900">{{ $tidakMasuk }}</div>
                            <div class="text-xs text-gray-500">Izin/Sakit/Alpa</div>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>

    <!-- Ada Pengganti Card -->
    <div class="overflow-hidden rounded-lg bg-white shadow">
        <div class="px-4 py-5 sm:p-6">
            <div class="flex items-center">
                <div class="flex-shrink-0">
                    <svg class="h-6 w-6 text-orange-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"></path>
                    </svg>
                </div>
                <div class="ml-5 w-0 flex-1">
                    <dl>
                        <dt class="truncate text-sm font-medium text-gray-500">Ada Pengganti</dt>
                        <dd>
                            <div class="text-2xl font-semibold text-gray-900">{{ $adaPengganti }}</div>
                            <div class="text-xs text-gray-500">Sudah digantikan</div>
                        </dd>
                    </dl>
                </div>
            </div>
        </div>
    </div>
</div>
