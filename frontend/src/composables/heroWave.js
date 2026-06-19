/**
 * Hero 섹션의 wave canvas 애니메이션.
 * 기존 index.html 의 drawWave 를 외부 모듈로 분리.
 */
export function createHeroWave(canvas) {
  if (!canvas) return null
  const ctx = canvas.getContext('2d')
  let waveW = 0
  let waveH = 0
  let spike = 0
  let raf = null

  function resize() {
    const dpr = window.devicePixelRatio || 1
    waveW = canvas.clientWidth
    waveH = canvas.clientHeight
    canvas.width = waveW * dpr
    canvas.height = waveH * dpr
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
  }

  function draw(time) {
    ctx.clearRect(0, 0, waveW, waveH)
    ctx.globalAlpha = 0.16
    for (let y = 110; y < waveH; y += 86) {
      ctx.beginPath()
      for (let x = 0; x <= waveW; x += 10) {
        const amp = 20 + Math.sin(time * 0.001 + y) * 8 + spike
        const yy = y + Math.sin(x * 0.012 + time * 0.0016 + y * 0.01) * amp
        if (x === 0) ctx.moveTo(x, yy)
        else ctx.lineTo(x, yy)
      }
      ctx.strokeStyle = y % 172 === 0 ? '#1B3BE8' : '#38BEF5'
      ctx.lineWidth = 2
      ctx.stroke()
    }
    ctx.globalAlpha = 0.9
    for (let i = 0; i < 7; i++) {
      const x = (time * 0.045 + i * 220) % (waveW + 180) - 80
      const y = waveH * (0.22 + i * 0.09) + Math.sin(time * 0.001 + i) * 28
      ctx.beginPath()
      ctx.arc(x, y, 3 + spike * 0.08, 0, Math.PI * 2)
      ctx.fillStyle = i % 2 ? '#38BEF5' : '#1B3BE8'
      ctx.fill()
    }
    spike *= 0.92
    raf = requestAnimationFrame(draw)
  }

  const onResize = () => resize()
  const onPointer = () => { spike = 26 }
  window.addEventListener('resize', onResize)
  window.addEventListener('pointerdown', onPointer)

  resize()
  raf = requestAnimationFrame(draw)

  return {
    dispose() {
      if (raf) cancelAnimationFrame(raf)
      window.removeEventListener('resize', onResize)
      window.removeEventListener('pointerdown', onPointer)
    }
  }
}
