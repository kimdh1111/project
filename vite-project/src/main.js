import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router';

createApp(App)
    .use(router)  // Vue Router 사용
    .mount('#app')
