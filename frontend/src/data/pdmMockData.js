// 예지보전(PDM) 데모 목업 데이터
// VITE_DEMO_MODE=true 빌드 시 백엔드 없이 사용

function ts(minutesAgo) {
  return new Date(Date.now() - minutesAgo * 60 * 1000).toISOString()
}

export const mockSummary = {
  totalCameraCount: 6,
  normalCameraCount: 3,
  warningCameraCount: 2,
  criticalCameraCount: 1,
  averageHealthScore: 74.2
}

export const mockCameras = [
  {
    cameraId: 1,
    cameraCode: 'CAM-TG01-F',
    cameraName: '판교 TG 1번 전방',
    direction: 'FRONT',
    laneIds: [101, 102],
    laneNames: ['1차로', '2차로'],
    riskLevel: 'NORMAL',
    healthScore: 91,
    avgOcrConfidence: 0.934,
    successRate: 0.961,
    matchRate: 0.948,
    missingRate: 0.039,
    modelType: 'RULE_BASED',
    reasonText: 'Recognition quality is stable',
    recommendedAction: 'No immediate maintenance required'
  },
  {
    cameraId: 2,
    cameraCode: 'CAM-TG01-R',
    cameraName: '판교 TG 1번 후방',
    direction: 'REAR',
    laneIds: [101, 102],
    laneNames: ['1차로', '2차로'],
    riskLevel: 'WARNING',
    healthScore: 67,
    avgOcrConfidence: 0.812,
    successRate: 0.843,
    matchRate: 0.791,
    missingRate: 0.157,
    modelType: 'ISOLATION_FOREST',
    reasonText: 'Temporary mismatch spike detected',
    recommendedAction: 'Check recent camera frame delay and lens contamination'
  },
  {
    cameraId: 3,
    cameraCode: 'CAM-TG02-F',
    cameraName: '판교 TG 2번 전방',
    direction: 'FRONT',
    laneIds: [201, 202, 203],
    laneNames: ['1차로', '2차로', '3차로'],
    riskLevel: 'CRITICAL',
    healthScore: 38,
    avgOcrConfidence: 0.612,
    successRate: 0.648,
    matchRate: 0.573,
    missingRate: 0.352,
    modelType: 'LSTM_AE',
    reasonText: 'Long-term degradation trend detected',
    recommendedAction: 'Schedule camera inspection and lens replacement check'
  },
  {
    cameraId: 4,
    cameraCode: 'CAM-TG02-R',
    cameraName: '판교 TG 2번 후방',
    direction: 'REAR',
    laneIds: [201, 202, 203],
    laneNames: ['1차로', '2차로', '3차로'],
    riskLevel: 'WARNING',
    healthScore: 62,
    avgOcrConfidence: 0.778,
    successRate: 0.801,
    matchRate: 0.764,
    missingRate: 0.199,
    modelType: 'RULE_BASED',
    reasonText: 'Short repeated quality drop detected',
    recommendedAction: 'Inspect mounting vibration and focus state'
  },
  {
    cameraId: 5,
    cameraCode: 'CAM-TG03-F',
    cameraName: '분당 TG 전방',
    direction: 'FRONT',
    laneIds: [301, 302],
    laneNames: ['1차로', '2차로'],
    riskLevel: 'NORMAL',
    healthScore: 88,
    avgOcrConfidence: 0.911,
    successRate: 0.934,
    matchRate: 0.921,
    missingRate: 0.066,
    modelType: 'RULE_BASED',
    reasonText: 'Recognition quality is stable',
    recommendedAction: 'No immediate maintenance required'
  },
  {
    cameraId: 6,
    cameraCode: 'CAM-TG03-R',
    cameraName: '분당 TG 후방',
    direction: 'REAR',
    laneIds: [301, 302],
    laneNames: ['1차로', '2차로'],
    riskLevel: 'NORMAL',
    healthScore: 83,
    avgOcrConfidence: 0.893,
    successRate: 0.912,
    matchRate: 0.896,
    missingRate: 0.088,
    modelType: 'ISOLATION_FOREST',
    reasonText: 'Recognition quality is stable',
    recommendedAction: 'No immediate maintenance required'
  }
]

