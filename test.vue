<script setup>
import { ref, computed } from 'vue'

// ---- Données (à remplacer par votre API) ----
const controls = ref([
  {
    id: 'L1_A.1_Actuals_Monthly_BMCI',
    level: 1,
    name: 'A.1. Confirmer que le périmètre couvert dans le reporting Liquidité / Liasse liquidité correspond à l’ensemble des entités recensées dans le LROD (Liquidity Reporting Operational Documentation) et au périmètre de responsabilité tel que défini par le Groupe',
    region: 'IRB',
    regime: 'LIQUIDITY',
    fonction: 'LOD1_LIQ_BMCI',
    dept: 'LOD1_LIQ_BMCI',
    frequency: 'Monthly',
    closing: 'Jan-2024',
    status: 'Pending',
    dueStatus: 'DUE',
    lastDueDate: '2024-01-31',
    rating: '',
    roles: {
      'Performer Preparer': '—',
      'Performer Approver': '—',
      'Owner Reviewer': '—',
      'Owner Approver': '—',
      Assignee: 'h23676',
      'Actioned by': '—',
    },
    objectives:
      'A.1. Confirmer que le périmètre couvert dans le reporting Liquidité / Liasse liquidité correspond à l’ensemble des entités recensées dans le LROD (Liquidity Reporting Operational Documentation) et au périmètre de responsabilité tel que défini par le Groupe.',
    description:
      'A.1. Le périmètre des entités couvertes dans le reporting Liquidité / Liasse Liquidité correspond aux entités recensées dans le LROD et au périmètre de responsabilité tel que défini par le Groupe (notamment prise en compte des mouvements de la période : entrées et sorties, changement de méthode de consolidation…). Si des entités du périmètre de responsabilité envoient directement leurs entités au niveau du Groupe, un dispositif de suivi doit être mis en place.',
    entities: [{ name: 'BANQUE MAROCAINE POUR LE COMMERCE ET L’INDUSTRIE', matisse: '22626' }],
    audit: [
      { date: '05-FEB-24 05:09', user: 'AUTOMATIC', event: 'THE CONTROL IS NOW PENDING' },
      { date: '05-FEB-24 05:09', user: 'AUTOMATIC', event: 'EMAIL NOTIFICATIONS SENT AS CONTROL IS PENDING' },
    ],
    comments: [],
  },
  {
    id: 'L1_A.2_Actuals_Monthly_BMCI',
    level: 1,
    name: 'A.2. Vérifier la cohérence des données sources avec la balance comptable',
    region: 'IRB',
    regime: 'LIQUIDITY',
    fonction: 'LOD1_LIQ_BMCI',
    dept: 'LOD1_LIQ_BMCI',
    frequency: 'Monthly',
    closing: 'Jan-2024',
    status: 'Approved',
    dueStatus: 'DONE',
    lastDueDate: '2024-01-31',
    rating: 'Satisfactory',
    roles: { 'Performer Preparer': 'h20418', 'Performer Approver': 'h31902', 'Owner Reviewer': '—', 'Owner Approver': '—', Assignee: 'h20418', 'Actioned by': 'h31902' },
    objectives: 'S’assurer que les données sources du reporting sont complètes et rapprochées de la comptabilité.',
    description: 'Rapprochement mensuel entre les données sources et la balance comptable, avec justification des écarts supérieurs au seuil défini.',
    entities: [{ name: 'BANQUE MAROCAINE POUR LE COMMERCE ET L’INDUSTRIE', matisse: '22626' }],
    audit: [{ date: '02-FEB-24 10:41', user: 'h31902', event: 'THE CONTROL WAS APPROVED' }],
    comments: [],
  },
  {
    id: 'L1_B.1_Actuals_Quarterly_BMCI',
    level: 1,
    name: 'B.1. Valider les hypothèses de stress appliquées au calcul du ratio de liquidité',
    region: 'IRB',
    regime: 'LIQUIDITY',
    fonction: 'LOD1_LIQ_BMCI',
    dept: 'LOD1_LIQ_BMCI',
    frequency: 'Quarterly',
    closing: 'Dec-2023',
    status: 'Reopened',
    dueStatus: 'OVERDUE',
    lastDueDate: '2023-12-29',
    rating: '',
    roles: { 'Performer Preparer': 'h27755', 'Performer Approver': '—', 'Owner Reviewer': '—', 'Owner Approver': '—', Assignee: 'h27755', 'Actioned by': 'h15507' },
    objectives: 'Garantir que les hypothèses de stress sont documentées, approuvées et conformes au cadre Groupe.',
    description: 'Revue trimestrielle des hypothèses de stress et de leur documentation, avec validation formelle du responsable.',
    entities: [{ name: 'BANQUE MAROCAINE POUR LE COMMERCE ET L’INDUSTRIE', matisse: '22626' }],
    audit: [{ date: '08-JAN-24 15:20', user: 'h15507', event: 'THE CONTROL WAS RETURNED / RE-OPENED' }],
    comments: [{ author: 'h15507', date: '08-JAN-24 15:20', text: 'Hypothèses incomplètes, merci de compléter la documentation.' }],
  },
])

