import ACCESS_ENUM from '@/access/accessEnum'

/**
 * 权限检查（判断当前登录用户是否具有某个权限）
 * @param loginUser 当前登录用户
 * @param needAccess 需要有的权限
 * @return boolean 有无权限
 */
const checkAccess = (loginUser: any, needAccess: string | string[] = ACCESS_ENUM.ADMIN) => {
  // 获取当前登录用户具有的权限（如果没有 loginUser，则表示未登录）
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true
  }

  // 如果 needAccess 是数组，检查用户是否具有数组中的任意一个权限
  if (Array.isArray(needAccess)) {
    return needAccess.includes(loginUserAccess)
  }
  // 如果用户登录才能访问
  if (needAccess === ACCESS_ENUM.USER) {
    // 如果用户没登录，那么表示无权限
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN) {
      return false
    }
  }

  // 如果需要VIP权限
  if (needAccess === ACCESS_ENUM.VIP) {
    // 如果用户为VIP
    if (loginUserAccess === ACCESS_ENUM.VIP) {
      return true
    }
  }

  // 如果需要管理员权限
  if (needAccess === ACCESS_ENUM.ADMIN) {
    // 如果不为管理员，表示无权限
    if (loginUserAccess !== ACCESS_ENUM.ADMIN) {
      return false
    }
  }
  return true
}

export default checkAccess
