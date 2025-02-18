<template>
  <div id="homePage">
    <h1>{{ msg }}</h1>
  </div>
</template>
<script setup lang="ts">
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'

const msg = 'JcPic,正在开发中.....'
// const loginUserStore = useLoginUserStore()




</script>

<style scoped>
#homePage {
}
</style>
