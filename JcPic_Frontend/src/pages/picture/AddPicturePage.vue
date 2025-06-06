<template>
  <div id="AddPicturePage">
    <h2 style="margin-bottom: 20px">{{ route.query?.id ? '修改' : '创建' }}图片</h2>
    <a-typography-paragraph v-if="spaceId" type="secondary">
      保存至空间：<a :href="`/space/${spaceId}`" target="_blank">{{ spaceId }}</a>
    </a-typography-paragraph>
    <!-- 选择上传方式 -->
    <a-tabs v-model:activeKey="uploadType">
      <a-tab-pane key="file" tab="本地文件上传">
        <!--通过本地上传外部图片组件-->
        <PictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess" />
      </a-tab-pane>
      <a-tab-pane key="url" tab="外部图片引入" force-rende r>
        <!--    URL上传组件-->
        <UrlPictureUpload :picture="picture" :spaceId="spaceId" :onSuccess="onSuccess"/>
      </a-tab-pane>
    </a-tabs>

    <!--    图片信息表单-->
    <a-form
      v-if="picture"
      name="pictureForm"
      layout="vertical"
      :model="pictureForm"
      @finish="handleSubmit"
    >
      <a-form-item label="名称" name="name">
        <a-input v-model:value="pictureForm.name" placeholder="输入图片名称" allow-clear />
      </a-form-item>
      <a-form-item label="简介" name="introduction">
        <a-textarea
          v-model:value="pictureForm.introduction"
          placeholder="输入图片简介"
          allow-clear
          :autosize="{ minRows: 3, maxRows: 5 }"
        />
      </a-form-item>
      <a-form-item label="分类" name="category">
        <a-auto-complete
          v-model:value="pictureForm.category"
          placeholder="输入图片分类"
          allow-clear
          :options="categoryOptions"
        />
      </a-form-item>
      <a-form-item label="标签" name="tags">
        <a-select
          v-model:value="pictureForm.tags"
          mode="tags"
          placeholder="输入标签"
          allow-clear
          :options="tagOptions"
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit" style="width: 100%">提交信息</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>
<script setup lang="ts">
import PictureUpload from '@/components/picture/PictureUpload.vue'
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import {
  editPictureUsingPost,
  getPictureVoByIdUsingGet,
  listPictureTagCategoryUsingGet,
} from '@/api/pictureController.ts'
import { useRoute, useRouter } from 'vue-router'
import UrlPictureUpload from '@/components/picture/UrlPictureUpload.vue'

const picture = ref<API.PictureVO>()
const pictureForm = reactive<API.PictureEditRequest>({})
const categoryOptions = ref<string[]>([])
const tagOptions = ref<string[]>([])
const router = useRouter()
const route = useRoute()

// 标签页选择参数
const uploadType =  ref<'file' | 'url'>('file')
// 空间 id
const spaceId = computed(() => {
  return route.query?.spaceId
})
/**
 * 图片上传成功
 * @param newPicture
 */
const onSuccess = (newPicture: API.PictureVO) => {
  picture.value = newPicture
  pictureForm.name = newPicture.name
}
// 处理创建图片信息表单
const handleSubmit = async (values: any) => {
  message.info('调用成功')
  const picID = picture.value?.id
  if (!picID) {
    message.error('发生错误,请检查图片输入信息')
    return
  }

  const res = await editPictureUsingPost({
    id: picID,
    spaceId: spaceId.value,
    ...values,
  })
  //   操作成功
  if (res.data.code === 0 && res.data.data) {
    message.success('操作成功')
    router.push({
      path: `/picture/${picID}`,
    })
  } else {
    message.error('操作失败, ' + res.data.message)
  }
}

/**
 * 获取标签和分类
 */
const getTagAndCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    tagOptions.value = (res.data.data.tagList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
    categoryOptions.value = (res.data.data.categoryList ?? []).map((data: string) => {
      return {
        value: data,
        label: data,
      }
    })
  } else {
    message.error('获取信息失败, ' + res.data.message)
  }
}

onMounted(() => {
  getTagAndCategoryOptions()
})
/**
 * 获取链接中的图片信息
 */
const getOldPicture = async () => {
  const id = route.query?.id
  if (!id) return
  const res = await getPictureVoByIdUsingGet({
    id: id,
  })
  if (res.data.code === 0 && res.data.data) {
    const data = res.data.data
    picture.value = data
    pictureForm.name = data.name
    pictureForm.introduction = data.introduction
    pictureForm.category = data.category
    pictureForm.tags = data.tags
  }
}

onMounted(() => {
  getOldPicture()
})
</script>

<style scoped>
#AddPicturePage {
  max-width: 720px;
  margin: 0 auto;
}
</style>
