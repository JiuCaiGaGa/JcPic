<template>
  <div id="pictureDetailPage">
    <a-row :gutter="[16, 16]">
      <a-col :sm="24" :md="16" :xl="18">
        <a-card title="预览图">
          <a-image :src="picture.url" style="max-height: 600px; object-fit: contain" />
        </a-card>
      </a-col>
      <a-col :sm="24" :md="8" :xl="6">
        <a-card title="图片信息">
          <a-descriptions :column="1">
            <a-descriptions-item label="作者">
              <a-space>
                <a-avatar :size="24" :src="picture.user?.userAvatar" />
                <div>{{ picture.user?.userName }}</div>
              </a-space>
            </a-descriptions-item>
            <a-descriptions-item label="名称">
              {{ picture.name ?? '未命名' }}
            </a-descriptions-item>
            <a-descriptions-item label="简介">
              {{ picture.introduction ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="分类">
              {{ picture.category ?? '默认' }}
            </a-descriptions-item>
            <a-descriptions-item label="标签">
              <a-tag v-for="tag in picture.tags" :key="tag">
                {{ tag }}
              </a-tag>
            </a-descriptions-item>
            <a-descriptions-item label="格式">
              {{ picture.picFormat ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽度">
              {{ picture.picWidth ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="高度">
              {{ picture.picHeight ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="宽高比">
              {{ picture.picScale ?? '-' }}
            </a-descriptions-item>
            <a-descriptions-item label="大小">
              {{ formatSize(picture.picSize) }}
            </a-descriptions-item>
            <a-descriptions-item label="主色调">
              <a-space>
                {{ picture.picColor ?? '-' }}
                <div
                  v-if="picture.picColor"
                  :style="{
                    width: '16px',
                    height: '16px',
                    backgroundColor: toHexColor(picture.picColor),
                  }"
                />
              </a-space>
            </a-descriptions-item>
          </a-descriptions>
          <a-space wrap v-if="picture.id">
            <a-button type="primary" @click="doDownload">
              免费下载
              <template #icon>
                <DownloadOutlined />
              </template>
            </a-button>


            <div v-if="picture.reviewStatus!=PIC_REVIEW_STATUS_ENUM.REVIEWING">
              <a-button type="link" @click="handleReview(PIC_REVIEW_STATUS_ENUM.PASS)" v-if=" canEdit && picture.reviewStatus !== PIC_REVIEW_STATUS_ENUM.PASS"  >通过审核</a-button>
              <a-popconfirm
                title="确定要退回吗?"
                ok-text="是"
                cancel-text="否"
                @confirm="confirmREJECT"
              >
                <a-button type="link"  v-if="canEdit && picture.reviewStatus !== PIC_REVIEW_STATUS_ENUM.REJECT" danger>退回</a-button>
              </a-popconfirm>
            </div>
            <a-button :icon="h(ShareAltOutlined)" type="primary" ghost @click="doShare">
              分享
            </a-button>
            <a-button v-if="canEdit" type="default" @click="doEdit">编辑<template #icon><EditOutlined />
              </template>
            </a-button>
            <a-popconfirm
              title="确定要删除吗?"
              ok-text="删除"
              cancel-text="取消"
              @confirm="confirmDelete"
            >
            <a-button v-if="canDelete" :icon="h(DeleteOutlined)" danger @click="doDelete">
              <template #icon><DeleteOutlined /></template>
            </a-button>
            </a-popconfirm>
          </a-space>
        </a-card>
      </a-col>
    </a-row>
    <ShareModal ref="shareModalRef" :link="shareLink" />
  </div>
</template>

<script setup lang="ts">
import { deletePictureUsingPost, doPictureReviewUsingPost, getPictureVoByIdUsingGet } from '@/api/pictureController.ts'
import { DeleteOutlined, EditOutlined, DownloadOutlined, ShareAltOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { computed, h, onMounted, ref } from 'vue'
import { downloadImage, formatSize,toHexColor } from '@/utils'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { useRouter } from 'vue-router'
import { PIC_REVIEW_STATUS_ENUM } from '@/constants/picture.ts'
import ShareModal from '@/components/ShareModal.vue'
import { SPACE_PERMISSION_ENUM } from '@/constants/space.ts'
interface Props {
  id: string | number
}

const props = defineProps<Props>()
const picture = ref<API.PictureVO>({})

const router = useRouter()

// 通用权限检查函数
function createPermissionChecker(permission: string) {
  return computed(() => {
    return (picture.value.permissionList ?? []).includes(permission)
  })
}
// 定义权限检查
const canEdit = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_EDIT)
const canDelete = createPermissionChecker(SPACE_PERMISSION_ENUM.PICTURE_DELETE)


/**
 * 获取链接中的图片信息
 */
const fetchPictureDetail = async () => {
  try {
    const res = await getPictureVoByIdUsingGet({
      id: props.id,
    })
    if (res.data.code === 0 && res.data.data) {
      picture.value = res.data.data
    } else {
      message.error('获取图片详细信息失败' + res.data.message)
    }
  } catch (e: any) {
    message.error('获取图片详细信息失败' + e.message)
  }
}
onMounted(() => {
  fetchPictureDetail()
})
const doEdit = () => {
  router.push(`/add_picture?id=${picture.value.id}`)
  router.push({
    path: '/add_picture',
    query: {
      id: picture.value.id,
      spaceId: picture.value.spaceId,
    },
  })
}

const doDelete = async () => {
  const id = picture.value.id
  if (!id) return
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
  } else {
    message.error('删除失败')
  }
}

// 处理下载
const doDownload = () => {
  downloadImage(picture.value.url)
}

// 改变审核状态
const handleReview = async (reviewStatus: number) => {
  const id = picture.value.id
  if (!id) return
  const reviewMessage = reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? '管理员操作通过' : '管理员操作拒绝'
  const res = await doPictureReviewUsingPost({
    id: id,
    reviewStatus,
    reviewMessage,
  })
  if (res.data.code === 0) {
    message.success('审核操作成功')
    // 重新获取详情信息
    fetchPictureDetail()
  } else {
    message.error('审核操作失败，' + res.data.message)
  }
}

const confirmREJECT = (e: MouseEvent) => {
  handleReview(PIC_REVIEW_STATUS_ENUM.REJECT);
}
const confirmDelete = (e: MouseEvent) => {
  doDelete();
  fetchPictureDetail();
}
// ----- 分享操作 ----
const shareModalRef = ref()
// 分享链接
const shareLink = ref<string>()
// 分享
const doShare = () => {
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.value.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}
</script>

<style scoped>
#pictureDetailPage {
}
</style>
