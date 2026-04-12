<template>
  <PageShell title="Paramètres — Utilisateurs" subtitle="Gestion des utilisateurs et des accès">
  <div class="space-y-6">
    <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
      <p class="text-sm text-gray-600 max-w-xl">
        Gérez les comptes : UID, nom, rôles et statut. Les données sont locales au navigateur (démo).
      </p>
      <button
        type="button"
        class="inline-flex items-center justify-center rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white shadow-sm hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 shrink-0"
        @click="openAddModal"
      >
        <svg class="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
        </svg>
        Ajouter une personne
      </button>
    </div>

    <div class="overflow-hidden rounded-lg border border-gray-200 shadow-sm">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 text-sm">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-4 py-3 text-left font-semibold text-gray-700">Utilisateur</th>
              <th scope="col" class="px-4 py-3 text-left font-semibold text-gray-700">Rôles</th>
              <th scope="col" class="px-4 py-3 text-left font-semibold text-gray-700">Statut</th>
              <th scope="col" class="px-4 py-3 text-right font-semibold text-gray-700 whitespace-nowrap">
                Actions
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 bg-white">
            <tr v-if="users.length === 0">
              <td colspan="4" class="px-4 py-12 text-center text-gray-500">
                Aucun utilisateur. Cliquez sur « Ajouter une personne » pour commencer.
              </td>
            </tr>
            <tr
              v-for="u in users"
              :key="u.uid"
              class="hover:bg-gray-50/80"
            >
              <td class="px-4 py-3">
                <div class="flex items-center gap-3 min-w-0">
                  <div
                    :class="[
                      'flex h-10 w-10 shrink-0 items-center justify-center rounded-full text-xs font-semibold shadow-sm ring-2 ring-white',
                      avatarRingClass(u.uid)
                    ]"
                    :aria-hidden="true"
                  >
                    {{ initials(u.name) }}
                  </div>
                  <div class="min-w-0">
                    <p class="font-medium text-gray-900 truncate">{{ u.name }}</p>
                    <p class="font-mono text-xs text-gray-500 truncate" :title="u.uid">{{ u.uid }}</p>
                  </div>
                </div>
              </td>
              <td class="px-4 py-3">
                <div class="flex flex-wrap gap-1.5">
                  <span
                    v-for="role in u.roles"
                    :key="role"
                    class="inline-flex rounded-full bg-gray-100 px-2.5 py-0.5 text-xs font-medium text-gray-700"
                  >
                    {{ role }}
                  </span>
                </div>
              </td>
              <td class="px-4 py-3 whitespace-nowrap">
                <span
                  :class="[
                    'inline-flex rounded-full px-2.5 py-0.5 text-xs font-semibold',
                    u.status === 'active'
                      ? 'bg-emerald-100 text-emerald-800'
                      : 'bg-red-100 text-red-800'
                  ]"
                >
                  {{ u.status === 'active' ? 'Actif' : 'Bloqué' }}
                </span>
              </td>
              <td class="px-4 py-3 text-right whitespace-nowrap">
                <div class="inline-flex items-center gap-1">
                  <button
                    type="button"
                    :class="[
                      'inline-flex h-9 w-9 items-center justify-center rounded-full transition-colors',
                      u.status === 'active'
                        ? 'text-amber-600 hover:bg-amber-50 hover:text-amber-800'
                        : 'text-emerald-600 hover:bg-emerald-50 hover:text-emerald-800'
                    ]"
                    :title="u.status === 'active' ? 'Bloquer' : 'Débloquer'"
                    :aria-label="u.status === 'active' ? 'Bloquer l’utilisateur' : 'Débloquer l’utilisateur'"
                    @click="toggleBlock(u)"
                  >
                    <!-- cadenas fermé = bloquer, ouvert = débloquer -->
                    <svg
                      v-if="u.status === 'active'"
                      class="h-5 w-5"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"
                      />
                    </svg>
                    <svg
                      v-else
                      class="h-5 w-5"
                      fill="none"
                      stroke="currentColor"
                      viewBox="0 0 24 24"
                    >
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M16.5 10.5V6.75a4.5 4.5 0 10-9 0v3.75m-.75 11.25h10.5a2.25 2.25 0 002.25-2.25v-6.75a2.25 2.25 0 00-2.25-2.25H6.75a2.25 2.25 0 00-2.25 2.25v6.75a2.25 2.25 0 002.25 2.25z"
                      />
                    </svg>
                  </button>
                  <button
                    type="button"
                    class="inline-flex h-9 w-9 items-center justify-center rounded-full text-red-600 transition-colors hover:bg-red-50 hover:text-red-800"
                    title="Supprimer"
                    aria-label="Supprimer l’utilisateur"
                    @click="removeUser(u)"
                  >
                    <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path
                        stroke-linecap="round"
                        stroke-linejoin="round"
                        stroke-width="2"
                        d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                      />
                    </svg>
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
  </PageShell>

  <!-- Modal ajout -->
  <Teleport to="body">
      <div
        v-if="addOpen"
        class="fixed inset-0 z-50 flex items-end justify-center sm:items-center p-4 bg-gray-900/50"
        role="dialog"
        aria-modal="true"
        aria-labelledby="add-user-title"
        @click.self="closeAddModal"
      >
        <div
          class="w-full max-w-md rounded-xl bg-white shadow-xl border border-gray-200"
          @click.stop
        >
          <div class="border-b border-gray-100 px-5 py-4">
            <h3 id="add-user-title" class="text-lg font-semibold text-gray-900">
              Ajouter une personne
            </h3>
            <p class="mt-1 text-sm text-gray-500">Renseignez le nom et les rôles. L’UID sera généré automatiquement.</p>
          </div>
          <form class="px-5 py-4 space-y-4" @submit.prevent="submitAdd">
            <div>
              <label for="new-name" class="block text-sm font-medium text-gray-700 mb-1">Nom</label>
              <input
                id="new-name"
                v-model.trim="form.name"
                type="text"
                required
                autocomplete="name"
                class="w-full rounded-lg border border-gray-300 px-3 py-2 text-sm shadow-sm focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none"
                placeholder="Ex. Marie Martin"
              />
            </div>
            <div>
              <span class="block text-sm font-medium text-gray-700 mb-2">Rôles</span>
              <div class="space-y-2">
                <label
                  v-for="opt in roleOptions"
                  :key="opt"
                  class="flex items-center gap-2 cursor-pointer"
                >
                  <input
                    v-model="form.roles"
                    type="checkbox"
                    :value="opt"
                    class="rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                  />
                  <span class="text-sm text-gray-700">{{ opt }}</span>
                </label>
              </div>
              <p v-if="form.roles.length === 0" class="mt-1 text-xs text-amber-600">Sélectionnez au moins un rôle.</p>
            </div>
            <div class="flex justify-end gap-2 pt-2">
              <button
                type="button"
                class="rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
                @click="closeAddModal"
              >
                Annuler
              </button>
              <button
                type="submit"
                :disabled="!form.name || form.roles.length === 0"
                class="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Créer
              </button>
            </div>
          </form>
        </div>
      </div>
  </Teleport>
