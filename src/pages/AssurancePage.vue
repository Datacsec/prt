<template>
  <div class="p-8">
    <!-- En-tête de la page -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Assurance</h1>
      <p class="text-gray-600">Gérez vos contrats d'assurance</p>
    </div>

    <!-- Liste des contrats d'assurance -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-800">Mes contrats</h2>
      </div>
      <ul class="divide-y divide-gray-200">
        <li v-for="contract in contracts" :key="contract.id" class="px-6 py-4 hover:bg-gray-50 transition-colors">
          <div class="flex items-center justify-between">
            <div class="flex items-center flex-1">
              <!-- Icône d'assurance -->
              <div class="w-12 h-12 rounded-lg bg-green-100 flex items-center justify-center mr-4">
                <svg class="w-6 h-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                </svg>
              </div>
              <!-- Informations du contrat -->
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900">{{ contract.type }}</p>
                <p class="text-sm text-gray-500">Numéro: {{ contract.number }}</p>
                <p class="text-xs text-gray-400 mt-1">Expire le: {{ contract.expiryDate }}</p>
              </div>
            </div>
            <!-- Statut -->
            <span class="px-3 py-1 text-xs font-medium rounded-full" :class="contract.status === 'Actif' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'">
              {{ contract.status }}
            </span>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Liste des contrats d'assurance
const contracts = ref([
  {
    id: 1,
    type: 'Assurance Automobile',
    number: 'AUTO-2024-001',
    expiryDate: '15 décembre 2024',
    status: 'Actif'
  },
  {
    id: 2,
    type: 'Assurance Habitation',
    number: 'HAB-2024-002',
    expiryDate: '20 mars 2025',
    status: 'Actif'
  },
  {
    id: 3,
    type: 'Assurance Santé',
    number: 'SANTE-2024-003',
    expiryDate: '10 janvier 2024',
    status: 'Expiré'
  },
  {
    id: 4,
    type: 'Assurance Vie',
    number: 'VIE-2024-004',
    expiryDate: '30 juin 2025',
    status: 'Actif'
  }
])
</script>
