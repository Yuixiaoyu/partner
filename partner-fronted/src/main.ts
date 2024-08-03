import {createApp} from 'vue'
import App from './App.vue'
// 2. 引入组件样式
import 'vant/lib/index.css';
import '../global.css'

import * as VueRouter from 'vue-router'
import routes from "./config/router";


const router = VueRouter.createRouter({
    // 4. 内部提供了 history 模式的实现。
    history: VueRouter.createWebHistory(),
    routes, // `routes: routes` 的缩写
})

const app = createApp(App)
app.use(router)
app.mount('#app')
