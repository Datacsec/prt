import { createApp } from 'vue'
import { createRouter, createWebHistory } from 'vue-router'
import App from './App.vue'
import './style.css'

// Import des pages
import DefaultPage from './pages/DefaultPage.vue'
import DownloadPage from './pages/DownloadPage.vue'
import HistoriquePage from './pages/HistoriquePage.vue'
import AssurancePage from './pages/AssurancePage.vue'
import BanquePage from './pages/BanquePage.vue'

// Configuration des routes
const routes = [
  {
    path: '/',
    name: 'default',
    component: DefaultPage
  },
  {
    path: '/download',
    name: 'download',
    component: DownloadPage
  },
  {
    path: '/historique',
    name: 'historique',
    component: HistoriquePage
  },
  {
    path: '/assurance',
    name: 'assurance',
    component: AssurancePage
  },
  {
    path: '/banque',
    name: 'banque',
    component: BanquePage
  }
]

// Création du router
const router = createRouter({
  history: createWebHistory(),
  routes
})

// Création de l'application
const app = createApp(App)
app.use(router)
app.mount('#app')
