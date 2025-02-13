import { ref } from 'vue'
import { defineStore } from 'pinia'

/**
 * 存储登陆用户信息
 */
export const useLoginUserStore = defineStore('loginUser', () => {
  // 初始值
  const loginUser = ref<any>({
    userName: '未登录',
  })

  async function fetchLoginUser() {
    // todo 获取后端登录用户信息


    /**
     * 临时测试 记得删除
     */
    setTimeout(() => {
      loginUser.value = {
        userName: 'Test',
        id : -1
      }

    },3000)

  }

  function setLoginUser(newLoginUser: any) {
    loginUser.value = newLoginUser
  }

  return {loginUser,fetchLoginUser,setLoginUser}
})
