<template>
  <div class="p-8">
    <!-- En-tête de la page -->
    <div class="mb-8">
      <h1 class="text-3xl font-bold text-gray-800 mb-2">Historique</h1>
      <p class="text-gray-600">Consultez votre historique d'activité</p>
    </div>

    <!-- Liste de l'historique -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200">
      <div class="px-6 py-4 border-b border-gray-200">
        <h2 class="text-lg font-semibold text-gray-800">Activités récentes</h2>
      </div>
      <ul class="divide-y divide-gray-200">
        <li v-for="activity in activities" :key="activity.id" class="px-6 py-4 hover:bg-gray-50 transition-colors">
          <div class="flex items-start">
            <!-- Icône d'activité -->
            <div class="w-10 h-10 rounded-full flex items-center justify-center mr-4 flex-shrink-0" :class="activity.iconBg">
              <span class="text-lg">{{ activity.icon }}</span>
            </div>
            <!-- Détails de l'activité -->
            <div class="flex-1 min-w-0">
              <p class="text-sm font-medium text-gray-900">{{ activity.title }}</p>
              <p class="text-sm text-gray-500 mt-1">{{ activity.description }}</p>
              <p class="text-xs text-gray-400 mt-2">{{ activity.time }}</p>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// Liste des activités
const activities = ref([
  {
    id: 1,
    title: 'Connexion réussie',
    description: 'Vous vous êtes connecté depuis un nouvel appareil',
    time: 'Il y a 2 heures',
    icon: '🔐',
    iconBg: 'bg-green-100'
  },
  {
    id: 2,
    title: 'Document téléchargé',
    description: 'document-important.pdf a été téléchargé',
    time: 'Il y a 5 heures',
    icon: '📥',
    iconBg: 'bg-blue-100'
  },
  {
    id: 3,
    title: 'Profil mis à jour',
    description: 'Vos informations personnelles ont été modifiées',
    time: 'Il y a 1 jour',
    icon: '✏️',
    iconBg: 'bg-purple-100'
  },
  {
    id: 4,
    title: 'Paiement effectué',
    description: 'Paiement de 150€ effectué avec succès',
    time: 'Il y a 2 jours',
    icon: '💳',
    iconBg: 'bg-yellow-100'
  },
  {
    id: 5,
    title: 'Document partagé',
    description: 'Vous avez partagé un document avec un contact',
    time: 'Il y a 3 jours',
    icon: '📤',
    iconBg: 'bg-indigo-100'
  }
])
</script>
