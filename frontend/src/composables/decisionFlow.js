/**
 * Architecture 섹션의 Radial Decision Flow 캔버스 애니메이션.
 * 기존 index.html 의 createDecisionFlow 를 외부 모듈로 분리한 것.
 *
 * 사용법:
 *   const stop = createDecisionFlow(canvasEl)
 *   // 컴포넌트 unmount 시:
 *   stop?.dispose()
 */
export function createDecisionFlow(canvas) {
  if (!canvas) return null
  const ctx = canvas.getContext('2d')
  let width = 0
  let height = 0
  let tick = 0
  let raf = null

  const nodes = [
    { id: 'edge1', label: 'EDGE-01', rx: 0.12, ry: 0.20, type: 'edge' },
    { id: 'yolo1', label: 'YOLO',    rx: 0.29, ry: 0.22, type: 'yolo' },
    { id: 'ocr1',  label: 'OCR',     rx: 0.43, ry: 0.30, type: 'ocr' },
    { id: 'edge2', label: 'EDGE-02', rx: 0.10, ry: 0.50, type: 'edge' },
    { id: 'yolo2', label: 'YOLO',    rx: 0.27, ry: 0.50, type: 'yolo' },
    { id: 'ocr2',  label: 'OCR',     rx: 0.43, ry: 0.50, type: 'ocr' },
    { id: 'edge3', label: 'EDGE-03', rx: 0.12, ry: 0.80, type: 'edge' },
    { id: 'yolo3', label: 'YOLO',    rx: 0.29, ry: 0.78, type: 'yolo' },
    { id: 'ocr3',  label: 'OCR',     rx: 0.43, ry: 0.70, type: 'ocr' },
    { id: 'ingress',  label: 'Ingress',    rx: 0.60, ry: 0.50, type: 'ingress' },
    { id: 'spring',   label: 'SpringBoot', rx: 0.74, ry: 0.50, type: 'spring' },
    { id: 'postgres', label: 'PostgreSQL', rx: 0.87, ry: 0.34, type: 'postgres' },
    { id: 'vue',      label: 'Vue',        rx: 0.87, ry: 0.66, type: 'vue' }
  ]
  const edges = [
    { a: 'edge1', b: 'yolo1' }, { a: 'yolo1', b: 'ocr1' }, { a: 'ocr1', b: 'ingress' },
    { a: 'edge2', b: 'yolo2' }, { a: 'yolo2', b: 'ocr2' }, { a: 'ocr2', b: 'ingress' },
    { a: 'edge3', b: 'yolo3' }, { a: 'yolo3', b: 'ocr3' }, { a: 'ocr3', b: 'ingress' },
    { a: 'ingress', b: 'spring' },
    { a: 'spring',  b: 'postgres' },
    { a: 'spring',  b: 'vue' }
  ]
  const colors = {
    edge: 'rgba(255,92,92,.96)',
    yolo: 'rgba(255,202,58,.96)',
    ocr: 'rgba(75,214,255,.96)',
    ingress: 'rgba(70,255,180,.94)',
    spring: 'rgba(122,116,255,.96)',
    postgres: 'rgba(66,153,225,.96)',
    vue: 'rgba(52,211,153,.96)'
  }
  const sizes = { edge: 9, yolo: 10, ocr: 10, ingress: 14, spring: 14, postgres: 11, vue: 11 }
  const packets = edges.map((e, i) => ({ ...e, t: (i % 3) * 0.22, speed: 0.0044 + (i % 4) * 0.00028 }))
  const getNode = (id) => nodes.find((n) => n.id === id)
  const pos = (n) => ({ x: n.rx * width, y: n.ry * height })

  function resize() {
    const dpr = window.devicePixelRatio || 1
    width = canvas.clientWidth
    height = canvas.clientHeight
    canvas.width = width * dpr
    canvas.height = height * dpr
    ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
  }

  function draw() {
    if (!width || !height) { raf = requestAnimationFrame(draw); return }
    tick += 0.009
    ctx.clearRect(0, 0, width, height)

    const bg = ctx.createRadialGradient(width * 0.58, height * 0.5, 20, width * 0.58, height * 0.5, Math.max(width, height))
    bg.addColorStop(0, '#18266B'); bg.addColorStop(0.56, '#0B1840'); bg.addColorStop(1, '#080C18')
    ctx.fillStyle = bg
    ctx.fillRect(0, 0, width, height)

    ctx.strokeStyle = 'rgba(255,255,255,.045)'; ctx.lineWidth = 0.5
    for (let r = 72; r < Math.max(width, height); r += 72) {
      ctx.beginPath(); ctx.arc(width * 0.60, height * 0.50, r, 0, Math.PI * 2); ctx.stroke()
    }

    edges.forEach((edge) => {
      const a = pos(getNode(edge.a))
      const b = pos(getNode(edge.b))
      const to = getNode(edge.b)
      ctx.beginPath(); ctx.moveTo(a.x, a.y); ctx.lineTo(b.x, b.y)
      ctx.strokeStyle = colors[to.type].replace(/[\d.]+\)$/, '0.32)')
      ctx.lineWidth = (to.type === 'ingress' || to.type === 'spring') ? 1.8 : 1.2
      ctx.stroke()
    })

    packets.forEach((packet) => {
      packet.t += packet.speed
      if (packet.t > 1) packet.t = 0
      const a = pos(getNode(packet.a))
      const b = pos(getNode(packet.b))
      const to = getNode(packet.b)
      const px = a.x + (b.x - a.x) * packet.t
      const py = a.y + (b.y - a.y) * packet.t
      const color = colors[to.type]
      ctx.beginPath(); ctx.arc(px, py, 3.6, 0, Math.PI * 2); ctx.fillStyle = color; ctx.fill()
      const glow = ctx.createRadialGradient(px, py, 0, px, py, 18)
      glow.addColorStop(0, color.replace(/[\d.]+\)$/, '0.26)')); glow.addColorStop(1, 'transparent')
      ctx.fillStyle = glow; ctx.fillRect(px - 20, py - 20, 40, 40)
    })

    nodes.forEach((node) => {
      const p = pos(node)
      const color = colors[node.type]
      const size = sizes[node.type]
      const pulse = Math.sin(tick * 2.2 + (node.rx + node.ry) * 6) * 0.5 + 0.5
      ctx.beginPath(); ctx.arc(p.x, p.y, size + 5 + pulse * 5, 0, Math.PI * 2)
      ctx.strokeStyle = color.replace(/[\d.]+\)$/, '0.22)'); ctx.lineWidth = 1.2; ctx.stroke()
      const ng = ctx.createRadialGradient(p.x, p.y, 0, p.x, p.y, size + 18)
      ng.addColorStop(0, color.replace(/[\d.]+\)$/, '0.28)')); ng.addColorStop(1, 'transparent')
      ctx.fillStyle = ng; ctx.fillRect(p.x - size - 20, p.y - size - 20, (size + 20) * 2, (size + 20) * 2)
      ctx.beginPath(); ctx.arc(p.x, p.y, size, 0, Math.PI * 2); ctx.fillStyle = color; ctx.fill()
      ctx.strokeStyle = 'rgba(255,255,255,.72)'; ctx.lineWidth = 1; ctx.stroke()
      ctx.fillStyle = 'rgba(255,255,255,.84)'; ctx.font = '700 10px Fira Mono, monospace'
      ctx.textAlign = 'center'; ctx.fillText(node.label, p.x, p.y + size + 15)
    })

    const fast = pos(getNode('ingress'))
    ctx.beginPath(); ctx.arc(fast.x, fast.y, 54 + Math.sin(tick * 1.8) * 5, 0, Math.PI * 2)
    ctx.strokeStyle = 'rgba(70,255,180,.12)'; ctx.lineWidth = 1; ctx.stroke()

    raf = requestAnimationFrame(draw)
  }

  window.addEventListener('resize', resize)
  resize()
  raf = requestAnimationFrame(draw)

  return {
    dispose() {
      if (raf) cancelAnimationFrame(raf)
      window.removeEventListener('resize', resize)
    }
  }
}
