M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z

<template>
  <div class="flex flex-col flex-1 min-h-0 w-full h-full">
    <!-- En-tête (remplace le titre de page App en mode plein écran) -->
    <header
      class="shrink-0 flex items-center gap-3 border-b border-gray-200 bg-white px-6 py-4"
    >
      <div
        class="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-violet-500 to-indigo-600 shadow-md shadow-indigo-500/20"
      >
        <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path
            stroke-linecap="round"
            stroke-linejoin="round"
            stroke-width="2"
            d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"
          />
        </svg>
      </div>
      <div class="min-w-0">
        <h1 class="text-lg font-semibold text-gray-900 truncate">Assistant IA</h1>
        <p class="text-xs text-gray-500 truncate">Démo frontend — réponses simulées dans le navigateur</p>
      </div>
    </header>

    <!-- Zone messages (scroll sur toute la hauteur restante) -->
    <div
      ref="scrollRoot"
      class="flex-1 min-h-0 overflow-y-auto bg-gray-50 px-6 py-6 space-y-4"
    >
      <div
        v-if="messages.length === 0"
        class="flex flex-col items-center justify-center text-center py-16 px-6 text-gray-500"
      >
        <div
          class="w-14 h-14 rounded-2xl bg-gradient-to-br from-violet-500 to-indigo-600 flex items-center justify-center mb-4 shadow-lg shadow-indigo-500/25"
        >
          <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="1.5"
              d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z"
            />
          </svg>
        </div>
        <p class="text-lg font-medium text-gray-800">Assistant IA</p>
        <p class="text-sm mt-2 max-w-sm">
          Posez une question ou décrivez ce dont vous avez besoin. Les réponses sont simulées côté navigateur (aucun appel serveur).
        </p>
      </div>

      <div
        v-for="msg in messages"
        :key="msg.id"
        :class="['flex', msg.role === 'user' ? 'justify-end' : 'justify-start']"
      >
        <div
          :class="[
            'max-w-[85%] rounded-2xl px-4 py-3 text-sm leading-relaxed shadow-sm',
            msg.role === 'user'
              ? 'bg-blue-600 text-white rounded-br-md'
              : 'bg-gray-100 text-gray-900 border border-gray-200 rounded-bl-md'
          ]"
        >
          <p class="whitespace-pre-wrap break-words">{{ msg.content }}</p>
          <p
            :class="[
              'text-[10px] mt-2 opacity-70',
              msg.role === 'user' ? 'text-blue-100' : 'text-gray-500'
            ]"
          >
            {{ formatTime(msg.at) }}
          </p>
        </div>
      </div>

      <div v-if="isTyping" class="flex justify-start">
        <div
          class="rounded-2xl rounded-bl-md border border-gray-200 bg-gray-50 px-4 py-3 flex items-center gap-1.5"
        >
          <span class="w-2 h-2 rounded-full bg-gray-400 animate-bounce" style="animation-delay: 0ms" />
          <span class="w-2 h-2 rounded-full bg-gray-400 animate-bounce" style="animation-delay: 150ms" />
          <span class="w-2 h-2 rounded-full bg-gray-400 animate-bounce" style="animation-delay: 300ms" />
        </div>
      </div>
    </div>

    <!-- Saisie : bandeau pleine largeur en bas -->
    <div class="shrink-0 border-t border-gray-200 bg-white px-6 py-4">
      <form class="flex gap-3 items-end max-w-5xl mx-auto w-full" @submit.prevent="sendMessage">
        <div class="flex-1 min-w-0">
          <label for="chat-input" class="sr-only">Message</label>
          <textarea
            id="chat-input"
            v-model="draft"
            rows="1"
            placeholder="Écrivez votre message…"
            class="w-full resize-none rounded-xl border border-gray-300 bg-gray-50 px-4 py-3 text-sm text-gray-900 placeholder-gray-400 shadow-sm focus:border-blue-500 focus:bg-white focus:ring-2 focus:ring-blue-500/20 outline-none transition min-h-[48px] max-h-32"
            :disabled="isTyping"
            @keydown.enter.exact.prevent="sendMessage"
          />
        </div>
        <button
          type="submit"
          :disabled="!draft.trim() || isTyping"
          class="shrink-0 inline-flex items-center justify-center rounded-xl bg-blue-600 px-6 py-3 text-sm font-medium text-white shadow-sm hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition"
        >
          Envoyer
        </button>
      </form>
      <p class="mt-2 text-xs text-gray-500 text-center max-w-5xl mx-auto">
        Branchez une API backend pour de vraies réponses IA.
      </p>
    </div>
  </div>
</template>

<script>
import { ref, watch, nextTick } from 'vue'

let idSeq = 0

const SIMULATED_REPLIES = [
  "Merci pour votre message. En production, cette réponse viendrait d’un modèle d’IA connecté à votre backend.",
  "Je note votre demande. Voici une réponse de démonstration : comment puis-je vous aider davantage sur le portail ?",
  "C’est une excellente question. Côté interface, tout est prêt : il reste à brancher l’endpoint de votre fournisseur d’IA.",
  "Voici un résumé fictif : votre message a été reçu et traité localement dans le navigateur, sans envoi de données.",
  "Pour aller plus loin, vous pourrez remplacer la fonction `simulateReply` par un appel `fetch` vers votre API."
]

export default {
  name: 'ChatIA',
  setup() {
    const messages = ref([])
    const draft = ref('')
    const isTyping = ref(false)
    const scrollRoot = ref(null)

    const formatTime = (d) =>
      new Intl.DateTimeFormat('fr-FR', {
        hour: '2-digit',
        minute: '2-digit'
      }).format(d)

    const scrollToBottom = async () => {
      await nextTick()
      const el = scrollRoot.value
      if (el) el.scrollTop = el.scrollHeight
    }

    watch(messages, () => scrollToBottom(), { deep: true })
    watch(isTyping, () => scrollToBottom())

    const simulateReply = (userText) => {
      const lower = userText.toLowerCase()
      if (lower.includes('bonjour') || lower.includes('salut')) {
        return "Bonjour ! Je suis l’assistant du portail (mode démo). Que souhaitez-vous faire aujourd’hui ?"
      }
      if (lower.includes('aide') || lower.includes('help')) {
        return "Vous pouvez poser des questions sur le portail, la navigation ou vos services. Connectez une API pour des réponses réelles."
      }
      const idx = Math.floor(Math.random() * SIMULATED_REPLIES.length)
      return SIMULATED_REPLIES[idx]
    }

    const sendMessage = () => {
      const text = draft.value.trim()
      if (!text || isTyping.value) return

      messages.value.push({
        id: ++idSeq,
        role: 'user',
        content: text,
        at: new Date()
      })
      draft.value = ''
      isTyping.value = true

      window.setTimeout(() => {
        messages.value.push({
          id: ++idSeq,
          role: 'assistant',
          content: simulateReply(text),
          at: new Date()
        })
        isTyping.value = false
      }, 600 + Math.random() * 500)
    }

    return {
      messages,
      draft,
      isTyping,
      scrollRoot,
      formatTime,
      sendMessage
    }
  }
}
</script>
