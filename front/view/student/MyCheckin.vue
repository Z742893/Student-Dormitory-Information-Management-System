<template>
  <div>
    <el-card>
      <template #header>
        <span class="checkin-page-title">
          <LineIcon name="clipboard" :size="18" />
          签到任务
        </span>
      </template>
      
      <el-alert
        v-if="!studentInfo.buildingNo"
        title="您还未入住宿舍，无需签到"
        type="warning"
        :closable="false"
        style="margin-bottom: 20px"
      />
      
      <el-empty v-if="activeTasks.length === 0 && studentInfo.buildingNo" description="暂无进行中的签到任务" />
      
      <div v-else>
        <el-row :gutter="20">
          <el-col :span="12" v-for="task in activeTasks" :key="task.id">
            <el-card shadow="hover" style="margin-bottom: 20px">
              <template #header>
                <div style="display: flex; justify-content: space-between; align-items: center">
                  <span style="font-weight: bold">{{ task.taskName }}</span>
                  <el-tag type="success">进行中</el-tag>
                </div>
              </template>
              
              <div style="margin-bottom: 15px">
                <p v-if="task.description" style="color: #666; margin-bottom: 10px">
                  {{ task.description }}
                </p>
                <p class="task-time-line">
                  <LineIcon name="clock" :size="15" tone="muted" />
                  签到时间：{{ formatDateTime(task.startTime) }} 至 {{ formatDateTime(task.endTime) }}
                </p>
              </div>
              
              <div style="text-align: center">
                <el-button 
                  v-if="!checkinStatus[task.id]"
                  type="primary" 
                  size="large"
                  :disabled="!studentInfo.buildingNo"
                  @click="handleCheckin(task)"
                >
                  <LineIcon name="check" :size="18" tone="on-primary" />
                  立即签到
                </el-button>
                <el-button 
                  v-else
                  type="success" 
                  size="large"
                  disabled
                >
                  <LineIcon name="circle-check" :size="18" tone="on-primary" />
                  已签到
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
    </el-card>
    
    <!-- 签到历史 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <span class="checkin-page-title">
          <LineIcon name="clipboard" :size="18" />
          所有签到任务
        </span>
      </template>
      
      <el-table :data="allTasks" border>
        <el-table-column prop="taskName" label="任务名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="endTime" label="结束时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="任务状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '进行中' ? 'success' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="checkinStatus[row.id]" type="success">已签到</el-tag>
            <el-tag v-else type="danger">未签到</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import LineIcon from '../../components/LineIcon.vue'
import { checkinApi, studentApi } from '../../api'
import { formatDateTime } from '../../utils/dateFormat'
import { useUserStore } from '../../store/user'

const userStore = useUserStore()
userStore.loadUserFromToken()

const activeTasks = ref([])
const allTasks = ref([])
const checkinStatus = ref({})
const studentInfo = ref({
  studentId: userStore.studentId || '',
  buildingNo: '',
  roomNo: ''
})

// 加载学生信息
const loadStudentInfo = async () => {
  if (!userStore.studentId) {
    ElMessage.error('无法获取学生信息，请重新登录')
    return
  }
  
  try {
    const res = await studentApi.search(userStore.studentId)
    if (res.data && res.data.length > 0) {
      const student = res.data[0]
      studentInfo.value = {
        studentId: student.studentId,
        buildingNo: student.buildingNo || '',
        roomNo: student.roomNo || ''
      }
    }
  } catch (error) {
    console.error('加载学生信息失败:', error)
  }
}

// 加载进行中的签到任务
const loadActiveTasks = async () => {
  try {
    const res = await checkinApi.getActiveTasks()
    activeTasks.value = res.data || []
    
    // 检查每个任务的签到状态
    for (const task of activeTasks.value) {
      await checkTaskStatus(task.id)
    }
  } catch (error) {
    console.error('加载签到任务失败:', error)
  }
}

// 加载所有签到任务
const loadAllTasks = async () => {
  try {
    const res = await checkinApi.listTasks()
    allTasks.value = res.data || []
    
    // 检查每个任务的签到状态
    for (const task of allTasks.value) {
      await checkTaskStatus(task.id)
    }
  } catch (error) {
    console.error('加载签到任务失败:', error)
  }
}

// 检查签到状态
const checkTaskStatus = async (taskId) => {
  if (!studentInfo.value.studentId) return
  
  try {
    const res = await checkinApi.checkStatus(taskId, studentInfo.value.studentId)
    checkinStatus.value[taskId] = res.data
  } catch (error) {
    console.error('检查签到状态失败:', error)
  }
}

// 签到
const handleCheckin = async (task) => {
  if (!studentInfo.value.buildingNo) {
    ElMessage.warning('您还未入住宿舍，无需签到')
    return
  }
  
  try {
    await ElMessageBox.confirm(
      `确定要签到任务"${task.taskName}"吗？`,
      '签到确认',
      { type: 'info' }
    )
    
    await checkinApi.doCheckin(task.id, studentInfo.value.studentId)
    ElMessage.success('签到成功！')
    checkinStatus.value[task.id] = true
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '签到失败')
    }
  }
}

onMounted(async () => {
  await loadStudentInfo()
  loadActiveTasks()
  loadAllTasks()
})
</script>

<style scoped>
.checkin-page-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--app-text, #2c3d55);
}

.task-time-line {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--app-text-muted, #5a6b82);
  margin-bottom: 0;
}

.task-time-line .line-icon {
  flex-shrink: 0;
}

:deep(.el-button--large) {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
</style>
