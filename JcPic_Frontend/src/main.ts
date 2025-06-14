import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue'
import VueCropper from 'vue-cropper';
import 'ant-design-vue/dist/reset.css'
import 'vue-cropper/dist/index.css'
import '@/access'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd)
app.use(VueCropper)


app.mount('#app')
