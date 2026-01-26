<template>
  <div class="p-8">
    <!-- En-tête de la page -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Banque</h1>
      <p class="text-gray-600">Consultez vos comptes bancaires</p>
    </div>

    <!-- Liste des comptes bancaires -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-800">Mes comptes</h2>
      </div>
      <ul class="divide-y divide-gray-200">
        <li v-for="account in accounts" :key="account.id" class="px-6 py-4 hover:bg-gray-50 transition-colors">
          <div class="flex items-center justify-between">
            <div class="flex items-center flex-1">
              <!-- Icône de compte bancaire -->
              <div class="w-12 h-12 rounded-lg bg-blue-100 flex items-center justify-center mr-4">
                <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z" />
                </svg>
              </div>
              <!-- Informations du compte -->
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900">{{ account.name }}</p>
                <p class="text-sm text-gray-500">IBAN: {{ account.iban }}</p>
                <p class="text-xs text-gray-400 mt-1">Type: {{ account.type }}</p>
              </div>
            </div>
            <!-- Solde -->
            <div class="text-right ml-4">
              <p class="text-lg font-bold text-gray-900">{{ account.balance }} €</p>
              <p class="text-xs text-gray-500">Solde disponible</p>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Liste des comptes bancaires
const accounts = ref([
  {
    id: 1,
    name: 'Compte Courant Principal',
    iban: 'FR76 1234 5678 9012 3456 7890 123',
    type: 'Compte courant',
    balance: '12,450.00'
  },
  {
    id: 2,
    name: 'Compte Épargne',
    iban: 'FR76 9876 5432 1098 7654 3210 987',
    type: 'Livret A',
    balance: '8,750.50'
  },
  {
    id: 3,
    name: 'Compte Professionnel',
    iban: 'FR76 5555 6666 7777 8888 9999 000',
    type: 'Compte professionnel',
    balance: '25,320.75'
  }
])
</script>
