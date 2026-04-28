<template>
  <div class="flex flex-col flex-1 min-h-0 w-full h-full">
    <header class="shrink-0 flex items-center gap-3 border-b border-gray-200 bg-white px-6 py-4">
      <div class="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-violet-500 to-indigo-600 shadow-md shadow-indigo-500/20">
        <svg class="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
        </svg>
      </div>
      <div class="min-w-0">
        <h1 class="text-lg font-semibold text-gray-900 truncate">Assistant IA</h1>
        <p class="text-xs text-gray-500 truncate">Connecté à l'API interne LLM</p>
      </div>
    </header>

    <div ref="scrollRoot" class="flex-1 min-h-0 overflow-y-auto bg-gray-50 px-6 py-6 space-y-4">
      <div v-if="messages.length === 0"
        class="flex flex-col items-center justify-center text-center py-16 px-6 text-gray-500">
        <div
          class="w-14 h-14 rounded-2xl bg-gradient-to-br from-violet-500 to-indigo-600 flex items-center justify-center mb-4 shadow-lg shadow-indigo-500/25">
          <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
              d="M8 10h.01M12 10h.01M16 10h.01M9 16H5a2 2 0 01-2-2V6a2 2 0 012-2h14a2 2 0 012 2v8a2 2 0 01-2 2h-5l-5 5v-5z" />
          </svg>
        </div>
        <p class="text-lg font-medium text-gray-800">Assistant IA</p>
        <p class="text-sm mt-2 max-w-sm">Posez une question ou décrivez ce dont vous avez besoin.</p>
      </div>

      <div v-for="msg in messages" :key="msg.id"
        :class="['flex', msg.role === 'user' ? 'justify-end' : 'justify-start']">

        <!-- Message utilisateur -->
        <div v-if="msg.role === 'user'"
          class="max-w-[85%] rounded-2xl rounded-br-md px-4 py-3 text-sm leading-relaxed shadow-sm bg-blue-600 text-white">
          <p class="whitespace-pre-wrap break-words">{{ msg.content }}</p>
          <p class="text-[10px] mt-2 opacity-70 text-blue-100">{{ formatTime(msg.at) }}</p>
        </div>

        <!-- Message assistant avec rendu code -->
        <div v-else
          class="max-w-[85%] rounded-2xl rounded-bl-md border border-gray-200 bg-white text-gray-900 shadow-sm overflow-hidden">
          <div class="px-4 py-3 text-sm leading-relaxed">
            <MessageContent :content="msg.content" />
          </div>
          <p class="text-[10px] px-4 pb-2 opacity-70 text-gray-500">{{ formatTime(msg.at) }}</p>
        </div>
      </div>

      <div v-if="isTyping" class="flex justify-start">
        <div
          class="rounded-2xl rounded-bl-md border border-gray-200 bg-gray-50 px-4 py-3 flex items-center gap-1.5">
          <span class="w-2 h-2 rounded-full bg-gray-400 animate-bounce" style="animation-delay: 0ms" />
          <span class="w-2 h-2 rounded-full bg-gray-400 animate-bounce" style="animation-delay: 150ms" />
          <span class="w-2 h-2 rounded-full bg-gray-400 animate-bounce" style="animation-delay: 300ms" />
        </div>
      </div>
    </div>

    <div class="shrink-0 border-t border-gray-200 bg-white px-6 py-4">
      <div class="flex gap-3 items-end max-w-5xl mx-auto w-full">
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
          type="button"
          :disabled="!draft.trim() || isTyping"
          class="shrink-0 inline-flex items-center justify-center rounded-xl bg-blue-600 px-6 py-3 text-sm font-medium text-white shadow-sm hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition"
          @click="sendMessage"
        >
          Envoyer
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, h, defineComponent, onMounted } from 'vue'

let idSeq = 0
const messages = ref([])
const draft = ref('')
const isTyping = ref(false)
const scrollRoot = ref(null)

