<template>
  <div id="AddPictureBatchPage">
    <h2 style="margin-bottom: 20px">批量创建图片</h2>
    <!--    图片信息表单-->
    <a-form name="formData" layout="vertical" :model="formData" @finish="handleSubmit">
      <a-form-item label="关键词" name="searchText">
        <a-input v-model:value="formData.searchText" placeholder="输入关键词" allow-clear />
      </a-form-item>
      <a-form-item label="抓取数量" name="count">
        <a-input-number
          v-model:value="formData.count"
          placeholder="输入抓取数量"
          :min="1"
          :max="15"
          allow-clear
          :autoSize="{ minRows: 3, maxRows: 5 }"
        />
      </a-form-item>
      <a-form-item label="名称前缀" name="namePrefix">
        <a-input v-model:value="formData.namePrefix" placeholder="输入名称前缀" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%" :loading="loading">
          {{ loading ? '任务执行中' : '提交任务'}}
        </a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { uploadPictureByBatchUsingPost } from '@/api/pictureController.ts'
import { useRouter } from 'vue-router'

const picture = ref<API.PictureVO>()
const formData = reactive<API.PictureUploadByBatchRequest>({
  count : 5
})

const router = useRouter()
const loading = ref<boolean>(false)

// 处理批量抓取图片信息表单
const handleSubmit = async (values: any) => {
  loading.value = true

  // message.info(formData)
  const res = await uploadPictureByBatchUsingPost({
    ...formData,
  })
  //   操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success(`操作成功,已获取到${res.data.data}条数据`)
    router.push({
      path: `/`,
    })
  } else {
    message.error('操作失败, ' + res.data.message)
  }
  loading.value = false
}
</script>

<style scoped>
#AddPictureBatchPage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
