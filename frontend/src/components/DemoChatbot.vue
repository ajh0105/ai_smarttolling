<script setup>
import { computed, nextTick, ref, useTemplateRef } from 'vue'
import { demoChatResponses, demoChatGreeting, demoChatFallback } from '@/data/demoChatResponses'

// Jetson 시연 페이지 URL (Tailscale IP, LTE 환경에서도 동작)
const JETSON_DEMO_URL = import.meta.env.VITE_JETSON_DEMO_URL || 'http://100.125.60.62:8000'

const isOpen = ref(false)
const inputText = ref('')
const isTyping = ref(false)
const messages = ref([{ role: 'bot', type: 'text', text: demoChatGreeting }])
const listRef = useTemplateRef('listRef')

const NAV_RULES = [
  { keywords: ['시연페이지', '데모페이지'], external: JETSON_DEMO_URL, reply: '시연 페이지로 이동합니다.' }
]

function normalize(text) {
  return text.toLowerCase().replace(/\s+/g, '')
}

function findNavigation(text) {
  const norm = normalize(text)
  for (const rule of NAV_RULES) {
    if (rule.keywords.some((kw) => norm.includes(normalize(kw)))) return rule
  }
  return null
}

function findAnswer(text) {
  const norm = normalize(text)
  for (const item of demoChatResponses) {
    for (const kw of item.keywords) {
      if (norm.includes(normalize(kw))) return item.payload
    }
  }
  return { type: 'text', text: demoChatFallback }
}

async function scrollToBottom() {
  await nextTick()
  if (listRef.value) listRef.value.scrollTop = listRef.value.scrollHeight
}

const CHECKUP_KEYWORDS = ['시연점검', '시스템점검', '데모점검', '시연체크']

function isCheckupRequest(text) {
  const norm = normalize(text)
  return CHECKUP_KEYWORDS.some((kw) => norm.includes(kw))
}

async function runSystemCheck() {
  const items = [
    { name: 'Spring Boot 서버', status: 'pending', note: '' },
    { name: 'FastAPI 서버', status: 'pending', note: '' },
    { name: 'Jetson', status: 'pending', note: '' },
    { name: '네트워크', status: 'pending', note: '' },
    { name: '메모리', status: 'pending', note: '' }
  ]
  const card = {
    role: 'bot',
    type: 'system-check',
    data: { title: '시연 점검 진행 중', items, done: false }
  }
  messages.value.push(card)
  await scrollToBottom()

  const idx = messages.value.length - 1
  for (let i = 0; i < items.length; i++) {
    await new Promise((r) => setTimeout(r, 1000))
    messages.value[idx].data.items[i].status = 'ok'
    messages.value[idx].data.items[i].note = '정상'
    await scrollToBottom()
  }
  messages.value[idx].data.title = '시연 점검 완료'
  messages.value[idx].data.done = true

  await new Promise((r) => setTimeout(r, 400))
  messages.value.push({
    role: 'bot',
    type: 'text',
    text: '전체 시스템 이상 없음.\n시연을 진행할 수 있습니다.\n좋은 결과가 있길 바래요.'
  })
  await scrollToBottom()
}

async function send() {
  const text = inputText.value.trim()
  if (!text || isTyping.value) return
  messages.value.push({ role: 'user', type: 'text', text })
  inputText.value = ''
  isTyping.value = true
  await scrollToBottom()

  // 라우팅 키워드 가로채기 ("시연 페이지" 등 → Jetson Tailscale URL 같은 탭 이동)
  const nav = findNavigation(text)
  if (nav) {
    await new Promise((r) => setTimeout(r, 400))
    isTyping.value = false
    messages.value.push({ role: 'bot', type: 'text', text: nav.reply })
    await scrollToBottom()
    setTimeout(() => {
      isOpen.value = false
      window.location.href = nav.external
    }, 1100)
    return
  }

  // 시연 점검 키워드 가로채기 (5개 항목 1초씩 순차 OK 표시)
  if (isCheckupRequest(text)) {
    await new Promise((r) => setTimeout(r, 400))
    isTyping.value = false
    await runSystemCheck()
    return
  }

  const reply = findAnswer(text)

  // 1) typing 인디케이터: 짧게
  await new Promise((r) => setTimeout(r, 500))
  isTyping.value = false

  // 2) loading 단계(있으면): "확인 중" 메시지 + 진행 막대
  if (reply.loadingText) {
    const duration = reply.loadingDuration ?? 4000
    messages.value.push({ role: 'bot', type: 'loading', text: reply.loadingText, duration })
    await scrollToBottom()
    await new Promise((r) => setTimeout(r, duration))
    messages.value = messages.value.filter((m) => m.type !== 'loading')
  }

  // 3) 실제 답변
  const final = { ...reply }
  delete final.loadingText
  delete final.loadingDuration
  messages.value.push({ role: 'bot', ...final })
  await scrollToBottom()
}

