import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import MyVipPage from '@/pages/vip/MyVipPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import accessEnum from '@/access/accessEnum.ts'
import NoAuth from '@/pages/NoAuth.vue'
import AddPicture from '@/pages/picture/AddPicture.vue'
import PictureManagePage from '@/pages/admin/PictureManagePage.vue'
import PictureDetailPage from '@/pages/picture/PictureDetailPage.vue'
/*
  路由
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomePage,
      meta: {
        access: accessEnum.NOT_LOGIN,
      },
    },
    {
      path: '/user/login',
      name: '登录页面',
      component: UserLoginPage,
      meta: {
        access: accessEnum.NOT_LOGIN,
      },
    },
    {
      path: '/user/register',
      name: '注册页面',
      component: UserRegisterPage,
      meta: {
        access: accessEnum.NOT_LOGIN,
      },
    },
    {
      path: '/admin/userManage',
      name: '管理员用户管理页面',
      component: UserManagePage,
      meta: {
        access : accessEnum.ADMIN,
      },
    },
    {
      path: '/admin/pictureManage',
      name: '图片管理页面',
      component: PictureManagePage,
      meta: {
        access : accessEnum.ADMIN,
      },
    },
    {
      path: '/vip/myVIP',
      name: '我的会员信息',
      component: MyVipPage,
      meta: {
        access: [accessEnum.VIP,accessEnum.ADMIN],
      },
    },
    {
      path: '/add_picture',
      title: '图片模块',
      component: AddPicture,
      meta: {
        access: accessEnum.USER,
      }
    },
    {
      path: '/picture/:id',
      name: '图片详情',
      component: PictureDetailPage,
      props: true,
      meta: {
        access: accessEnum.NOT_LOGIN,
      }
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
      meta: {
        access: accessEnum.NOT_LOGIN,
      },
    },
    {
      path: '/noAuth',
      name: '无权访问',
      component: NoAuth,
      meta: {
        access: accessEnum.NOT_LOGIN,
      }
    },
    // 添加通配符路由，匹配所有未定义的路径
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: NoAuth ,
    }
  ],
})

export default router
