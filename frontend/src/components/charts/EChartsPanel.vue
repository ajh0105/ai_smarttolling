<script setup>
import { ref, computed, onMounted, onBeforeUnmount, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  option: { type: Object, required: true },
  height: { type: [Number, String], default: 220 }
})

const heightStyle = computed(() =>
  typeof props.height === 'number' ? `${props.height}px` : props.height
)

const el = ref(null)
let chart = null

function init() {
  if (!el.value) return
  chart = echarts.init(el.value, null, { renderer: 'canvas' })
  chart.setOption(props.option)
}

function resize() {
  chart?.resize()
}

onMounted(async () => {
  await nextTick()
  init()
  window.addEventListener('resize', resize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resize)
  chart?.dispose()
  chart = null
})

watch(
  () => props.option,
  (opt) => { chart?.setOption(opt, { notMerge: false, lazyUpdate: true }) },
  { deep: true }
)
</script>

<template>
  <div ref="el" :style="{ width: '100%', height: heightStyle }"></div>
</template>