// ---- État ----
const search = ref('')
const statusFilter = ref('Tous')
const selectedId = ref(null)
const newComment = ref('')
const statuses = ['Tous', 'Pending', 'Approved', 'Reopened']

const filtered = computed(() => {
  const q = search.value.trim().toLowerCase()
  return controls.value.filter(
    (c) =>
      (statusFilter.value === 'Tous' || c.status === statusFilter.value) &&
      (!q || c.id.toLowerCase().includes(q) || c.name.toLowerCase().includes(q)),
  )
})

const selected = computed(() => controls.value.find((c) => c.id === selectedId.value) || null)

// ---- Aides d'affichage ----
const statusClass = (s) =>
  ({
    Pending: 'bg-amber-50 text-amber-700',
    Approved: 'bg-emerald-50 text-emerald-700',
    Reopened: 'bg-rose-50 text-rose-700',
  })[s] || 'bg-slate-100 text-slate-600'

const dueClass = (s) =>
  ({ DUE: 'text-amber-700', DONE: 'text-emerald-700', OVERDUE: 'text-rose-600' })[s] || 'text-slate-700'

function stamp() {
  const d = new Date()
  const m = ['JAN', 'FEB', 'MAR', 'APR', 'MAY', 'JUN', 'JUL', 'AUG', 'SEP', 'OCT', 'NOV', 'DEC'][d.getMonth()]
  const p = (n) => String(n).padStart(2, '0')
  return p(d.getDate()) + '-' + m + '-' + String(d.getFullYear()).slice(2) + ' ' + p(d.getHours()) + ':' + p(d.getMinutes())
}

// ---- Actions ----
const open = (id) => (selectedId.value = id)
const back = () => (selectedId.value = null)

function approve() {
  const c = selected.value
  c.status = 'Approved'
  c.dueStatus = 'DONE'
  c.audit.unshift({ date: stamp(), user: 'admin', event: 'THE CONTROL WAS APPROVED' })
}

function reopen() {
  const c = selected.value
  c.status = 'Reopened'
  c.dueStatus = 'DUE'
  c.audit.unshift({ date: stamp(), user: 'admin', event: 'THE CONTROL WAS RETURNED / RE-OPENED' })
}

function addComment() {
  const text = newComment.value.trim()
  if (!text) return
  selected.value.comments.unshift({ author: 'admin', date: stamp(), text })
  newComment.value = ''
}
</script>