</template>

<script>
import { ref } from 'vue'
import PageShell from './PageShell.vue'

let uidCounter = 100

const AVATAR_GRADIENTS = [
  'bg-gradient-to-br from-sky-400 to-blue-600 text-white',
  'bg-gradient-to-br from-violet-400 to-purple-600 text-white',
  'bg-gradient-to-br from-emerald-400 to-teal-600 text-white',
  'bg-gradient-to-br from-amber-400 to-orange-600 text-white',
  'bg-gradient-to-br from-rose-400 to-pink-600 text-white',
  'bg-gradient-to-br from-slate-500 to-slate-700 text-white'
]

const hashUid = (uid) => {
  let h = 0
  for (let i = 0; i < uid.length; i += 1) h = (h * 31 + uid.charCodeAt(i)) >>> 0
  return h
}

const initialUsers = () => [
  {
    uid: 'usr_a1b2c3d4',
    name: 'Jean Dupont',
    roles: ['admin', 'utilisateur'],
    status: 'active'
  },
  {
    uid: 'usr_e5f6g7h8',
    name: 'Sophie Leroy',
    roles: ['lecture seule'],
    status: 'active'
  },
  {
    uid: 'usr_i9j0k1l2',
    name: 'Marc Bernard',
    roles: ['utilisateur'],
    status: 'blocked'
  }
]

export default {
  name: 'Parametres',
  components: { PageShell },
  setup() {
    const users = ref(initialUsers())
    const addOpen = ref(false)
    const roleOptions = ['admin', 'utilisateur', 'lecture seule']
    const form = ref({
      name: '',
      roles: ['utilisateur']
    })

    const genUid = () => {
      uidCounter += 1
      return `usr_${Date.now().toString(36)}_${uidCounter}`
    }

    const openAddModal = () => {
      form.value = { name: '', roles: ['utilisateur'] }
      addOpen.value = true
    }

    const closeAddModal = () => {
      addOpen.value = false
    }

    const submitAdd = () => {
      if (!form.value.name || form.value.roles.length === 0) return
      users.value = [
        ...users.value,
        {
          uid: genUid(),
          name: form.value.name,
          roles: [...form.value.roles],
          status: 'active'
        }
      ]
      closeAddModal()
    }

    const toggleBlock = (u) => {
      users.value = users.value.map((x) =>
        x.uid === u.uid
          ? { ...x, status: x.status === 'active' ? 'blocked' : 'active' }
          : x
      )
    }

    const removeUser = (u) => {
      if (!confirm(`Supprimer définitivement ${u.name} (${u.uid}) ?`)) return
      users.value = users.value.filter((x) => x.uid !== u.uid)
    }

    const initials = (name) => {
      const parts = name.trim().split(/\s+/).filter(Boolean)
      if (parts.length >= 2) {
        return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase()
      }
      if (parts.length === 1 && parts[0].length >= 2) {
        return parts[0].substring(0, 2).toUpperCase()
      }
      return (parts[0] || '?').substring(0, 2).toUpperCase()
    }

    const avatarRingClass = (uid) => AVATAR_GRADIENTS[hashUid(uid) % AVATAR_GRADIENTS.length]

    return {
      users,
      addOpen,
      form,
      roleOptions,
      openAddModal,
      closeAddModal,
      submitAdd,
      toggleBlock,
      removeUser,
      initials,
      avatarRingClass
    }
  }
}
</script>