function toggle() {
  isOpen.value = !isOpen.value
  if (isOpen.value) scrollToBottom()
}
</script>

<template>
  <Transition name="chatbot-fade">
    <section v-if="isOpen" class="chatbot-panel" role="dialog" aria-label="Hi-Five 어시스턴트">
      <header class="chatbot-header">
        <span class="chatbot-title">Hi-Five 어시스턴트</span>
        <button class="chatbot-close" type="button" aria-label="닫기" @click="toggle">×</button>
      </header>
      <ul ref="listRef" class="chatbot-messages">
        <li
          v-for="(m, i) in messages"
          :key="i"
          :class="['chatbot-message', m.role === 'user' ? 'is-user' : 'is-bot', (m.type === 'edge-warning' || m.type === 'traffic-summary' || m.type === 'alert-list' || m.type === 'system-check') ? 'is-card' : '', m.type === 'loading' ? 'is-loading' : '']"
        >
          <template v-if="m.type === 'loading'">
            <div class="loading-row">
              <span class="loading-spinner" aria-hidden="true" />
              <span class="loading-text">{{ m.text }}</span>
            </div>
            <div class="loading-bar">
              <div class="loading-bar-fill" :style="{ animationDuration: m.duration + 'ms' }" />
            </div>
          </template>
          <template v-else-if="m.type === 'traffic-summary'">
            <article class="traffic-card">
              <header class="traffic-card-head">
                <span class="traffic-card-icon" aria-hidden="true">📊</span>
                <span class="traffic-card-title">{{ m.data.title }}</span>
                <span class="traffic-card-badge">{{ m.data.badge }}</span>
              </header>
              <div class="traffic-card-headline">
                <span class="traffic-card-value">
                  {{ m.data.primary.value.toLocaleString() }}<span class="traffic-card-suffix">{{ m.data.primary.suffix }}</span>
                </span>
                <span :class="['traffic-card-change', `is-${m.data.change.direction}`]">
                  <span class="traffic-card-arrow">{{ m.data.change.direction === 'up' ? '▲' : '▼' }}</span>
                  {{ m.data.change.value.toFixed(1) }}{{ m.data.change.suffix }}
                </span>
              </div>
              <div class="traffic-card-compare">
                <div class="traffic-card-row">
                  <span class="traffic-card-row-label">{{ m.data.primary.label }}</span>
                  <div class="traffic-card-bar">
                    <div class="traffic-card-bar-fill is-primary" :style="{ width: (m.data.primary.value / Math.max(m.data.primary.value, m.data.comparison.value)) * 100 + '%' }" />
                  </div>
                  <span class="traffic-card-row-value">{{ m.data.primary.value.toLocaleString() }}{{ m.data.primary.suffix }}</span>
                </div>
                <div class="traffic-card-row">
                  <span class="traffic-card-row-label">{{ m.data.comparison.label }}</span>
                  <div class="traffic-card-bar">
                    <div class="traffic-card-bar-fill is-muted" :style="{ width: (m.data.comparison.value / Math.max(m.data.primary.value, m.data.comparison.value)) * 100 + '%' }" />
                  </div>
                  <span class="traffic-card-row-value">{{ m.data.comparison.value.toLocaleString() }}{{ m.data.comparison.suffix }}</span>
                </div>
              </div>
              <p class="traffic-card-note">{{ m.data.note }}</p>
            </article>
          </template>
          <template v-else-if="m.type === 'alert-list'">
            <article class="alert-card">
              <header class="alert-card-head">
                <span class="alert-card-icon" aria-hidden="true">🔔</span>
                <span class="alert-card-title">{{ m.data.title }}</span>
                <span class="alert-card-summary">
                  <span
                    v-for="s in m.data.summary"
                    :key="s.level"
                    :class="['alert-pill', `is-${s.level}`]"
                  >{{ s.count }} {{ s.level.toUpperCase() }}</span>
                </span>
              </header>
              <ul class="alert-list">
                <li
                  v-for="(a, idx) in m.data.alerts"
                  :key="idx"
                  :class="['alert-item', `is-${a.level}`]"
                >
                  <div class="alert-item-head">
                    <span :class="['alert-badge', `is-${a.level}`]">
                      <span class="alert-badge-dot" aria-hidden="true" />{{ a.level.toUpperCase() }}
                    </span>
                    <span class="alert-time">{{ a.time }}</span>
                  </div>
                  <div class="alert-target">{{ a.target }}</div>
                  <div class="alert-title">{{ a.title }}</div>
                  <div v-if="a.meta" class="alert-meta">{{ a.meta }}</div>
                  <div v-if="a.note" class="alert-note">{{ a.note }}</div>
                </li>
              </ul>
            </article>
          </template>
          <template v-else-if="m.type === 'edge-warning'">
            <article class="edge-card">
              <header class="edge-card-head">
                <span class="edge-card-icon" aria-hidden="true">⚠</span>
                <span class="edge-card-title">{{ m.data.edgeId }}</span>
                <span class="edge-card-badge">{{ m.data.badge }}</span>
              </header>
              <div class="edge-card-metrics">
                <div class="edge-card-metric">
                  <span class="edge-card-metric-label">{{ m.data.primary.label }}</span>
                  <span class="edge-card-metric-value is-warn">
                    {{ m.data.primary.value.toFixed(m.data.primary.decimals ?? 1) }}<span class="edge-card-metric-suffix">{{ m.data.primary.suffix }}</span>
                  </span>
                </div>
                <div class="edge-card-metric is-muted">
                  <span class="edge-card-metric-label">{{ m.data.threshold.label }}</span>
                  <span class="edge-card-metric-value">
                    {{ m.data.threshold.value.toFixed(m.data.threshold.decimals ?? 1) }}<span class="edge-card-metric-suffix">{{ m.data.threshold.suffix }}</span>
                  </span>
                </div>
              </div>
              <div class="edge-card-bar" :aria-label="`임계 대비 ${(m.data.primary.value / m.data.threshold.value * 100).toFixed(0)}%`">
                <div class="edge-card-bar-fill" :style="{ width: Math.min(100, (m.data.primary.value / m.data.threshold.value) * 83.33) + '%' }" />
                <div class="edge-card-bar-threshold" style="left: 83.33%" />
              </div>
              <p class="edge-card-note">{{ m.data.note }}</p>
            </article>
          </template>
          <template v-else-if="m.type === 'system-check'">
            <article class="check-card" :class="{ 'is-done': m.data.done, 'is-failed': m.data.failed }">
              <header class="check-card-head">
                <span class="check-card-icon" aria-hidden="true">
                  {{ m.data.failed ? '⚠️' : (m.data.done ? '✅' : '🔍') }}
                </span>
                <span class="check-card-title">{{ m.data.title }}</span>
              </header>
              <ul class="check-list">
                <li
                  v-for="(item, idx) in m.data.items"
                  :key="idx"
                  :class="['check-item', `is-${item.status}`]"
                >
                  <span class="check-icon" aria-hidden="true">
                    <span v-if="item.status === 'pending'" class="check-spinner" />
                    <span v-else-if="item.status === 'error'" class="check-cross">✕</span>
                    <span v-else class="check-tick">✓</span>
                  </span>
                  <span class="check-name">{{ item.name }}</span>
                  <span class="check-note">{{ item.status === 'pending' ? '확인 중…' : item.note }}</span>
                </li>
              </ul>
            </article>
          </template>
          <span v-else>{{ m.text }}</span>
        </li>
        <li v-if="isTyping" class="chatbot-message is-bot is-typing" aria-live="polite">
          <span class="typing-dots"><span /><span /><span /></span>
        </li>
      </ul>
      <form class="chatbot-input-row" @submit.prevent="send">
        <input
          v-model="inputText"
          type="text"
          class="chatbot-input"
          placeholder="질문을 입력하세요"
          autocomplete="off"
          :disabled="isTyping"
        />
        <button type="submit" class="chatbot-send" :disabled="isTyping || !inputText.trim()">전송</button>
      </form>
    </section>
  </Transition>

  <button class="chatbot-fab" type="button" :aria-expanded="isOpen" aria-label="Hi-Five 어시스턴트 열기" @click="toggle">
    <img src="/chatbot.png" alt="chatbot" />
  </button>
