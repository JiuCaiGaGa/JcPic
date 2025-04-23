<template>
  <div id="userRegisterPage">
    <h2 class="title">JcPic _ 注册页面</h2>
    <div class="desc">欢迎使用</div>
    <a-form :model="formState" name="basic" autocomplete="off" @finish="handleSubmit">
      <a-form-item
        name="userAccount"
        :rules="[
          { required: true, message: '请输入你的账户名!' },
          { min: 3, message: '账户格式不正确' },
        ]"
      >
        <a-input v-model:value="formState.userAccount" placeholder="请输入账户名" />
      </a-form-item>

      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入你的密码!' },
          { min: 8, message: '密码格式不正确' },
          { pattern: /^(?=.*[a-zA-Z])(?=.*\d)[a-zA-Z\d]{8,}$/, message: '密码必须包含字母和数字' },
        ]"
      >
        <a-input-password v-model:value="formState.userPassword" placeholder="请输入密码" />
      </a-form-item>

      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请再次输入你的密码!' },
          { min: 8, message: '密码格式不正确' },
        ]"
      >
        <a-input-password v-model:value="formState.checkPassword" placeholder="请再次输入密码" />
      </a-form-item>
      <div class="tips">
        已有账号
        <RouterLink to="/user/login"> 去登录</RouterLink>
      </div>

      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">注册</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup lang="ts">
import { reactive } from 'vue'
import { userRegisterUsingPost } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import router from '@/router'

const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

const loginUserStore = useLoginUserStore()

const handleSubmit = async (values: any) => {
  if (values.checkPassword !== values.userPassword) {
    message.error('两次输入密码应保持一致!')
    return
  }
  try {
    const res = await userRegisterUsingPost(values)
    if (res.data.code === 0 && res.data.data) {
      message.success('注册成功')
      router.push({
        path: '/user/login',
        replace: true,
      })
    } else {
      message.error(res.data.message)
    }
  } catch (e) {
    message.error('注册操作失败,' + e.message)
  }
}
</script>

<style scoped>
#userRegisterPage {
  font-size: 30px;
  max-width: 360px;
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
