<script setup>
import { ref, computed, onMounted } from 'vue'

const emit = defineEmits(['saved', 'cancel'])

// ---- Formulaire ----
const form = ref({
  controleId: '',
  title: '',
  description: '',
  nbJoursApresArret: 0,
  frequence: 'Monthly',
})
const frequences = ['Daily', 'Weekly', 'Monthly', 'Quarterly', 'Semi-annual', 'Yearly']

// ---- Départements ----
const allDepartments = ref([])
const direction = ref('')
const departement = ref('')
const selectedIdentities = ref(new Set())

const error = ref('')
const loading = ref(false)

// Adapte ici si tu as déjà un client axios ou un wrapper avec le JWT
async function request(url, options = {}) {
  const token = localStorage.getItem('token') // adapte au nom que tu utilises
  const res = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  })
  if (!res.ok) {
    const body = await res.json().catch(() => null)
    throw new Error(body?.message || `Erreur ${res.status}`)
  }
  return res.status === 204 ? null : res.json()
}

onMounted(async () => {
  try {
    allDepartments.value = await request('/api/departments')
  } catch (e) {
    error.value = 'Impossible de charger les départements'
  }
})

const directions = computed(() =>
  [...new Set(allDepartments.value.map((d) => d.direction))].sort()
)

const departements = computed(() =>
  [...new Set(
    allDepartments.value
      .filter((d) => d.direction === direction.value)
      .map((d) => d.departement)
  )].sort()
)

const entitiesRows = computed(() =>
  allDepartments.value.filter(
    (d) => d.direction === direction.value && d.departement === departement.value
  )
)

function onDirectionChange() {
  departement.value = ''
}

function toggle(identity) {
  const s = new Set(selectedIdentities.value)
  s.has(identity) ? s.delete(identity) : s.add(identity)
  selectedIdentities.value = s
}

function selectAllVisible() {
  const s = new Set(selectedIdentities.value)
  entitiesRows.value.forEach((d) => s.add(d.identity))
  selectedIdentities.value = s
}

function remove(identity) {
  const s = new Set(selectedIdentities.value)
  s.delete(identity)
  selectedIdentities.value = s
}

// Pour afficher les chips des éléments sélectionnés
const selectedRows = computed(() =>
  allDepartments.value.filter((d) => selectedIdentities.value.has(d.identity))
)

const isValid = computed(
  () =>
    form.value.controleId.trim() &&
    form.value.title.trim() &&
    form.value.frequence &&
    Number.isInteger(Number(form.value.nbJoursApresArret)) &&
    selectedIdentities.value.size > 0
)