</template>

<style scoped>
.chatbot-fab {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  border: none;
  background: #ffffff;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.18);
  cursor: pointer;
  z-index: 9998;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.15s ease;
}
.chatbot-fab:hover {
  transform: translateY(-2px);
}
.chatbot-fab img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
}

.chatbot-panel {
  position: fixed;
  right: 24px;
  bottom: 100px;
  width: 340px;
  height: 460px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 12px 36px rgba(0, 0, 0, 0.22);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  z-index: 9999;
  font-family: inherit;
}

.chatbot-header {
  padding: 12px 14px;
  background: #1f3a5f;
  color: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-shrink: 0;
}
.chatbot-title {
  font-weight: 600;
  font-size: 14px;
  letter-spacing: 0.2px;
}
.chatbot-close {
  background: transparent;
  border: none;
  color: #ffffff;
  font-size: 22px;
  cursor: pointer;
  line-height: 1;
  padding: 0 4px;
}
.chatbot-close:hover {
  opacity: 0.8;
}

.chatbot-messages {
  list-style: none;
  margin: 0;
  padding: 12px;
  flex: 1;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: #f6f8fb;
}
.chatbot-message {
  max-width: 82%;
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 13px;
  line-height: 1.5;
  word-break: keep-all;
  white-space: pre-wrap;
  animation: msg-in 0.22s ease both;
}
.chatbot-message.is-bot {
  align-self: flex-start;
  background: #e9eef5;
  color: #1f2937;
  border-bottom-left-radius: 4px;
}
.chatbot-message.is-user {
  align-self: flex-end;
  background: #2563eb;
  color: #ffffff;
  border-bottom-right-radius: 4px;
}
.chatbot-message.is-card {
  max-width: 92%;
  padding: 0;
  background: transparent;
  border-radius: 0;
}

