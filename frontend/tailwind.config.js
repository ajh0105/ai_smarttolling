/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        deep: '#080C18',
        navy: '#0B1840',
        brand: '#1B3BE8',
        sky: '#38BEF5',
        cloud: '#E8EFFE',
        line: 'rgba(11,24,64,.12)'
      },
      fontFamily: {
        headline: ['"Big Shoulders Display"', 'sans-serif'],
        mono: ['"Fira Mono"', 'monospace'],
        body: ['"Noto Sans KR"', 'sans-serif']
      }
    }
  },
  plugins: []
}
