export const demoChatResponses = [
  {
    keywords: ['어제 통행', '어제 통행량', '어제통행'],
    payload: {
      type: 'traffic-summary',
      loadingText: '어제 통행 데이터를 집계하는 중입니다.',
      loadingDuration: 3500,
      data: {
        title: '어제 통행 현황',
        badge: 'DAILY',
        primary: { label: '어제', value: 7936, suffix: '대' },
        comparison: { label: '평일 평균', value: 7500, suffix: '대' },
        change: { value: 5.8, suffix: '%', direction: 'up' },
        note: '평일 평균 7,500대 대비 5.8% 증가입니다.'
      }
    }
  },
  {
    keywords: [
      '알림 내역',
      '알림내역'
    ],
    payload: {
      type: 'alert-list',
      loadingText: '모든 엣지 상태를 확인 중입니다.',
      loadingDuration: 4000,
      data: {
        title: '알림 내역',
        summary: [
          { level: 'warn', count: 1 },
          { level: 'info', count: 1 }
        ],
        alerts: [
          {
            level: 'warn',
            time: '18:42',
            target: 'EDGE-GATE-03',
            title: 'OCR 드롭율 임계 초과',
            meta: '현재 0.13% / 임계 0.10% (+30%)',
            note: '영상 인식 품질 저하 — 점검 및 OCR 재학습 권고'
          },
          {
            level: 'info',
            time: '17:28',
            target: 'EDGE-GATE-07',
            title: 'LTE 백업망 → LAN 복구',
            meta: '12분 만에 자동 복구',
            note: ''
          }
        ]
      }
    }
  }
]

export const demoChatGreeting = '안녕하세요,\nHi-Five 통합 관제 어시스턴트입니다.\n무엇을 도와드릴까요?'

export const demoChatFallback = '시연 모드는 "어제 통행량", "알림 내역", "시연 페이지", "시연 점검" 같은 질문에 답할 수 있어요.'