@keyframes msg-in {
  from { opacity: 0; transform: translateY(4px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Typing indicator */
.chatbot-message.is-typing {
  background: #e9eef5;
  padding: 10px 14px;
}
.typing-dots {
  display: inline-flex;
  gap: 4px;
  align-items: center;
}
.typing-dots span {
  width: 6px;
  height: 6px;
  background: #6b7280;
  border-radius: 50%;
  animation: typing 1.2s infinite ease-in-out;
}
.typing-dots span:nth-child(2) { animation-delay: 0.18s; }
.typing-dots span:nth-child(3) { animation-delay: 0.36s; }
@keyframes typing {
  0%, 60%, 100% { opacity: 0.3; transform: translateY(0); }
  30% { opacity: 1; transform: translateY(-3px); }
}

/* Loading bubble (data lookup) */
.chatbot-message.is-loading {
  background: #ffffff;
  border: 1px solid #e0e7ef;
  max-width: 92%;
  padding: 10px 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
}
.loading-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.loading-spinner {
  width: 12px;
  height: 12px;
  border: 2px solid #cbd5e1;
  border-top-color: #2563eb;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  flex-shrink: 0;
  display: inline-block;
}
.loading-text {
  font-size: 12.5px;
  color: #334155;
  font-weight: 500;
}
.loading-bar {
  width: 100%;
  height: 3px;
  background: #e5e7eb;
  border-radius: 2px;
  overflow: hidden;
}
.loading-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #2563eb 0%, #60a5fa 100%);
  width: 0;
  animation-name: loading-progress;
  animation-timing-function: linear;
  animation-fill-mode: forwards;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
@keyframes loading-progress {
  from { width: 0; }
  to { width: 100%; }
}

/* Traffic summary card */
.traffic-card {
  background: #ffffff;
  border: 1px solid #dbeafe;
  border-radius: 12px;
  padding: 12px 14px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
  width: 100%;
  box-sizing: border-box;
}
.traffic-card-head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f1f5f9;
}
.traffic-card-icon { font-size: 14px; }
.traffic-card-title {
  flex: 1;
  font-weight: 700;
  font-size: 13px;
  color: #1f2937;
  letter-spacing: 0.2px;
}
.traffic-card-badge {
  font-size: 10px;
  font-weight: 700;
  padding: 3px 9px;
  border-radius: 999px;
  background: #2563eb;
  color: #ffffff;
  letter-spacing: 0.4px;
}
.traffic-card-headline {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding: 10px 0 8px;
}
.traffic-card-value {
  font-size: 26px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1;
}
.traffic-card-suffix {
  font-size: 13px;
  font-weight: 600;
  margin-left: 2px;
  color: #6b7280;
}
.traffic-card-change {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  font-weight: 700;
  padding: 3px 9px;
  border-radius: 999px;
}
.traffic-card-change.is-up {
  color: #047857;
  background: #d1fae5;
}
.traffic-card-change.is-down {
  color: #b91c1c;
  background: #fee2e2;
}
.traffic-card-arrow {
  font-size: 9px;
  line-height: 1;
}
.traffic-card-compare {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding-top: 2px;
}
.traffic-card-row {
  display: grid;
  grid-template-columns: 54px 1fr 60px;
  align-items: center;
  gap: 8px;
}
.traffic-card-row-label {
  font-size: 11px;
  color: #6b7280;
}
.traffic-card-bar {
  height: 8px;
  background: #f1f5f9;
  border-radius: 4px;
  overflow: hidden;
}
.traffic-card-bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.4s ease;
}
.traffic-card-bar-fill.is-primary {
  background: linear-gradient(90deg, #2563eb 0%, #60a5fa 100%);
}
.traffic-card-bar-fill.is-muted {
  background: #cbd5e1;
}
.traffic-card-row-value {
  font-size: 11.5px;
  font-weight: 600;
  color: #1f2937;
  text-align: right;
}
.traffic-card-note {
  margin: 8px 0 0;
  padding: 6px 8px;
  font-size: 11px;
  color: #1e40af;
  background: #eff6ff;
  border-radius: 6px;
  line-height: 1.4;
}

/* Alert list card */
.alert-card {
  background: #ffffff;
  border: 1px solid #fde0c2;
  border-radius: 12px;
  padding: 12px 14px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
  width: 100%;
  box-sizing: border-box;
}
.alert-card-head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f1f5f9;
}
.alert-card-icon { font-size: 14px; }
.alert-card-title {
  flex: 1;
  font-weight: 700;
  font-size: 13px;
  color: #1f2937;
}
.alert-card-summary {
  display: inline-flex;
  gap: 4px;
}
.alert-pill {
  font-size: 10px;
  font-weight: 700;
  padding: 2px 7px;
  border-radius: 999px;
  letter-spacing: 0.3px;
}
.alert-pill.is-warn { background: #f59e0b; color: #ffffff; }
.alert-pill.is-info { background: #dbeafe; color: #1e40af; }

.alert-list {
  list-style: none;
  padding: 0;
  margin: 10px 0 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.alert-item {
  padding: 9px 11px;
  border-radius: 8px;
  background: #f8fafc;
  border-left: 3px solid #cbd5e1;
}
.alert-item.is-warn {
  background: #fff7ed;
  border-left-color: #f59e0b;
}
.alert-item.is-info {
  background: #f0f9ff;
  border-left-color: #2563eb;
}
.alert-item-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 4px;
}
.alert-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 999px;
  letter-spacing: 0.3px;
  border: 1px solid transparent;
}
.alert-badge.is-warn {
  background: #ffffff;
  color: #b45309;
  border-color: #fde0c2;
}
.alert-badge.is-info {
  background: #ffffff;
  color: #1e40af;
  border-color: #dbeafe;
}
.alert-badge-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
  display: inline-block;
}
.alert-time {
  font-size: 11px;
  color: #6b7280;
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
}
.alert-target {
  font-size: 11px;
  font-weight: 700;
  color: #475569;
  letter-spacing: 0.2px;
}
.alert-title {
  font-size: 12.5px;
  font-weight: 600;
  color: #1f2937;
  margin-top: 2px;
  line-height: 1.4;
}
.alert-meta {
  font-size: 11px;
  color: #6b7280;
  margin-top: 2px;
  line-height: 1.4;
}
.alert-note {
  margin-top: 4px;
  padding: 5px 8px;
  font-size: 11px;
  color: #b45309;
  background: #fef3c7;
  border-radius: 4px;
  line-height: 1.4;
}

/* Edge warning card */
.edge-card {
  background: #ffffff;
  border: 1px solid #fde0c2;
  border-radius: 12px;
  padding: 12px 14px;
  box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
  width: 100%;
  box-sizing: border-box;
}
.edge-card-head {
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f1f5f9;
}
.edge-card-icon {
  font-size: 14px;
  color: #f59e0b;
}
.edge-card-title {
  flex: 1;
  font-weight: 700;
  font-size: 13px;
  color: #1f2937;
  letter-spacing: 0.2px;
}
.edge-card-badge {
  font-size: 10px;
  font-weight: 700;
  padding: 3px 9px;
  border-radius: 999px;
  background: #f59e0b;
  color: #ffffff;
  letter-spacing: 0.4px;
}
.edge-card-metrics {
  display: flex;
  gap: 14px;
  padding: 10px 0 6px;
}
.edge-card-metric {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.edge-card-metric.is-muted {
  opacity: 0.7;
}
.edge-card-metric-label {
  font-size: 10.5px;
  color: #6b7280;
  letter-spacing: 0.2px;
}
.edge-card-metric-value {
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.1;
}
.edge-card-metric-value.is-warn {
  color: #f59e0b;
}
.edge-card-metric-suffix {
  font-size: 11px;
  font-weight: 600;
  margin-left: 1px;
}
.edge-card-bar {
  position: relative;
  height: 6px;
  background: #f1f5f9;
  border-radius: 3px;
  margin: 6px 0 8px;
  overflow: visible;
}
.edge-card-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #f59e0b 0%, #ea580c 100%);
  border-radius: 3px;
  transition: width 0.4s ease;
}
.edge-card-bar-threshold {
  position: absolute;
  top: -3px;
  bottom: -3px;
  width: 2px;
  background: #ef4444;
  border-radius: 1px;
}
.edge-card-bar-threshold::after {
  content: '임계';
  position: absolute;
  top: -16px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 9px;
  color: #ef4444;
  font-weight: 600;
  white-space: nowrap;
}
.edge-card-note {
  margin: 4px 0 0;
  padding: 6px 8px;
  font-size: 11px;
  color: #b45309;
  background: #fef3c7;
  border-radius: 6px;
  line-height: 1.4;
}

.chatbot-input-row {
  display: flex;
  border-top: 1px solid #e5e7eb;
  background: #ffffff;
  flex-shrink: 0;
}
.chatbot-input {
  flex: 1;
  border: none;
  padding: 12px 14px;
  font-size: 13px;
  outline: none;
  background: transparent;
  color: #1f2937;
}
.chatbot-input:disabled {
  background: #f9fafb;
  color: #9ca3af;
}
.chatbot-send {
  border: none;
  background: #2563eb;
  color: #ffffff;
  padding: 0 18px;
  font-size: 13px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.15s;
}
.chatbot-send:hover:not(:disabled) {
  background: #1d4ed8;
}
.chatbot-send:disabled {
  background: #9ca3af;
  cursor: not-allowed;
}

.chatbot-fade-enter-active,
.chatbot-fade-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}
.chatbot-fade-enter-from,
.chatbot-fade-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

