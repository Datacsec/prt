<template>
  <div class="flex h-screen bg-gray-50">
    <!-- Sidebar Menu -->
    <SidebarMenu />
    
    <!-- Contenu principal -->
    <main class="flex-1 overflow-y-auto">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import SidebarMenu from './components/SidebarMenu.vue'
</script>