// 품질 추세 (카메라별, 최근 12버킷 × 10분)
function makeTrend(cameraId) {
  const baseOcr     = { 1: 93, 2: 81, 3: 61, 4: 78, 5: 91, 6: 89 }[cameraId] ?? 80
  const baseSuccess = { 1: 96, 2: 84, 3: 65, 4: 80, 5: 93, 6: 91 }[cameraId] ?? 82
  const baseMatch   = { 1: 95, 2: 79, 3: 57, 4: 76, 5: 92, 6: 90 }[cameraId] ?? 81
  return Array.from({ length: 12 }, (_, i) => {
    const noise = () => (Math.random() - 0.5) * 6
    return {
      bucketStart: ts((11 - i) * 10),
      avgOcrConfidence:  Math.min(1, Math.max(0, (baseOcr     + noise()) / 100)),
      successRate:       Math.min(1, Math.max(0, (baseSuccess  + noise()) / 100)),
      matchRate:         Math.min(1, Math.max(0, (baseMatch    + noise()) / 100))
    }
  })
}

export const mockQualityMetrics = {
  1: makeTrend(1),
  2: makeTrend(2),
  3: makeTrend(3),
  4: makeTrend(4),
  5: makeTrend(5),
  6: makeTrend(6)
}

export const mockAlerts = [
  {
    alertId: 1001,
    alertTitle: 'Rear Camera Degrade CRITICAL alert',
    cameraId: 3,
    cameraCode: 'CAM-TG02-F',
    laneId: 201,
    riskLevel: 'CRITICAL',
    status: 'OPEN',
    reasonText: 'Long-term degradation trend detected',
    createdAt: ts(18)
  },
  {
    alertId: 1002,
    alertTitle: 'Rear Camera Spike WARNING alert',
    cameraId: 2,
    cameraCode: 'CAM-TG01-R',
    laneId: 102,
    riskLevel: 'WARNING',
    status: 'CHECKING',
    reasonText: 'Temporary mismatch spike detected',
    createdAt: ts(35)
  },
  {
    alertId: 1003,
    alertTitle: 'Front Camera Pattern WARNING alert',
    cameraId: 4,
    cameraCode: 'CAM-TG02-R',
    laneId: null,
    riskLevel: 'WARNING',
    status: 'OPEN',
    reasonText: 'Short repeated quality drop detected',
    createdAt: ts(52)
  },
  {
    alertId: 1004,
    alertTitle: 'Front Camera Low Count WARNING alert',
    cameraId: 3,
    cameraCode: 'CAM-TG02-F',
    laneId: 203,
    riskLevel: 'WARNING',
    status: 'OPEN',
    reasonText: 'Event count is too low for reliable sequence analysis',
    createdAt: ts(71)
  },
  {
    alertId: 1005,
    alertTitle: 'Front Camera Legacy CRITICAL alert',
    cameraId: 3,
    cameraCode: 'CAM-TG02-F',
    laneId: null,
    riskLevel: 'CRITICAL',
    status: 'CHECKING',
    reasonText: 'Legacy API save verification',
    createdAt: ts(90)
  }
]

const plates = ['12가3456','34나7890','56다1234','78라5678','90마9012','11바3456','22사7890','33아1234']
function rp() { return plates[Math.floor(Math.random() * plates.length)] }

export const mockCompareResults = Array.from({ length: 20 }, (_, i) => {
  const matched = i % 5 !== 3
  const front = rp()
  const types = ['PLATE_MISMATCH', 'REAR_MISSING', 'FRONT_MISSING', 'LOW_CONFIDENCE']
  return {
    compareId: 2001 + i,
    eventGroupKey: `EVT-${String(2001 + i).padStart(6, '0')}`,
    laneName: ['1차로', '2차로', '3차로'][i % 3],
    frontPlateText: front,
    rearPlateText: matched ? front : (i % 5 === 4 ? null : rp()),
    isMatched: matched,
    mismatchType: matched ? null : types[i % types.length],
    confidenceGap: matched ? null : parseFloat((Math.random() * 0.3).toFixed(2)),
    comparedAt: ts(i * 1.5)
  }
})

export const mockFastApiStatus = {
  intervalSeconds: 300,
  analysisWindowMinutes: 30,
  bucketMinutes: 10,
  targets: 'RULE_BASED, ISOLATION_FOREST, LSTM_AE'
}
