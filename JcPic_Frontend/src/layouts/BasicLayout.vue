<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout>
        <a-layout-content class="content">
          <router-view />
        </a-layout-content>
      </a-layout>
      <a-layout-footer class="footer">
        <GithubOutlined />
        <a href="https://github.com/JiuCaiGaGa/JcPic" target="_blank">
          JcPic by 韭菜gaga, 欢迎访问我的 GitHub
        </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import { GithubOutlined } from '@ant-design/icons-vue'
</script>

<style scoped>
#basicLayout .header {
  padding-inline: 20px;
  background: white;
  color: unset;
  margin-bottom: 16px;
}

#basicLayout .content {
  padding: 20px;
  background: linear-gradient(to right, #abcdef, #efefef);
  margin-bottom: 45px;
}

#basicLayout .footer {
  background: #f5f5f5;
  padding: 20px;
  position: fixed;
  bottom: 0;
  right: 0;
  left: 0;
  text-align: center;
}
</style>
