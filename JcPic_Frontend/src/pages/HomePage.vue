<template>
  <div id="homePage">
    <h1>{{ msg }}</h1>
  </div>
  <!--  region 顶部搜索框-->
  <div class="search-bar">
    <a-input-search
      v-model:value="searchParams.searchText"
      placeholder="搜索内容"
      enter-button="搜搜"
      size="large"
      @search="doSearch"
    />
  </div>
  <!--  endregion-->
  <!--  region 类型栏-->
  <a-tabs v-model:active-key="selectedCategory">
    <a-tab-pane tab="全部" key="all" />
    <a-tab-pane v-for="category in categoryList" :tab="category" :key="category" />
  </a-tabs>
  <div class="tag-bar">
    <span style="margin-right: 8px">标签: </span>
    <a-space :size="[0, 8]" wrap>
      <a-checkable-tag
        v-for="(tag, index) in tagList"
        :key="tag"
        v-model:checked="selectTagList[index]"
        @change="doSearch"
      >
        {{ tag }}
      </a-checkable-tag>
    </a-space>
  </div>
  <!--  endregion-->
  <!--  region 主页展示图片部分-->
  <a-list
    :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
    :data-source="dataList"
    :pagination="pagination"
    :loading="loading"
  >
    <template #renderItem="{ item: picture }">
      <a-list-item style="padding: 0">
        <a-card hoverable @click="doClick(picture)">
          <template #cover>
            <div class="image-container">
              <img
                style="height: 180px; object-fit: cover; width: 100%"
                :alt="picture.name"
                :src="picture.url"
              />
              <!-- 悬浮遮罩 -->
              <div class="overlay">
                <div class="overlay-content">
                  <h3>{{ picture.name }}</h3>
                  <p>{{ picture.description }}</p>
                  <a-flex>
                    <a-tag color="green">
                      {{ picture.category ?? '默认' }}
                    </a-tag>
                    <a-tag v-for="tag in picture.tags" :key="tag" color="yellow">
                      {{ tag }}
                    </a-tag>
                  </a-flex>
                </div>
              </div>
            </div>
          </template>
        </a-card>
      </a-list-item>
    </template>
  </a-list>
  <!--  endregion-->
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  listPictureTagCategoryUsingGet,
  listPictureVoByPageUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'

const msg = 'JcPic,正在开发中.....'
const dataList = ref<API.PictureVO[]>([])
const total = ref(0)
const loading = ref(true)

const categoryList = ref<string[]>([])
const tagList = ref<string[]>([])
const selectedCategory = ref<string>('all')
const selectTagList = ref<boolean[]>([])

const router = useRouter()

const searchParams = reactive<API.PictureQueryRequest>({
  currentPage: 1,
  pageSize: 6,
  sortField: 'createTime',
  sortOrder: 'ascend', // 降序
})

//分页器
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: Number(total.value),
    onChange: (page: number, pageSize: number) => {
      searchParams.current = page
      searchParams.pageSize = pageSize
      fetchData()
    },
  }
})

const fetchData = async () => {
  loading.value = true
  const params = {
    ...searchParams,
    tags: [],
  }
  if (selectedCategory.value !== 'all') {
    params.category = selectedCategory.value
  }
  selectTagList.value.forEach((useTag, index) => {
    if (useTag) {
      params.tags.push(tagList.value[index])
    }
  })
  const res = await listPictureVoByPageUsingPost(params)

  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('获取数据失败....\n 请稍后再试')
  }
  loading.value = false
}

// 页面加载时请求数据
onMounted(() => {
  fetchData()
})

const doSearch = () => {
  searchParams.current = 1
  console.log(searchParams)
  fetchData()
}

// 获取标签和分类选项
const getTagCategoryOptions = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    // 转换成下拉选项组件接受的格式
    categoryList.value = res.data.data.categoryList ?? []
    tagList.value = res.data.data.tagList ?? []
  } else {
    message.error('加载分类标签失败，' + res.data.message)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})
/**
 * 主页图片点击事件 => 跳转到对应的图片详情
 * @param picture
 */
const doClick = (picture: API.PictureVO) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}
</script>

<style scoped>
#homePage {
  margin-bottom: 16px;
}

.search-bar {
  max-width: 480px;
  margin: 0 auto 16px;
}

.tag-bar {
  margin-bottom: 16px;
}

.image-container {
  position: relative;
  overflow: hidden;
  width: 100%;
}

.overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 10px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  opacity: 0;
  transition: opacity 0.3s ease;
  cursor: pointer;
  z-index: 1;
  pointer-events: none;
}

/* 当鼠标悬停时显示遮罩层 */
.image-container:hover .overlay {
  opacity: 1;
}

.overlay-content {
  text-align: left;
}
</style>
