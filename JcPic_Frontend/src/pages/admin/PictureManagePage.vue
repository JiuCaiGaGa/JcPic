<template>
  <div id="pictureManagePage">
    <!--    region 搜索栏-->
    <a-form layout="inline" :model="searchParams" @finish="doSearch" class="search_form">
      <a-form-item label="关键词">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="输入搜索内容（名称和简介）"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="类型">
        <a-input v-model:value="searchParams.category" placeholder="输入图片类型" allow-clear />
      </a-form-item>
      <a-form-item label="格式">
        <a-input v-model:value="searchParams.picFormat" placeholder="输入图片后缀" allow-clear />
      </a-form-item>
      <a-form-item label="标签">
        <a-select
          v-model:value="searchParams.tags"
          mode="tags"
          placeholder="标签"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <!--    endregion-->
    <div style="margin-bottom: 20px" />
    <!--    region 表格-->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'url'">
          <a-image :src="record.url" :width="80" />
        </template>
        <template v-if="column.dataIndex === 'tags'">
          <a-space wrap>
            <a-tag v-for="tag in JSON.parse(record.tags || '[]')" :key="tag">
              {{ tag }}
            </a-tag>
          </a-space>
        </template>
        <template v-if="column.dataIndex === 'picInfo'">
          <div>
            格式：<strong>{{ record.picFormat }}</strong>
          </div>
          <div>
            宽度：<strong>{{ record.picWidth }}</strong>
          </div>
          <div>
            高度：<strong>{{ record.picHeight }}</strong>
          </div>
          <div>
            宽高比：<strong>{{ record.picScale }}</strong>
          </div>
          <div>
            大小：<strong>{{ (record.picSize / 1024).toFixed(2) }}KB</strong>
          </div>
        </template>

        <template v-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD') }}
        </template>
        <template v-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD') }}
        </template>
        <template v-else-if="column.dataIndex === 'userId'">
          <a-button type="link" :href="`/userDetail?id=${record.userId}`" target="_blank"
            >用户主页</a-button
          >
        </template>
        <template v-else-if="column.key === 'action'">
          <div v-if="record.pictureRole !== 'admin'">
            <a-button type="link" :href="`/add_picture?id=${record.id}`" target="_blank"
              >编辑</a-button
            >
            <a-button danger @click="doDelete(record.id)">删除</a-button>
          </div>
          <div v-else-if="record.pictureRole === 'admin'">
            <span>不可操作</span>
          </div>
        </template>
      </template>
    </a-table>
    <!--    endregion-->
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { deletePictureUsingPost, listPictureByPageUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: '图片',
    dataIndex: 'url',
  },
  {
    title: '名称',
    dataIndex: 'name',
  },
  {
    title: '简介',
    dataIndex: 'introduction',
    ellipsis: true,
  },
  {
    title: '类型',
    dataIndex: 'category',
  },
  {
    title: '标签',
    dataIndex: 'tags',
  },
  {
    title: '图片信息',
    dataIndex: 'picInfo',
  },
  {
    title: '用户id',
    dataIndex: 'userId',
    width: 80,
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '编辑时间',
    dataIndex: 'editTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const dataList = ref<API.PictureVO[]>([])
const total = ref(0)

const searchParams = reactive<API.PictureQueryRequest>({
  currentPage: 1,
  pageSize: 5,
  sortField: 'createTime',
  sortOrder: 'ascend', // 降序
})

//分页器
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: Number(total.value),
    showSizeChanger: true,
    showTotal: (total) => `共${total}条`,
  }
})

const fetchData = async () => {
  const res = await listPictureByPageUsingPost({
    ...searchParams,
  })

  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败....\n 请稍后再试')
  }
}

// 页面加载时请求数据
onMounted(() => {
  fetchData()
})

const doTableChange = async (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  await fetchData()
}

const doSearch = async () => {
  searchParams.current = 1
  await fetchData()
}

const doDelete = async (id: number) => {
  if (!id) return
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    await fetchData()
  } else {
    message.error('删除失败')
  }
}
</script>

<style scoped></style>
