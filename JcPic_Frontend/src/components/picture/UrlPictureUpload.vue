<template>
  <div id="urlPictureUploadPage">
    <h1>{{ msg }}</h1>
    <div class="url-picture-upload">
      <a-input-group compact>
        <a-input
          v-model:value="fileUrl"
          style="width: calc(100% - 120px)"
          placeholder="请输入要上传的图片url"
        />
        <a-button type="primary" style="width: 120px" @click="handleUpload" :loading="loading"
          >提交</a-button
        >
      </a-input-group>
      <div class="img-wrapper">
        <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { uploadPictureByUrlUsingPost, uploadPictureUsingPost } from '@/api/pictureController.ts'

const msg = '方式2: 通过URL上传图片'
import { ref } from 'vue'
import { message } from 'ant-design-vue'

interface Props {
  picture?: API.PictureVO
  spaceId?: number
  onSuccess?: (newPicture: API.PictureVO) => void
}

const fileUrl = ref<string>()
const props = defineProps<Props>()

const handleUpload = async () => {
  loading.value = true
  const params: API.PictureUploadRequest = { fileUrl: fileUrl.value }
  params.spaceId = props.spaceId;
  if (props.picture) {
    params.id = props.picture.id
  }
  try {
    const res = await uploadPictureByUrlUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      message.success('上传函数调用成功')
      // 通知父组件上传成功
      props.onSuccess?.(res.data.data)
    } else {
      message.error('上传失败, ' + res.data.message)
    }
  } catch (error) {
    console.log('上传失败,' + error)
    message.error('图片上传失败, ' + error.message)
  }
  loading.value = false
}

const loading = ref<boolean>(false)

</script>

<style scoped>
#urlPictureUploadPage {
}

.url-picture-upload {
  margin-bottom: 16px;
}
.url-picture-upload img {
  max-width: 100%;
  max-height: 100%;
  margin: 0 auto;
}
.url-picture-upload .img-wrapper {
  text-align: center;
  margin-top: 16px;
}
</style>
