<template >
  <div id="NoAuthPage" >
<!--    <span class="WarningMes">这里不是你该来的地方! </span>-->
    <a-image :width="500" :preview="false" src="/get_out.png"/>
    <span class="WarningMes"> 从我家里出去！</span>
  </div>
</template>
<script setup lang="ts">
</script>

<style scoped>
#NoAuthPage {
  height: 60vh;
  display: grid;
  place-items: center;
  align-items: center;
}
.WarningMes {
  color: #559999;
  font-size: 72px;
}
</style>
