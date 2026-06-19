<script setup>
import { nextTick, onBeforeUnmount, onMounted, ref, watch } from 'vue'

const props = defineProps({
  type: { type: String, default: 'line' },
  data: { type: Object, required: true },
  options: { type: Object, default: () => ({}) },
  height: { type: Number, default: 180 }
})

const canvasRef = ref(null)
let chart = null

function mergeChartOptions(base, extra) {
  return {
    ...base,
    ...extra,
    plugins: {
      ...(base.plugins ?? {}),
      ...(extra.plugins ?? {}),
      legend: {
        ...(base.plugins?.legend ?? {}),
        ...(extra.plugins?.legend ?? {}),
        labels: {
          ...(base.plugins?.legend?.labels ?? {}),
          ...(extra.plugins?.legend?.labels ?? {})
        }
      },
      tooltip: {
        ...(base.plugins?.tooltip ?? {}),
        ...(extra.plugins?.tooltip ?? {}),
        callbacks: {
          ...(base.plugins?.tooltip?.callbacks ?? {}),
          ...(extra.plugins?.tooltip?.callbacks ?? {})
        }
      }
    },
    scales: {
      ...(base.scales ?? {}),
      ...(extra.scales ?? {}),
      x: {
        ...(base.scales?.x ?? {}),
        ...(extra.scales?.x ?? {})
      },
      y: {
        ...(base.scales?.y ?? {}),
        ...(extra.scales?.y ?? {})
      }
    }
  }
}

function loadChartJs() {
  if (window.Chart) return Promise.resolve(window.Chart)

  return new Promise((resolve, reject) => {
    const existing = document.querySelector('script[data-chartjs]')
    if (existing) {
      existing.addEventListener('load', () => resolve(window.Chart), { once: true })
      existing.addEventListener('error', reject, { once: true })
      return
    }

    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/chart.js@4.4.9/dist/chart.umd.min.js'
    script.async = true
    script.dataset.chartjs = 'true'
    script.onload = () => resolve(window.Chart)
    script.onerror = reject
    document.head.appendChild(script)
  })
}

async function renderChart() {
  await nextTick()
  if (!canvasRef.value) return

  const Chart = await loadChartJs()
  chart?.destroy()
  const baseOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          labels: { color: '#c9d7eb', boxWidth: 10, boxHeight: 10 }
        }
      },
      scales: {
        x: {
          ticks: { color: '#9fb2cb' },
          grid: { color: 'rgba(117, 151, 194, 0.12)' }
        },
        y: {
          ticks: { color: '#9fb2cb' },
          grid: { color: 'rgba(117, 151, 194, 0.12)' }
        }
      },
    }

  chart = new Chart(canvasRef.value, {
    type: props.type,
    data: props.data,
    options: mergeChartOptions(baseOptions, props.options)
  })
}

onMounted(renderChart)
watch(() => [props.type, props.data, props.options], renderChart, { deep: true })

onBeforeUnmount(() => {
  chart?.destroy()
})
</script>

<template>
  <div class="chart-js-panel" :style="{ height: `${height}px` }">
    <canvas ref="canvasRef"></canvas>
  </div>
</template>

<style scoped>
.chart-js-panel {
  position: relative;
  width: 100%;
  min-height: 0;
}

.chart-js-panel canvas {
  display: block;
}
</style>
