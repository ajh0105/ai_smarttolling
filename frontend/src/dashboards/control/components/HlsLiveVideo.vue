<script setup>
import Hls from 'hls.js'
import { onBeforeUnmount, onMounted, shallowRef, watch } from 'vue'

const props = defineProps({
  src: { type: String, default: '' },
  live: { type: Boolean, default: false }
})

const emit = defineEmits(['error', 'fps'])
const videoRef = shallowRef(null)

const LIVE_EDGE_DRIFT_SEC = 12
const STALL_RECOVERY_INTERVAL_MS = 5000

let hls = null
let stalledAt = 0
let frameCallbackId = 0
let frameCount = 0
let frameWindowStartedAt = 0
let fpsTimer = null
let previousFrameTotal = 0

function getRenderedFrameTotal(video) {
  const quality = video.getVideoPlaybackQuality?.()
  return quality?.totalVideoFrames ?? video.webkitDecodedFrameCount ?? 0
}

function stopFpsCounter() {
  const video = videoRef.value
  if (frameCallbackId && video?.cancelVideoFrameCallback) {
    video.cancelVideoFrameCallback(frameCallbackId)
  }
  if (fpsTimer) window.clearInterval(fpsTimer)
  frameCallbackId = 0
  fpsTimer = null
  frameCount = 0
  frameWindowStartedAt = 0
  previousFrameTotal = 0
}

function onVideoFrame(now) {
  frameCount += 1
  if (!frameWindowStartedAt) frameWindowStartedAt = now

  const elapsed = now - frameWindowStartedAt
  if (elapsed >= 1000) {
    emit('fps', Number(((frameCount * 1000) / elapsed).toFixed(1)))
    frameCount = 0
    frameWindowStartedAt = now
  }

  const video = videoRef.value
  if (video?.requestVideoFrameCallback) {
    frameCallbackId = video.requestVideoFrameCallback(onVideoFrame)
  }
}

function startFpsCounter(video) {
  stopFpsCounter()
  if (video.requestVideoFrameCallback) {
    frameCallbackId = video.requestVideoFrameCallback(onVideoFrame)
    return
  }

  previousFrameTotal = getRenderedFrameTotal(video)
  fpsTimer = window.setInterval(() => {
    const total = getRenderedFrameTotal(video)
    const fps = Math.max(0, total - previousFrameTotal)
    previousFrameTotal = total
    if (fps > 0) emit('fps', fps)
  }, 1000)
}

function destroyPlayer() {
  stopFpsCounter()
  if (hls) {
    hls.destroy()
    hls = null
  }
  if (videoRef.value) {
    videoRef.value.removeAttribute('src')
    videoRef.value.load()
  }
}

function play(video) {
  video.play().catch(() => {})
}

function syncToLiveIfNeeded(video) {
  const livePosition = hls?.liveSyncPosition
  if (!Number.isFinite(livePosition)) return
  if (livePosition - video.currentTime > LIVE_EDGE_DRIFT_SEC) {
    video.currentTime = livePosition
  }
}

function hasBufferedAhead(video) {
  for (let index = 0; index < video.buffered.length; index += 1) {
    if (video.currentTime >= video.buffered.start(index) - 0.05 && video.currentTime < video.buffered.end(index) - 0.25) {
      return true
    }
  }
  return false
}

function recoverHlsError(data) {
  const video = videoRef.value
  if (!video || !hls) return

  if (data?.type === Hls.ErrorTypes.NETWORK_ERROR) {
    hls.startLoad(-1)
    play(video)
    return
  }

  if (data?.type === Hls.ErrorTypes.MEDIA_ERROR) {
    hls.recoverMediaError()
    play(video)
    return
  }

  emit('error')
}

function handleStalled() {
  const now = Date.now()
  if (now - stalledAt < STALL_RECOVERY_INTERVAL_MS) return
  stalledAt = now

  const video = videoRef.value
  if (!video) return
  hls?.startLoad(-1)
  if (!hasBufferedAhead(video)) syncToLiveIfNeeded(video)
  play(video)
}

function attachPlayer() {
  const video = videoRef.value
  destroyPlayer()
  if (!video || !props.src) return

  video.muted = true
  video.playsInline = true
  video.preload = 'auto'
  startFpsCounter(video)

  if (video.canPlayType('application/vnd.apple.mpegurl')) {
    video.src = props.src
    play(video)
    return
  }

  if (!Hls.isSupported()) {
    emit('error')
    return
  }

  hls = new Hls({
    lowLatencyMode: false,
    backBufferLength: 20,
    maxBufferLength: 30,
    maxMaxBufferLength: 45,
    liveSyncDurationCount: 6,
    liveMaxLatencyDurationCount: 12,
    maxLiveSyncPlaybackRate: 1.05,
    maxFragLookUpTolerance: 0.25,
    nudgeOffset: 0.1,
    nudgeMaxRetry: 5,
    startFragPrefetch: true,
    manifestLoadingTimeOut: 10000,
    levelLoadingTimeOut: 10000,
    fragLoadingTimeOut: 15000
  })
  hls.on(Hls.Events.ERROR, (_, data) => {
    if (data?.details === Hls.ErrorDetails.BUFFER_STALLED_ERROR) {
      handleStalled()
      return
    }
    if (data?.fatal) recoverHlsError(data)
  })
  hls.on(Hls.Events.MANIFEST_PARSED, () => play(video))
  hls.on(Hls.Events.LEVEL_UPDATED, () => syncToLiveIfNeeded(video))
  hls.on(Hls.Events.FRAG_BUFFERED, () => play(video))
  hls.loadSource(props.src)
  hls.attachMedia(video)
}

watch(() => props.src, attachPlayer)
onMounted(attachPlayer)
onBeforeUnmount(destroyPlayer)
</script>

<template>
  <video
    ref="videoRef"
    class="dashboard-live-frame"
    autoplay
    muted
    playsinline
    @error="emit('error')"
    @stalled="handleStalled"
  />
</template>
