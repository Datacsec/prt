<template>
  <div class="p-8">
    <!-- En-tête de la page -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Téléchargements</h1>
      <p class="text-gray-600">Gérez vos fichiers téléchargés</p>
    </div>

    <!-- Liste des téléchargements -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-800">Mes téléchargements</h2>
      </div>
      <ul class="divide-y divide-gray-200">
        <li v-for="download in downloads" :key="download.id" class="px-6 py-4 hover:bg-gray-50 transition-colors">
          <div class="flex items-center justify-between">
            <div class="flex items-center flex-1">
              <!-- Icône de fichier -->
              <div class="w-12 h-12 rounded-lg bg-blue-100 flex items-center justify-center mr-4">
                <svg class="w-6 h-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21h10a2 2 0 002-2V9.414a1 1 0 00-.293-.707l-5.414-5.414A1 1 0 0012.586 3H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
                </svg>
              </div>
              <!-- Informations du fichier -->
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900 truncate">{{ download.name }}</p>
                <p class="text-sm text-gray-500">{{ download.size }} • {{ download.date }}</p>
              </div>
            </div>
            <!-- Bouton de téléchargement -->
            <button class="ml-4 px-4 py-2 text-sm font-medium text-primary-600 bg-primary-50 rounded-lg hover:bg-primary-100 transition-colors">
              Télécharger
            </button>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Liste des téléchargements
const downloads = ref([
  {
    id: 1,
    name: 'document-important.pdf',
    size: '2.4 MB',
    date: 'Téléchargé le 15 janvier 2024'
  },
  {
    id: 2,
    name: 'rapport-annuel.pdf',
    size: '5.8 MB',
    date: 'Téléchargé le 12 janvier 2024'
  },
  {
    id: 3,
    name: 'presentation.pptx',
    size: '12.3 MB',
    date: 'Téléchargé le 10 janvier 2024'
  },
  {
    id: 4,
    name: 'image-photo.jpg',
    size: '1.2 MB',
    date: 'Téléchargé le 8 janvier 2024'
  }
])
</script>