async function submit() {
  error.value = ''
  if (!isValid.value) {
    error.value = 'Veuillez remplir les champs obligatoires et choisir au moins une entité.'
    return
  }
  loading.value = true
  try {
    const payload = {
      controleId: form.value.controleId.trim(),
      title: form.value.title.trim(),
      description: form.value.description.trim() || null,
      nbJoursApresArret: Number(form.value.nbJoursApresArret),
      frequence: form.value.frequence,
      departmentIdentities: [...selectedIdentities.value],
    }
    const created = await request('/api/controles', {
      method: 'POST',
      body: JSON.stringify(payload),
    })
    emit('saved', created)
  } catch (e) {
    error.value = e.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <form class="space-y-6" @submit.prevent="submit">
    <div class="flex items-center justify-between">
      <h2 class="text-lg font-semibold text-slate-800">Ajouter un contrôle</h2>
      <button type="button" class="text-sm text-slate-500 hover:text-slate-800" @click="emit('cancel')">
        ← Retour à la liste
      </button>
    </div>

    <!-- Infos du contrôle -->
    <div class="grid gap-4 rounded-xl border border-slate-200 bg-white p-5 sm:grid-cols-2">
      <div>
        <label class="mb-1 block text-sm font-medium text-slate-700">Identifiant du contrôle *</label>
        <input v-model="form.controleId" type="text" placeholder="L1_A.1_Actuals_Monthly_BMCI"
          class="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none" />
      </div>

      <div>
        <label class="mb-1 block text-sm font-medium text-slate-700">Titre *</label>
        <input v-model="form.title" type="text"
          class="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none" />
      </div>

      <div class="sm:col-span-2">
        <label class="mb-1 block text-sm font-medium text-slate-700">Description</label>
        <textarea v-model="form.description" rows="3"
          class="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none" />
      </div>

      <div>
        <label class="mb-1 block text-sm font-medium text-slate-700">Fréquence *</label>
        <select v-model="form.frequence"
          class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-blue-500 focus:outline-none">
          <option v-for="f in frequences" :key="f" :value="f">{{ f }}</option>
        </select>
      </div>

      <div>
        <label class="mb-1 block text-sm font-medium text-slate-700">Jours par rapport à l'arrêté *</label>
        <input v-model.number="form.nbJoursApresArret" type="number" step="1"
          class="w-full rounded-lg border border-slate-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none" />
        <p class="mt-1 text-xs text-slate-500">
          Négatif : à remplir avant l'arrêté (ex. -3). Positif : peut être rempli après (ex. 5).
        </p>
      </div>
    </div>

    <!-- Périmètre -->
    <div class="space-y-4 rounded-xl border border-slate-200 bg-white p-5">
      <h3 class="text-sm font-semibold text-slate-800">Périmètre *</h3>

      <div class="grid gap-4 sm:grid-cols-2">
        <div>
          <label class="mb-1 block text-sm font-medium text-slate-700">Direction</label>
          <select v-model="direction" @change="onDirectionChange"
            class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-blue-500 focus:outline-none">
            <option value="" disabled>Choisir une direction</option>
            <option v-for="d in directions" :key="d" :value="d">{{ d }}</option>
          </select>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-slate-700">Département</label>
          <select v-model="departement" :disabled="!direction"
            class="w-full rounded-lg border border-slate-300 bg-white px-3 py-2 text-sm focus:border-blue-500 focus:outline-none disabled:bg-slate-100">
            <option value="" disabled>Choisir un département</option>
            <option v-for="d in departements" :key="d" :value="d">{{ d }}</option>
          </select>
        </div>
      </div>

      <div v-if="entitiesRows.length">
        <div class="mb-2 flex items-center justify-between">
          <label class="text-sm font-medium text-slate-700">Entités</label>
          <button type="button" class="text-xs text-blue-600 hover:underline" @click="selectAllVisible">
            Tout sélectionner
          </button>
        </div>
        <div class="grid gap-2 sm:grid-cols-2">
          <label v-for="d in entitiesRows" :key="d.identity"
            class="flex cursor-pointer items-center gap-2 rounded-lg border border-slate-200 px-3 py-2 text-sm hover:bg-slate-50">
            <input type="checkbox" :checked="selectedIdentities.has(d.identity)" @change="toggle(d.identity)" />
            <span>{{ d.entity }}</span>
          </label>
        </div>
      </div>

      <!-- Sélection globale -->
      <div v-if="selectedRows.length">
        <p class="mb-2 text-xs font-medium uppercase text-slate-500">
          Sélection ({{ selectedRows.length }})
        </p>
        <div class="flex flex-wrap gap-2">
          <span v-for="d in selectedRows" :key="d.identity"
            class="inline-flex items-center gap-1 rounded-full bg-blue-50 px-3 py-1 text-xs text-blue-700">
            {{ d.direction }} / {{ d.departement }} / {{ d.entity }}
            <button type="button" class="font-bold hover:text-blue-900" @click="remove(d.identity)">×</button>
          </span>
        </div>
      </div>
    </div>

    <p v-if="error" class="rounded-lg bg-red-50 px-4 py-2 text-sm text-red-700">{{ error }}</p>

    <div class="flex justify-end gap-3">
      <button type="button" class="rounded-lg border border-slate-300 px-4 py-2 text-sm hover:bg-slate-50"
        @click="emit('cancel')">
        Annuler
      </button>
      <button type="submit" :disabled="loading || !isValid"
        class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-50">
        {{ loading ? 'Enregistrement...' : 'Enregistrer' }}
      </button>
    </div>
  </form>
</template>
