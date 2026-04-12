<template>
  <div class="space-y-4">
    <p class="text-sm text-gray-600">
      Synthèse des soldes par compte (données de démonstration).
    </p>

    <div class="overflow-hidden rounded-lg border border-gray-200 shadow-sm">
      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200 text-sm">
          <thead class="bg-gray-50">
            <tr>
              <th scope="col" class="px-4 py-3 text-left font-semibold text-gray-700 w-16">
                Compte
              </th>
              <th scope="col" class="px-4 py-3 text-left font-semibold text-gray-700">
                Nom du compte
              </th>
              <th scope="col" class="px-4 py-3 text-left font-semibold text-gray-700">
                Nom du poste
              </th>
              <th scope="col" class="px-4 py-3 text-right font-semibold text-gray-700 whitespace-nowrap">
                Montant
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 bg-white">
            <tr
              v-for="row in rows"
              :key="row.code"
              class="hover:bg-gray-50/80"
            >
              <td class="px-4 py-3">
                <span
                  class="inline-flex h-8 w-8 items-center justify-center rounded-full bg-blue-100 text-xs font-bold text-blue-800"
                >
                  {{ row.code }}
                </span>
              </td>
              <td class="px-4 py-3 font-medium text-gray-900">{{ row.accountName }}</td>
              <td class="px-4 py-3 text-gray-700">{{ row.poste }}</td>
              <td class="px-4 py-3 text-right font-mono tabular-nums text-gray-900">
                {{ formatMoney(row.amount) }}
              </td>
            </tr>
          </tbody>
          <tfoot class="border-t-2 border-gray-300 bg-gray-50">
            <tr>
              <td colspan="3" class="px-4 py-3 text-right font-semibold text-gray-900">
                Total
              </td>
              <td class="px-4 py-3 text-right font-mono tabular-nums text-base font-bold text-blue-800">
                {{ formatMoney(totalAmount) }}
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref } from 'vue'

export default {
  name: 'BalanceBanque',
  setup() {
    const rows = ref([
      { code: 'A', accountName: 'Compte courant principal', poste: 'Siège — Trésorerie', amount: 12_450.3 },
      { code: 'B', accountName: 'Compte épargne entreprise', poste: 'Réserves — Provisions', amount: 48_200.0 },
      { code: 'C', accountName: 'Compte opérationnel filiale Nord', poste: 'Lille — Exploitation', amount: 6_780.55 },
      { code: 'D', accountName: 'Compte projet investissement', poste: 'CAPEX — Immobilier', amount: 125_000.0 },
      { code: 'E', accountName: 'Compte TVA / charges sociales', poste: 'Comptabilité — Obligations', amount: 3_215.12 }
    ])

    const totalAmount = computed(() =>
      rows.value.reduce((sum, r) => sum + r.amount, 0)
    )

    const formatMoney = (value) =>
      new Intl.NumberFormat('fr-FR', {
        style: 'currency',
        currency: 'EUR'
      }).format(value)

    return {
      rows,
      totalAmount,
      formatMoney
    }
  }
}
</script>
