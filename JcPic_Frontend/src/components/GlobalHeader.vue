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
      <a-col flex="80px">
        <div class="user_login_status">
          <div v-if="loginUserStore.loginUser.id">
            {{ loginUserStore.loginUser.username ?? '佚名' }}
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
import { h, ref } from 'vue'
import { HomeOutlined, GithubOutlined } from '@ant-design/icons-vue'
import { MenuProps } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

const loginUserStore = useLoginUserStore()

const items = ref<MenuProps['items']>([
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
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
])

const router = useRouter()

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
