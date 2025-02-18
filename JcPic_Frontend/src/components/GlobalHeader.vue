<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="140px">
        <router-link to="/">
          <div id="titleBar" class="titleBar">
            <img class="logo" src="../assets/logo.png" alt="JcPic" />
            <div class="title">JcPic</div>
          </div>
        </router-link>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="current"
          mode="horizontal"
          :items="items"
          @click="doMenuClick"
        />
      </a-col>
      <a-col flex="100px">
        <div class="user_login_status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '佚名' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button href="/user/login" type="primary">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { computed, h, ref } from 'vue'
import { HomeOutlined, GithubOutlined, LogoutOutlined } from '@ant-design/icons-vue'
import { MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { userLogoutUsingPost } from '@/api/userController.ts'

const loginUserStore = useLoginUserStore()

// 原始菜单项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/about',
    label: '关于',
    title: '关于',
  },
  {
    key: 'JcGithub',
    icon: () => h(GithubOutlined),
    label: h(
      'a',
      { href: 'https://github.com/JiuCaiGaGa/JcPic', target: '_blank' },
      '韭菜gaga的GitHub主页',
    ),
    title: 'JcGaG_GitHub',
  },
]

const items = computed(() => filterMenus(originItems))

const router = useRouter()
// 对原始菜单项过滤
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    if (menu?.key?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if(!loginUser || loginUser.userRole !== 'admin'){
        return false
      }
    }
    return true
  })
}

/**
 * 路由跳转事件
 */

const doMenuClick = ({ key }) => {
  if (key !== 'JcGithub') {
    router.push({ path: key })
  }
}

/**
 * 高亮的菜单项
 */
const current = ref<string[]>([])
router.afterEach((to, from, next) => {
  current.value = [to.path]
})

const doLogout = async () => {
  const res = await userLogoutUsingPost()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push({
      path: '/',
      replace: true,
    })
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
#globalHeader .titleBar {
  display: flex;
  align-items: center;
}

.logo {
  height: 48px;
}

.title {
  font-size: 28px;
  color: black;
  margin-left: 20px;
}
</style>