<template>
  <div class="flex min-h-screen bg-slate-50 text-slate-900">
    <!-- Barre latérale -->
    <aside class="hidden w-16 shrink-0 flex-col items-center gap-7 border-r border-slate-200 bg-white py-5 sm:flex">
      <div class="flex h-11 w-11 items-center justify-center rounded-lg bg-emerald-700 text-white">
        <svg class="h-6 w-6" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2l2.4 6.9H22l-6 4.4 2.3 7L12 16l-6.3 4.3 2.3-7-6-4.4h7.6z" /></svg>
      </div>
      <nav class="flex flex-1 flex-col items-center gap-6 text-slate-500">
        <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path d="M12 19V5M5 12l7-7 7 7" /></svg>
        <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path d="M12 3v18M5 7h14M5 7l-3 7h6zM19 7l-3 7h6z" /></svg>
        <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path d="M12 4v11m-5-4 5 5 5-5M5 20h14" /></svg>
        <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9" /><path d="M12 7v5l3 2" /></svg>
        <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path d="M3 17l6-6 4 4 8-8M15 7h6v6" /></svg>
        <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><rect x="3" y="5" width="18" height="14" rx="2" /><path d="M3 10h18" /></svg>
        <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path d="M3 7a2 2 0 012-2h4l2 2h8a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2z" /></svg>
        <svg class="h-5 w-5 text-slate-900" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path d="M4 7h10M18 7h2M4 17h2M10 17h10M14 4v6M8 14v6" /></svg>
        <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path d="M12 3l8 3v6c0 5-3.5 8-8 9-4.5-1-8-4-8-9V6z" /><path d="M9 12l2 2 4-4" /></svg>
      </nav>
      <div class="flex flex-col items-center gap-5">
        <span class="text-sm font-semibold text-slate-700">OE</span>
        <button class="rounded-lg bg-rose-50 p-2 text-rose-500 hover:bg-rose-100" aria-label="Se déconnecter">
          <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path d="M9 4H5a2 2 0 00-2 2v12a2 2 0 002 2h4M16 8l4 4-4 4M20 12H9" /></svg>
        </button>
      </div>
    </aside>

    <main class="min-w-0 flex-1 px-5 py-8 sm:px-10">
      <header>
        <h1 class="text-3xl font-bold tracking-tight">Paramètres — BEACON APP</h1>
        <p class="mt-1 text-sm text-slate-500">Gestion des paramètres de l’application BEACON</p>
      </header>

      <section class="mt-8 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm sm:p-7">
        <!-- Onglets -->
        <div class="flex flex-wrap items-center justify-between gap-3">
          <div class="flex flex-wrap gap-3">
            <button class="rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-800 hover:bg-slate-50">Utilisateurs</button>
            <button class="rounded-lg border border-slate-300 bg-white px-4 py-2 text-sm font-medium text-slate-800 hover:bg-slate-50">Departements</button>
            <button class="rounded-lg border border-slate-900 bg-slate-900 px-4 py-2 text-sm font-semibold text-white">Controles</button>
          </div>
          <button v-if="!selected" class="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-blue-700">
            <span class="text-lg leading-none">+</span> Ajouter un contrôle
          </button>
        </div>

        <!-- ============ LISTE ============ -->
        <template v-if="!selected">
          <p class="mt-5 text-sm text-slate-500">Gérer les contrôles : titre, description, périmètre et statut.</p>

          <div class="mt-4 flex flex-wrap items-center gap-3">
            <div class="relative w-full max-w-xs">
              <svg class="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><circle cx="11" cy="11" r="7" /><path d="M20 20l-3.5-3.5" /></svg>
              <input
                v-model="search"
                type="search"
                placeholder="Rechercher un contrôle"
                class="w-full rounded-lg border border-slate-300 bg-white py-2 pl-9 pr-3 text-sm placeholder:text-slate-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
              />
            </div>
            <div class="flex flex-wrap gap-2">
              <button
                v-for="s in statuses"
                :key="s"
                class="rounded-lg border px-3 py-2 text-sm"
                :class="statusFilter === s ? 'border-slate-900 bg-slate-900 font-semibold text-white' : 'border-slate-300 text-slate-700 hover:bg-slate-50'"
                @click="statusFilter = s"
              >{{ s }}</button>
            </div>
          </div>

          <div class="mt-5 overflow-x-auto rounded-xl border border-slate-200">
            <table class="w-full min-w-[820px] text-left text-sm">
              <thead class="bg-slate-50 text-slate-600">
                <tr>
                  <th class="px-5 py-3 font-semibold">Contrôle</th>
                  <th class="px-4 py-3 font-semibold">Fréquence</th>
                  <th class="px-4 py-3 font-semibold">Clôture</th>
                  <th class="px-4 py-3 font-semibold">Fonction</th>
                  <th class="px-4 py-3 font-semibold">Statut</th>
                  <th class="px-5 py-3 text-right font-semibold">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-100">
                <tr v-for="c in filtered" :key="c.id" class="cursor-pointer hover:bg-slate-50/70" @click="open(c.id)">
                  <td class="max-w-md px-5 py-3.5">
                    <p class="font-medium text-slate-800">{{ c.id }}</p>
                    <p class="mt-0.5 line-clamp-2 text-xs text-slate-500">{{ c.name }}</p>
                  </td>
                  <td class="px-4 py-3.5 text-slate-700">{{ c.frequency }}</td>
                  <td class="px-4 py-3.5 text-slate-700">{{ c.closing }}</td>
                  <td class="px-4 py-3.5 text-slate-700">{{ c.fonction }}</td>
                  <td class="px-4 py-3.5">
                    <span class="rounded-full px-2.5 py-1 text-xs font-medium" :class="statusClass(c.status)">{{ c.status }}</span>
                  </td>
                  <td class="px-5 py-3.5">
                    <div class="flex items-center justify-end gap-3 text-slate-500" @click.stop>
                      <button class="hover:text-slate-900" aria-label="Voir" @click="open(c.id)">
                        <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z" /><circle cx="12" cy="12" r="3" /></svg>
                      </button>
                      <button class="text-rose-500 hover:text-rose-700" aria-label="Supprimer">
                        <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path d="M4 7h16M10 11v6M14 11v6M6 7l1 12a2 2 0 002 2h6a2 2 0 002-2l1-12M9 7V4h6v3" /></svg>
                      </button>
                      <button class="text-blue-600 hover:text-blue-800" aria-label="Modifier">
                        <svg class="h-5 w-5" fill="none" stroke="currentColor" stroke-width="1.8" viewBox="0 0 24 24"><path d="M12 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-6M18.5 3.5a2.1 2.1 0 013 3L12 16l-4 1 1-4z" /></svg>
                      </button>
                    </div>
                  </td>
                </tr>
                <tr v-if="!filtered.length">
                  <td colspan="6" class="px-5 py-10 text-center text-slate-500">Aucun contrôle trouvé. Modifiez votre recherche ou le filtre.</td>
                </tr>
              </tbody>
            </table>
          </div>
        </template>

        <!-- ============ DÉTAIL ============ -->
        <template v-else>
          <button class="mt-5 inline-flex items-center gap-1 text-sm font-medium text-blue-600 hover:text-blue-800" @click="back">
            <svg class="h-4 w-4" fill="none" stroke="currentColor" stroke-width="2" viewBox="0 0 24 24"><path d="M15 6l-6 6 6 6" /></svg>
            Retour à la liste
          </button>

          <!-- Titre -->
          <div class="mt-3 flex flex-wrap items-end justify-between gap-3 border-b-2 border-slate-900 pb-3">
            <h2 class="text-2xl font-bold tracking-tight">Level {{ selected.level }} Control — {{ selected.id }}</h2>
            <div class="flex flex-wrap items-center gap-5 text-sm text-slate-600">
              <span>Closing : <strong class="text-slate-900">{{ selected.closing }}</strong></span>
              <span>Frequency : <strong class="text-slate-900">{{ selected.frequency }}</strong></span>
              <span class="rounded-full px-2.5 py-1 text-xs font-medium" :class="statusClass(selected.status)">{{ selected.status }}</span>
            </div>
          </div>

          <p class="mt-4 text-sm leading-relaxed text-slate-800"><span class="font-semibold">Control Name :</span> {{ selected.name }}</p>

          <!-- Méta + rôles + Quick view -->
          <div class="mt-5 grid gap-6 lg:grid-cols-3">
            <dl class="grid grid-cols-[auto_1fr] gap-x-4 gap-y-2 text-sm">
              <dt class="font-medium text-slate-500">Region</dt><dd>{{ selected.region }}</dd>
              <dt class="font-medium text-slate-500">Regime</dt><dd>{{ selected.regime }}</dd>
              <dt class="font-medium text-slate-500">Function</dt><dd>{{ selected.fonction }}</dd>
              <dt class="font-medium text-slate-500">Dept</dt><dd>{{ selected.dept }}</dd>
            </dl>

            <dl class="grid grid-cols-[auto_1fr] gap-x-4 gap-y-2 text-sm">
              <template v-for="(v, k) in selected.roles" :key="k">
                <dt class="font-medium text-slate-500">{{ k }}</dt><dd>{{ v }}</dd>
              </template>
            </dl>

            <div class="rounded-xl border border-slate-200 bg-slate-50 p-4">
              <p class="text-sm font-semibold underline decoration-slate-300 underline-offset-4">Quick View</p>
              <dl class="mt-3 grid grid-cols-[auto_1fr] items-center gap-x-4 gap-y-2 text-sm">
                <dt class="text-slate-500">Due Status</dt><dd class="font-semibold" :class="dueClass(selected.dueStatus)">{{ selected.dueStatus }}</dd>
                <dt class="text-slate-500">Last Due Date</dt><dd>{{ selected.lastDueDate }}</dd>
                <dt class="text-slate-500">Rating</dt>
                <dd>
                  <select v-model="selected.rating" class="w-full rounded-md border border-slate-300 bg-white px-2 py-1 text-sm focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200">
                    <option value="">—</option>
                    <option>Satisfactory</option>
                    <option>Needs improvement</option>
                    <option>Unsatisfactory</option>
                  </select>
                </dd>
              </dl>
              <div class="mt-4 flex gap-2">
                <button class="rounded-md bg-emerald-600 px-3 py-1.5 text-sm font-semibold text-white hover:bg-emerald-700" @click="approve">Approve</button>
                <button class="rounded-md bg-rose-500 px-3 py-1.5 text-sm font-semibold text-white hover:bg-rose-600" @click="reopen">Return / Re-open</button>
              </div>
              <p class="mt-3 text-xs text-slate-500">Attachments : 0 · Comments : {{ selected.comments.length }}</p>
            </div>
          </div>

          <!-- Control overview + Entities -->
          <div class="mt-6 grid gap-6 lg:grid-cols-3">
            <div class="overflow-hidden rounded-xl border border-slate-200 lg:col-span-2">
              <h3 class="bg-slate-900 px-4 py-2 text-sm font-semibold text-white">Control Overview</h3>
              <div class="grid gap-5 p-4 md:grid-cols-2">
                <div>
                  <h4 class="text-sm font-semibold">Control Objectives</h4>
                  <p class="mt-2 text-sm leading-relaxed text-slate-600">{{ selected.objectives }}</p>
                </div>
                <div>
                  <h4 class="text-sm font-semibold">Control Description</h4>
                  <p class="mt-2 max-h-48 overflow-y-auto text-sm leading-relaxed text-slate-600">{{ selected.description }}</p>
                </div>
              </div>
            </div>

            <div class="overflow-hidden rounded-xl border border-slate-200">
              <h3 class="bg-slate-900 px-4 py-2 text-sm font-semibold text-white">Entities / Matisse</h3>
              <table class="w-full text-left text-sm">
                <thead class="text-slate-600">
                  <tr><th class="px-4 py-2 font-semibold">Entity Names / Ref</th><th class="px-4 py-2 font-semibold">Matisse</th></tr>
                </thead>
                <tbody class="divide-y divide-slate-100">
                  <tr v-for="e in selected.entities" :key="e.matisse">
                    <td class="px-4 py-2 text-slate-700">{{ e.name }}</td>
                    <td class="px-4 py-2 text-slate-700">{{ e.matisse }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Audit trail -->
          <div class="mt-6 overflow-hidden rounded-xl border border-slate-200">
            <h3 class="bg-slate-900 px-4 py-2 text-sm font-semibold text-white">Audit Trail</h3>
            <div class="max-h-60 overflow-auto">
              <table class="w-full min-w-[560px] text-left text-sm">
                <thead class="bg-slate-50 text-slate-600">
                  <tr><th class="px-4 py-2 font-semibold">Date</th><th class="px-4 py-2 font-semibold">User</th><th class="px-4 py-2 font-semibold">Event</th></tr>
                </thead>
                <tbody class="divide-y divide-slate-100">
                  <tr v-for="(a, i) in selected.audit" :key="i">
                    <td class="whitespace-nowrap px-4 py-2 text-slate-600">{{ a.date }}</td>
                    <td class="px-4 py-2 text-slate-600">{{ a.user }}</td>
                    <td class="px-4 py-2 text-slate-600">{{ a.event }}</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          <!-- Commentaires -->
          <div class="mt-6 overflow-hidden rounded-xl border border-slate-200">
            <h3 class="bg-slate-900 px-4 py-2 text-sm font-semibold text-white">Comments and Attachments</h3>
            <div class="p-4">
              <div class="flex gap-2">
                <input
                  v-model="newComment"
                  type="text"
                  placeholder="Ajouter un commentaire"
                  class="flex-1 rounded-lg border border-slate-300 px-3 py-2 text-sm placeholder:text-slate-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-200"
                  @keyup.enter="addComment"
                />
                <button class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700" @click="addComment">Publier</button>
              </div>
              <ul class="mt-4 space-y-3">
                <li v-for="(c, i) in selected.comments" :key="i" class="rounded-lg bg-slate-50 px-3 py-2 text-sm">
                  <p class="text-xs text-slate-500">{{ c.author }} · {{ c.date }}</p>
                  <p class="mt-0.5 text-slate-800">{{ c.text }}</p>
                </li>
                <li v-if="!selected.comments.length" class="text-sm text-slate-500">Aucun commentaire pour ce contrôle.</li>
              </ul>
            </div>
          </div>
        </template>
      </section>
    </main>
  </div>
</template>
