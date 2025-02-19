<template>
  <div id="app">
    <BasicLayout />
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
// import { healthUsingGet } from '@/api/mainController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

// 功能移动至 @/access/index
// const loginUserStore = useLoginUserStore();
// loginUserStore.fetchLoginUser();

// 健康检查
// healthUsingGet().then((res) => {
//   console.log(res)
// })

</script>

<style scoped></style>
