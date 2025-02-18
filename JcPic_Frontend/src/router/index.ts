import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import MyVipPage from '@/pages/vip/MyVipPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: '登录页面',
      component: UserLoginPage,
    },
    {
      path: '/user/register',
      name: '注册页面',
      component: UserRegisterPage,
    },
    {
      path: '/admin/userManage',
      name: '管理员用户管理页面',
      component: UserManagePage,
    },
    {
      path: '/vip/myVIP',
      name: '我的会员信息',
      component: MyVipPage,
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

export default router
