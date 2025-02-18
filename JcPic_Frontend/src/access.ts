/**
 * 前端权限控制
 */

import router from '@/router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'

// 首次获取信息
let firstFetch = true

router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser
  if (firstFetch) {
    // 页面首次加载时，等待后端返回信息再校验用户权限
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
    firstFetch = false
  }
  const toUrl = to.fullPath

  // 权限校验逻辑部分
  if (toUrl.startsWith('/admin')) {
    // 用户访问了管理员权限部分
    if (!loginUser || loginUser.userRole !== 'admin') {
      message.error('无权访问')
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
  }
  next()
})
