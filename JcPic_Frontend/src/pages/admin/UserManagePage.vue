<template>
  <div id="userManagePage">

<!--    region 搜索栏-->
    <a-form layout="inline" :model="searchParams" @finish="doSearch" class="search_form">
      <a-form-item label="账号">
        <a-input v-model:value="searchParams.userAccount" placeholder="输入账号" allow-clear/>
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchParams.userName" placeholder="输入用户名" allow-clear/>
      </a-form-item>
      <a-form-item >
        <a-button type="primary" html-type="submit" >搜索</a-button>
      </a-form-item>
    </a-form>
<!--    endregion-->
    <div style="margin-bottom: 20px"/>
<!--    region 表格-->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" :width="80" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="black">【管理员】</a-tag>
          </div>
          <div v-else-if="record.userRole === 'vip'">
            <a-tag color="orange">【VIP】</a-tag>
          </div>
          <div v-else>
            <a-tag color="green">【普通用户】</a-tag>
          </div>
          <a-tag></a-tag>
        </template>
        <template v-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <div v-if="record.userRole !== 'admin'">
            <a-button danger @click="doDelete(record.id)">删除</a-button>
          </div>
          <div v-else-if="record.userRole === 'admin'">
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
import { deleteUserUsingPost, listUserVoByPageUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'id',
    dataIndex: 'id',
  },
  {
    title: '账号',
    dataIndex: 'userAccount',
  },
  {
    title: '用户名',
    dataIndex: 'userName',
  },
  {
    title: '头像',
    dataIndex: 'userAvatar',
  },
  {
    title: '简介',
    dataIndex: 'userProfile',
  },
  {
    title: '用户角色',
    dataIndex: 'userRole',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const dataList = ref<API.UserVO[]>([])
const total = ref(0)

const searchParams = reactive<API.UserQueryRequest>({
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
  const res = await listUserVoByPageUsingPost({
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
  if(!id) return
  const res = await deleteUserUsingPost({id})
  if (res.data.code === 0) {
    message.success("删除成功")
    await fetchData()
  }else {
    message.error("删除失败")
  }
}
</script>

<style scoped>
</style>
