import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/main.css'
import './assets/index-styles/style.css'
import './assets/index-styles/subpage.css'
import './assets/index-styles/overrides.css'

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')