/* ===== 시연 점검 카드 ===== */
.check-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
  width: 100%;
}
.check-card-head {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 700;
  color: #0b1840;
  letter-spacing: -0.01em;
}
.check-card.is-done .check-card-title { color: #0b7a3f; }
.check-card-icon { font-size: 14px; }
.check-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.check-item {
  display: grid;
  grid-template-columns: 16px 1fr auto;
  align-items: center;
  gap: 8px;
  padding: 7px 9px;
  background: #f2f7fd;
  border-radius: 6px;
  font-size: 12px;
  transition: background-color 0.2s ease;
}
.check-item.is-ok { background: rgba(11, 122, 63, 0.10); }
.check-item.is-pending .check-name { color: #5d6d82; }
.check-item.is-ok .check-name {
  color: #0b1840;
  font-weight: 700;
}
.check-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 14px;
  height: 14px;
}
.check-spinner {
  width: 12px;
  height: 12px;
  border: 2px solid #c7d8ff;
  border-top-color: #1b3be8;
  border-radius: 50%;
  animation: check-spin 0.8s linear infinite;
  display: inline-block;
}
@keyframes check-spin {
  to { transform: rotate(360deg); }
}
.check-tick {
  color: #0b7a3f;
  font-weight: 900;
  font-size: 13px;
  line-height: 1;
}
.check-note {
  font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
  font-size: 11px;
  color: #5d6d82;
  letter-spacing: -0.01em;
}
.check-item.is-ok .check-note {
  color: #0b7a3f;
  font-weight: 700;
}

/* === 에러 상태 (네트워크 실패) === */
.check-item.is-error {
  background: rgba(226, 73, 62, 0.10);
  border: 1px solid rgba(226, 73, 62, 0.22);
  animation: error-pulse 1.6s ease-in-out infinite;
}
.check-item.is-error .check-name {
  color: #e2493e;
  font-weight: 700;
}
.check-cross {
  color: #e2493e;
  font-weight: 900;
  font-size: 13px;
  line-height: 1;
}
.check-item.is-error .check-note {
  color: #e2493e;
  font-weight: 700;
}
@keyframes error-pulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(226, 73, 62, 0); }
  50%      { box-shadow: 0 0 0 3px rgba(226, 73, 62, 0.18); }
}
.check-card.is-failed .check-card-title { color: #e2493e; }

/* === 재테스트 확인 카드 === */
.alert-confirm {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  background: linear-gradient(180deg, #fff5f4 0%, #fdecea 100%);
  border: 1px solid #f5c2bc;
  border-radius: 10px;
  width: 100%;
}
.alert-confirm-head {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 800;
  color: #b8362c;
}
.alert-confirm-icon { font-size: 14px; }
.alert-confirm-text {
  font-size: 12.5px;
  line-height: 1.5;
  color: #5d3a36;
  margin: 0;
}
.alert-confirm-prompt {
  margin-top: 4px;
  font-size: 12.5px;
  font-weight: 700;
  color: #0b1840;
}
.alert-confirm-actions {
  display: flex;
  gap: 8px;
  margin-top: 6px;
}
.confirm-btn {
  flex: 1;
  height: 32px;
  border-radius: 7px;
  border: 1px solid transparent;
  font-size: 12.5px;
  font-weight: 800;
  letter-spacing: -0.01em;
  cursor: pointer;
  transition: all 0.15s ease;
}
.confirm-btn.yes {
  background: #0c59b6;
  color: #fff;
}
.confirm-btn.yes:hover { background: #0b3979; }
.confirm-btn.no {
  background: #ffffff;
  color: #5d3a36;
  border-color: #f5c2bc;
}
.confirm-btn.no:hover { background: #fff5f4; }
.alert-confirm-result {
  margin-top: 4px;
  padding: 6px 10px;
  border-radius: 6px;
  font-size: 11.5px;
  font-weight: 800;
  letter-spacing: 0.04em;
  text-align: center;
}
.alert-confirm-result.is-yes {
  background: rgba(11, 122, 63, 0.10);
  color: #0b7a3f;
}
.alert-confirm-result.is-no {
  background: rgba(93, 109, 130, 0.10);
  color: #5d6d82;
}
</style>
