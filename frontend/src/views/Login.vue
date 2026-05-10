<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>学生宿舍信息管理系统</h2>
      
      <el-tabs v-model="activeTab">
        <!-- 登录标签页 -->
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" label-width="80px">
            <el-form-item label="用户名">
              <el-input v-model="loginForm.username" placeholder="请输入用户名" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" @keyup.enter="handleLogin" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- 学生注册标签页 -->
        <el-tab-pane label="学生注册" name="register">
          <el-form :model="registerForm" label-width="80px">
            <el-form-item label="用户名">
              <el-input v-model="registerForm.username" placeholder="用于登录" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" />
            </el-form-item>
            <el-form-item label="姓名">
              <el-input v-model="registerForm.name" placeholder="真实姓名" />
            </el-form-item>
            <el-form-item label="性别">
              <el-select v-model="registerForm.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="学院">
              <el-input v-model="registerForm.college" placeholder="请输入学院" />
            </el-form-item>
            <el-form-item label="班级">
              <el-input v-model="registerForm.className" placeholder="请输入班级" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="handleRegister" style="width: 100%">注册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '../api'

const router = useRouter()
const activeTab = ref('login')

const loginForm = reactive({
  username: '',
  password: ''
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  name: '',
  gender: '',
  phone: '',
  college: '',
  className: ''
})

const handleLogin = async () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }

  try {
    const res = await userApi.login(loginForm)
    const token = res.data
    
    // 保存 token
    localStorage.setItem('token', token)
    
    // 解析 token 获取角色信息
    const payload = parseJwtToken(token)
    console.log('登录成功，用户信息:', payload)
    
    ElMessage.success('登录成功')
    
    // 根据角色跳转
    if (payload.role === 'ADMIN') {
      router.push('/admin/dashboard')
    } else if (payload.role === 'STUDENT') {
      router.push('/student/dashboard')
    } else {
      router.push('/login')
    }
  } catch (error) {
    console.error('登录失败:', error)
  }
}

// 解析 JWT Token
const parseJwtToken = (token) => {
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
    return {}
  }
}

const handleRegister = async () => {
  // 表单验证
  if (!registerForm.username || !registerForm.password || !registerForm.name) {
    ElMessage.warning('请填写必填项：用户名、密码、姓名')
    return
  }

  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }

  if (registerForm.password.length < 6) {
    ElMessage.warning('密码长度至少6位')
    return
  }

  if (registerForm.phone && !/^1[3-9]\d{9}$/.test(registerForm.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }

  try {
    await userApi.registerStudent({
      username: registerForm.username,
      password: registerForm.password,
      name: registerForm.name,
      gender: registerForm.gender,
      phone: registerForm.phone,
      college: registerForm.college,
      className: registerForm.className
    })
    
    ElMessage.success('注册成功！学号已自动生成，请使用用户名登录')
    
    // 切换到登录标签页
    activeTab.value = 'login'
    
    // 清空注册表单
    Object.keys(registerForm).forEach(key => registerForm[key] = '')
  } catch (error) {
    console.error(error)
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url('../assets/bg.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    155deg,
    rgba(238, 245, 255, 0.92) 0%,
    rgba(214, 229, 255, 0.75) 50%,
    rgba(185, 208, 255, 0.55) 100%
  );
  backdrop-filter: blur(6px);
}

.login-card {
  width: 450px;
  padding: 20px;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
  z-index: 1;
  box-shadow: 0 12px 40px rgba(70, 104, 180, 0.12) !important;
  border-radius: 12px !important;
  background: rgba(255, 255, 255, 0.96) !important;
  backdrop-filter: blur(12px);
  border: 1px solid #d6e5ff !important;
}

.login-card :deep(.el-card__body) {
  padding: 30px;
}

h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #2c3d55;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 0.02em;
}
</style>
