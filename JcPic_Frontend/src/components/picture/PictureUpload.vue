<template>
  <div id="pictureUploadPage">
    <h1>{{ msg }}</h1>
    <div class="picture_upload">
      <a-upload
        list-type="picture-card"
        :show-upload-list="false"
        :custom-request="handleUpload"
        :before-upload="beforeUpload"
      >
        <img v-if="picture?.url" :src="picture?.url" alt="avatar" />
        <div v-else>
          <loading-outlined v-if="loading"></loading-outlined>
          <plus-outlined v-else></plus-outlined>
          <div class="ant-upload-text">点击或拖拽上传</div>
        </div>
      </a-upload>
    </div>
  </div>
</template>
<script setup lang="ts">
import { uploadPictureUsingPost } from '@/api/pictureController.ts'

const msg = '图片上传组件,正在开发中.....'
import { ref } from 'vue'
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import type { UploadChangeParam, UploadProps } from 'ant-design-vue'

interface Props {
  picture?: API.PictureVO
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()

const handleUpload = async ({file} : any) => {
  loading.value = true
  const params = props.picture ? {id: props.picture.id } : {}
  try {
    const res = await uploadPictureUsingPost(params,{ }, file )
    if (res.data.code === 0 && res.data.data ) {
      message.success("上传函数调用成功")
      // 通知父组件上传成功
      props.onSuccess?.(res.data.data)
    }else{
      message.error("上传失败, " + res.data.message )
    }
  }catch (error) {
    console.log("上传失败,"+ error)
    message.error("图片上传失败, "+ error.message)
  }
  loading.value = false
}

const loading = ref<boolean>(false)


const beforeUpload = (file: UploadProps['fileList'][number]) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('当前上传格式不支持, 请使用 jpeg 或 png 格式')
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片尺寸过大,不得超过 2MB!')
  }
  return isJpgOrPng && isLt2M
}
</script>

<style scoped>
#pictureUploadPage {
}
.picture_upload :deep(.ant-upload) {
  width: 100% !important;
  min-width: 150px;
  height: 100% !important;
  min-height: 150px;
}
.picture_upload img {
  max-width: 100%;
  max-height: 500px;
}
.avatar-uploader > .ant-upload {
  width: 128px;
  height: 128px;
}

.ant-upload-select-picture-card i {
  font-size: 32px;
  color: #999;
}


.ant-upload-select-picture-card .ant-upload-text {
  margin-top: 8px;
  color: #666;
}
</style>
