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
          :items="menus"
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
import { computed, ref } from 'vue'
import { LogoutOutlined } from '@ant-design/icons-vue'
import { MenuProps, message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { userLogoutUsingPost } from '@/api/userController.ts'

const loginUserStore = useLoginUserStore()
const router = useRouter()
//  region 菜单项权限过滤
import { routes } from '@/access/routes.ts'
import checkAccess from '@/access/checkAccess.ts'

// 把路由项转换为菜单项
const menuToRouteItem = (item: any) => {
  return {
    key: item.key, // 用于路由的跳转
    label: item.label,
    title: item.title,
    icon: item.icon ?? undefined,
  }
}

// 过滤菜单项
const items = computed(() => {
  return routes
    .filter((item) => {
      if (item.meta?.hideInMenu) {
        return false
      }
      console.log(item.meta?.access)
      // 根据权限过滤菜单，有权限则返回 true，则保留该菜单
      return checkAccess(loginUserStore.loginUser, item.meta?.access as string)
    })
    .map(menuToRouteItem) // 转换为菜单项格式
})
const menus = ref<MenuProps['items']>(items)
// endregion

/**
 * 路由跳转事件
 */

const doMenuClick = ({ key }) => {
  router.push({ path: key })
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
