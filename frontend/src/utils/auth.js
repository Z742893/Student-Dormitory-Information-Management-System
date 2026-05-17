/**
 * 用户认证工具
 */

/**
 * 解析 JWT Token
 * @param {string} token - JWT token
 * @returns {object|null} 解析后的payload
 */
export function parseJwt(token) {
  if (!token) return null

  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    )
    return JSON.parse(jsonPayload)
  } catch (error) {
    console.error('解析token失败:', error)
    return null
  }
}

/**
 * 获取当前登录用户信息
 * @returns {object|null} 用户信息 { userId, username, role, studentId }
 */
export function getCurrentUser() {
  const token = localStorage.getItem('token')
  if (!token) return null

  const payload = parseJwt(token)
  if (!payload) return null

  return {
    userId: payload.userId,
    username: payload.username,
    role: payload.role,
    studentId: payload.studentId || null
  }
}

/**
 * 检查是否已登录
 * @returns {boolean}
 */
export function isLoggedIn() {
  return !!localStorage.getItem('token')
}

/**
 * 检查是否是管理员
 * @returns {boolean}
 */
export function isAdmin() {
  const user = getCurrentUser()
  return user && user.role === 'ADMIN'
}

/**
 * 检查是否是学生
 * @returns {boolean}
 */
export function isStudent() {
  const user = getCurrentUser()
  return user && user.role === 'STUDENT'
}

/**
 * 退出登录
 */
export function logout() {
  localStorage.removeItem('token')
}