// ─── Chargement dynamique de highlight.js ───────────────────────
const hljs = ref(null)

function loadStylesheet(href) {
  if (document.querySelector(`link[href="${href}"]`)) return
  const link = document.createElement('link')
  link.rel = 'stylesheet'
  link.href = href
  document.head.appendChild(link)
}

onMounted(async () => {
  // Thème GitHub Dark via CDN cdnjs
  loadStylesheet('https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/github-dark.min.css')

  // Chargement du script hljs
  await new Promise((resolve, reject) => {
    if (window.hljs) { hljs.value = window.hljs; resolve(); return }
    const script = document.createElement('script')
    script.src = 'https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js'
    script.onload = () => { hljs.value = window.hljs; resolve() }
    script.onerror = reject
    document.head.appendChild(script)
  })
})

// ─── Utilitaires ────────────────────────────────────────────────
const formatTime = (d) =>
  new Intl.DateTimeFormat('fr-FR', { hour: '2-digit', minute: '2-digit' }).format(d)

const scrollToBottom = async () => {
  await nextTick()
  const el = scrollRoot.value
  if (el) el.scrollTop = el.scrollHeight
}

watch(messages, () => scrollToBottom(), { deep: true })
watch(isTyping, () => scrollToBottom())

// ─── Détection des blocs de code ────────────────────────────────
function parseContent(text) {
  const segments = []
  const codeBlockRegex = /```(\w*)\n?([\s\S]*?)```/g
  let lastIndex = 0
  let match

  while ((match = codeBlockRegex.exec(text)) !== null) {
    if (match.index > lastIndex) {
      segments.push({ type: 'text', content: text.slice(lastIndex, match.index) })
    }
    segments.push({
      type: 'code',
      lang: match[1] || 'plaintext',
      content: match[2].trimEnd()
    })
    lastIndex = match.index + match[0].length
  }

  if (lastIndex < text.length) {
    segments.push({ type: 'text', content: text.slice(lastIndex) })
  }

  return segments
}

