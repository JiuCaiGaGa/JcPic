<template>
  <div id="userLoginPage">
    <h2 class="title">JcPic _ 登陆页面</h2>
    <div class="desc">欢迎使用</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item
        name="userAccount"
        :rules="[{ required: true, message: 'Please input your username!' }]"
      >
        <a-input v-model:value="formState.userAccount" placeholder="请输入账户名" />
      </a-form-item>

      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: 'Please input your password!' },
          { min: 8, message: '密码格式不正确' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>
      <div class="tips">
        没有账号?
        <RouterLink to="/user/register"> 注册账号</RouterLink>
      </div>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">登录</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup lang="ts">
import { reactive } from 'vue'
import { userLoginUsingPost } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
  // remember: true,
})

const loginUserStore = useLoginUserStore()

const handleSubmit = async (values: any) => {
  try {
    const res = await userLoginUsingPost(values)
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('登陆成功')
      await router.push({
        path: '/',
        replace: true,
      })
    } else {
      message.error(res.data.message)
    }
  } catch (e) {
    message.error('登陆操作失败,' + e.message)
  }
}
</script>

<style scoped>
#userLoginPage {
  max-height: 360px;
  margin: 0 auto;
}

.title {
  text-align: center;
  margin-bottom: 16px;
}

.desc {
  text-align: center;
  color: #555;
  margin-top: 16px;
}

.tips {
  color: #999;
  text-align: right;
  font-size: 14px;
  margin-bottom: 16px;
}
</style>
