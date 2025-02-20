import { HomeOutlined , GithubOutlined} from '@ant-design/icons-vue'
import { h } from 'vue'
import accessEnum from '@/access/accessEnum.ts'

export const routes = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
    meta: {
      access: accessEnum.NOT_LOGIN,
    },
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
    meta: {
      access: accessEnum.ADMIN,
    },
  },
  {
    key: '/about',
    label: '关于',
    title: '关于',
    meta: {
      access: accessEnum.NOT_LOGIN,
    },
  },
  {
    key: 'JcGithub',
    icon: () => h(GithubOutlined),
    label: h(
      'a',
      { href: 'https://github.com/JiuCaiGaGa/JcPic', target: '_blank' },
      '韭菜gaga的GitHub主页',
    ),
    title: 'JcGaG_GitHub',
    meta: {
      access: accessEnum.NOT_LOGIN,
    },
  },
]
