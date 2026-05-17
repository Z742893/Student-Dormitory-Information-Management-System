<template>
  <span
    class="line-icon"
    :class="toneClass"
    :style="{ width: px, height: px }"
    role="img"
    aria-hidden="true"
  >
    <svg
      viewBox="0 0 24 24"
      fill="none"
      xmlns="http://www.w3.org/2000/svg"
      stroke="currentColor"
      stroke-width="1.65"
      stroke-linecap="round"
      stroke-linejoin="round"
    >
      <!-- user: 单人轮廓 -->
      <g v-if="name === 'user'">
        <circle cx="12" cy="8" r="3.5" />
        <path d="M6 20v-1a4 4 0 0 1 4-4h4a4 4 0 0 1 4 4v1" />
      </g>

      <!-- users: 访客/多人 -->
      <g v-else-if="name === 'users'">
        <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
        <circle cx="9" cy="7" r="3.5" />
        <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
        <path d="M16 3.13a4 4 0 0 1 0 7.75" />
      </g>

      <!-- home: 房屋 -->
      <g v-else-if="name === 'home'">
        <path d="m3 9 9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z" />
        <polyline points="9 22 9 12 15 12 15 22" />
      </g>

      <!-- wrench: 报修/工具 -->
      <g v-else-if="name === 'wrench'">
        <path
          d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z"
        />
      </g>

      <!-- info -->
      <g v-else-if="name === 'info'">
        <circle cx="12" cy="12" r="10" />
        <path d="M12 16v-4" />
        <path d="M12 8h.01" />
      </g>

      <!-- check -->
      <g v-else-if="name === 'check'">
        <polyline points="20 6 9 17 4 12" />
      </g>

      <!-- help: 问号圆 -->
      <g v-else-if="name === 'help'">
        <circle cx="12" cy="12" r="10" />
        <path d="M9.09 9a3 3 0 0 1 5.82 1c0 2-3 3-3 3" />
        <path d="M12 17h.01" />
      </g>

      <!-- chevron-right -->
      <g v-else-if="name === 'chevron-right'">
        <polyline points="9 18 15 12 9 6" />
      </g>

      <!-- clock -->
      <g v-else-if="name === 'clock'">
        <circle cx="12" cy="12" r="10" />
        <polyline points="12 6 12 12 16 14" />
      </g>

      <!-- circle-check -->
      <g v-else-if="name === 'circle-check'">
        <circle cx="12" cy="12" r="10" />
        <path d="m9 12 2 2 4-4" />
      </g>

      <!-- building: 楼栋 -->
      <g v-else-if="name === 'building'">
        <path d="M3 21h18" />
        <path d="M6 21V10l6-4 6 4v11" />
        <path d="M9 21v-6h6v6" />
      </g>

      <!-- grid: 房间 / 网格 -->
      <g v-else-if="name === 'grid'">
        <rect x="3" y="3" width="7" height="7" rx="1.5" />
        <rect x="14" y="3" width="7" height="7" rx="1.5" />
        <rect x="3" y="14" width="7" height="7" rx="1.5" />
        <rect x="14" y="14" width="7" height="7" rx="1.5" />
      </g>

      <!-- key: 入住 -->
      <g v-else-if="name === 'key'">
        <circle cx="8" cy="10" r="3" />
        <path d="M10.8 12.8 21 21" />
        <path d="M16 16 18 18" />
      </g>

      <!-- clipboard: 签到 / 任务 -->
      <g v-else-if="name === 'clipboard'">
        <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" />
        <path d="M15 2H9a1 1 0 0 0-1 1v2a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1Z" />
        <path d="M9 13h6" />
        <path d="M9 17h4" />
      </g>

      <!-- log-out -->
      <g v-else-if="name === 'log-out'">
        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
        <polyline points="16 17 21 12 16 7" />
        <line x1="21" y1="12" x2="9" y2="12" />
      </g>

      <!-- search -->
      <g v-else-if="name === 'search'">
        <circle cx="11" cy="11" r="8" />
        <path d="m21 21-4.3-4.3" />
      </g>
    </svg>
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  /** user | users | home | wrench | info | check | help | chevron-right | clock | circle-check | building | grid | key | clipboard | log-out | search */
  name: {
    type: String,
    required: true
  },
  size: {
    type: [Number, String],
    default: 20
  },
  /** default | muted | on-primary */
  tone: {
    type: String,
    default: 'default'
  }
})

const px = computed(() => `${Number(props.size)}px`)

const toneClass = computed(() =>
  props.tone === 'muted'
    ? 'line-icon--muted'
    : props.tone === 'on-primary'
      ? 'line-icon--on-primary'
      : ''
)
</script>

<style scoped>
.line-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  vertical-align: middle;
  color: inherit;
}

.line-icon svg {
  width: 100%;
  height: 100%;
  display: block;
}

.line-icon--muted {
  color: var(--app-text-muted, #5a6b82);
  opacity: 0.92;
}

.line-icon--on-primary {
  color: #fff;
  opacity: 0.95;
}
</style>
