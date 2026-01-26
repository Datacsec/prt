<template>
  <div class="p-8">
    <!-- En-tête de la page -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Page d'accueil</h1>
      <p class="text-gray-600">Bienvenue sur votre tableau de bord</p>
    </div>

    <!-- Liste des éléments -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-800">Éléments récents</h2>
      </div>
      <ul class="divide-y divide-gray-200">
        <li v-for="item in items" :key="item.id" class="px-6 py-4 hover:bg-gray-50 transition-colors">
          <div class="flex items-center justify-between">
            <div class="flex items-center">
              <div class="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center mr-4">
                <span class="text-primary-600 font-semibold">{{ item.icon }}</span>
              </div>
              <div>
                <p class="text-sm font-medium text-gray-900">{{ item.title }}</p>
                <p class="text-sm text-gray-500">{{ item.description }}</p>
              </div>
            </div>
            <span class="text-xs text-gray-400">{{ item.date }}</span>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Liste des éléments à afficher
const items = ref([
  {
    id: 1,
    title: 'Document 1',
    description: 'Description du document',
    date: 'Il y a 2 jours',
    icon: '📄'
  },
  {
    id: 2,
    title: 'Document 2',
    description: 'Description du document',
    date: 'Il y a 5 jours',
    icon: '📄'
  },
  {
    id: 3,
    title: 'Document 3',
    description: 'Description du document',
    date: 'Il y a 1 semaine',
    icon: '📄'
  }
])
</script>
