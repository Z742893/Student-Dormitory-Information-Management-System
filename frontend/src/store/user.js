import { defineStore } from 'pinia'
import { getCurrentUser } from '../utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    userId: null,
    username: null,
    role: null,
    studentId: null
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.userId,
    isAdmin: (state) => state.role === 'ADMIN',
    isStudent: (state) => state.role === 'STUDENT'
  },
  
  actions: {
    // 从 token 加载用户信息
    loadUserFromToken() {
      const user = getCurrentUser()
      if (user) {
        this.userId = user.userId
        this.username = user.username
        this.role = user.role
        this.studentId = user.studentId || null
      }
    },
    
    // 清除用户信息
    clearUser() {
      this.userId = null
      this.username = null
      this.role = null
      this.studentId = null
    }
  }
})