// ─── Composant CodeBlock avec highlight.js ──────────────────────
const CodeBlock = defineComponent({
  name: 'CodeBlock',
  props: {
    lang: { type: String, default: 'plaintext' },
    code: { type: String, required: true }
  },
  setup(props) {
    const copied = ref(false)
    const codeRef = ref(null)

    // Applique highlight.js dès que le DOM est prêt et que hljs est chargé
    const applyHighlight = () => {
      if (codeRef.value && hljs.value) {
        // Remet le contenu texte brut avant de re-highlighter
        codeRef.value.textContent = props.code
        const validLang = hljs.value.getLanguage(props.lang)
        if (validLang) {
          const result = hljs.value.highlight(props.code, { language: props.lang })
          codeRef.value.innerHTML = result.value
        } else {
          const result = hljs.value.highlightAuto(props.code)
          codeRef.value.innerHTML = result.value
        }
      }
    }

    // Applique quand le composant est monté
    onMounted(() => {
      if (hljs.value) {
        applyHighlight()
      } else {
        // hljs pas encore chargé, on attend
        const unwatch = watch(hljs, (val) => {
          if (val) { applyHighlight(); unwatch() }
        })
      }
    })

    const copy = async () => {
      try {
        await navigator.clipboard.writeText(props.code)
        copied.value = true
        setTimeout(() => { copied.value = false }, 2000)
      } catch {
        const ta = document.createElement('textarea')
        ta.value = props.code
        document.body.appendChild(ta)
        ta.select()
        document.execCommand('copy')
        document.body.removeChild(ta)
        copied.value = true
        setTimeout(() => { copied.value = false }, 2000)
      }
    }

    return () =>
      h('div', { class: 'my-3 rounded-xl overflow-hidden border border-gray-700 shadow-lg' }, [
        // Barre supérieure
        h('div', { class: 'flex items-center justify-between px-4 py-2 bg-[#161b22] border-b border-gray-700' }, [
          h('div', { class: 'flex items-center gap-2' }, [
            // Points décoratifs style macOS
            h('span', { class: 'w-3 h-3 rounded-full bg-[#ff5f57]' }),
            h('span', { class: 'w-3 h-3 rounded-full bg-[#febc2e]' }),
            h('span', { class: 'w-3 h-3 rounded-full bg-[#28c840]' }),
            h('span', { class: 'ml-3 text-xs font-mono font-semibold text-gray-400 uppercase tracking-widest' }, props.lang)
          ]),
          h('button', {
            onClick: copy,
            class: 'flex items-center gap-1.5 text-xs px-3 py-1.5 rounded-lg font-medium transition-all duration-200 ' +
              (copied.value
                ? 'bg-green-500/20 text-green-400 border border-green-500/30'
                : 'bg-gray-700/60 text-gray-300 border border-gray-600/50 hover:bg-gray-600 hover:text-white hover:border-gray-500')
          }, [
            copied.value
              ? h('svg', { class: 'w-3.5 h-3.5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' },
                  h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2.5', d: 'M5 13l4 4L19 7' }))
              : h('svg', { class: 'w-3.5 h-3.5', fill: 'none', stroke: 'currentColor', viewBox: '0 0 24 24' },
                  h('path', { 'stroke-linecap': 'round', 'stroke-linejoin': 'round', 'stroke-width': '2',
                    d: 'M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z' })),
            copied.value ? 'Copié !' : 'Copier'
          ])
        ]),
        // Bloc de code — hljs injecte le HTML coloré dans codeRef
        h('pre', { class: 'overflow-x-auto bg-[#0d1117] px-5 py-4 m-0' },
          h('code', {
            ref: codeRef,
            class: `hljs language-${props.lang} font-mono text-[13px] leading-6`
          }, props.code)
        )
      ])
  }
})

// ─── Composant MessageContent : parse et rend les segments ──────
const MessageContent = defineComponent({
  name: 'MessageContent',
  props: {
    content: { type: String, required: true }
  },
  setup(props) {
    return () => {
      const segments = parseContent(props.content)
      return h('div', { class: 'space-y-1' },
        segments.map((seg, i) => {
          if (seg.type === 'code') {
            return h(CodeBlock, { key: i, lang: seg.lang, code: seg.content })
          }
          // Texte brut : on gère aussi le inline code `...`
          const parts = seg.content.split(/(`[^`]+`)/)
          return h('p', { key: i, class: 'whitespace-pre-wrap break-words' },
            parts.map((part, j) => {
              if (part.startsWith('`') && part.endsWith('`')) {
                return h('code', {
                  key: j,
                  class: 'bg-gray-100 text-rose-600 rounded px-1.5 py-0.5 text-[12px] font-mono border border-gray-200'
                }, part.slice(1, -1))
              }
              return part
            })
          )
        })
      )
    }
  }
})

// ─── Envoi du message vers l'API ────────────────────────────────
const sendMessage = async () => {
  const text = draft.value.trim()
  if (!text || isTyping.value) return

  messages.value.push({ id: ++idSeq, role: 'user', content: text, at: new Date() })
  draft.value = ''
  isTyping.value = true

  try {
    const response = await fetch(
      'https://llmaas-ap88967-prod.data.cloud.net.intra/engines/gpt-oss-120b/chat/completions',
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'x-litellm-api-key': 'sk-yW9DGMr_cwC0NnOfNpKwOg'
        },
        body: JSON.stringify({
          messages: messages.value.map(m => ({ role: m.role, content: m.content }))
        })
      }
    )

    const data = await response.json()
    const reply = data.choices[0].message.content

    messages.value.push({ id: ++idSeq, role: 'assistant', content: reply, at: new Date() })
  } catch (err) {
    messages.value.push({
      id: ++idSeq,
      role: 'assistant',
      content: '⚠️ Erreur lors de la connexion à l\'API.',
      at: new Date()
    })
  } finally {
    isTyping.value = false
  }
}
</script>