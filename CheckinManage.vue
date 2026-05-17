<template>
  <div>
    <el-card>
      <template #header>
        <div class="checkin-card-head">
          <span class="checkin-card-title">
            <LineIcon name="clipboard" :size="18" />
            签到任务管理
          </span>
          <el-button type="primary" @click="showAddDialog">发布签到任务</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="60" />
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
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '进行中' ? 'success' : 'info'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="viewStatistics(row)">
              查看统计
            </el-button>
            <el-button size="small" @click="showEditDialog(row)">编辑</el-button>
            <el-button v-if="row.status === '进行中'" size="small" type="warning" @click="handleEnd(row.id)">
              结束
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑签到任务' : '发布签到任务'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="任务名称" required>
          <el-input v-model="form.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入任务描述" />
        </el-form-item>
        <el-form-item label="签到时间" required>
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="状态" v-if="isEdit">
          <el-select v-model="form.status">
            <el-option label="进行中" value="进行中" />
            <el-option label="已结束" value="已结束" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 统计对话框 -->
    <el-dialog v-model="statsDialogVisible" title="签到统计" width="800px">
      <div v-if="statistics" style="margin-bottom: 20px">
        <el-descriptions :column="4" border>
          <el-descriptions-item label="任务名称">{{ statistics.taskName }}</el-descriptions-item>
          <el-descriptions-item label="应签到人数">{{ statistics.totalStudents }}</el-descriptions-item>
          <el-descriptions-item label="已签到人数">
            <span style="color: #67c23a">{{ statistics.checkedInCount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="未签到人数">
            <span style="color: #f56c6c">{{ statistics.notCheckedInCount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="签到率">
            <el-progress :percentage="statistics.checkinRate" :format="(p) => p.toFixed(2) + '%'" />
          </el-descriptions-item>
        </el-descriptions>
      </div>
      
      <el-table :data="studentStatusList" border max-height="400">
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="buildingNo" label="楼号" width="80" />
        <el-table-column prop="roomNo" label="房间号" width="80" />
        <el-table-column prop="hasCheckedIn" label="签到状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.hasCheckedIn ? 'success' : 'danger'">
              {{ row.hasCheckedIn ? '已签到' : '未签到' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="checkinTime" label="签到时间">
          <template #default="{ row }">
            {{ row.checkinTime ? formatDateTime(row.checkinTime) : '-' }}
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { checkinApi } from '../../api'
import LineIcon from '../../components/LineIcon.vue'
import { formatDateTime } from '../../utils/dateFormat'

const tableData = ref([])
const dialogVisible = ref(false)
const statsDialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})
const timeRange = ref([])
const statistics = ref(null)
const studentStatusList = ref([])

const loadData = async () => {
  const res = await checkinApi.listTasks()
  tableData.value = res.data
}

const showAddDialog = () => {
  isEdit.value = false
  form.value = { status: '进行中' }
  timeRange.value = []
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  form.value = { ...row }
  timeRange.value = [row.startTime, row.endTime]
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.taskName) {
    ElMessage.warning('请输入任务名称')
    return
  }
  if (!timeRange.value || timeRange.value.length !== 2) {
    ElMessage.warning('请选择签到时间范围')
    return
  }
  
  form.value.startTime = timeRange.value[0]
  form.value.endTime = timeRange.value[1]
  
  try {
    if (isEdit.value) {
      await checkinApi.updateTask(form.value)
      ElMessage.success('更新成功')
    } else {
      await checkinApi.addTask(form.value)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

const handleEnd = async (id) => {
  try {
    await ElMessageBox.confirm('确定要结束该签到任务吗？', '提示', { type: 'warning' })
    await checkinApi.endTask(id)
    ElMessage.success('已结束')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('操作失败')
    }
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该签到任务吗？相关签到记录也会被删除。', '提示', { type: 'warning' })
    await checkinApi.deleteTask(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const viewStatistics = async (row) => {
  try {
    const [statsRes, statusRes] = await Promise.all([
      checkinApi.getStatistics(row.id),
      checkinApi.getStudentStatus(row.id)
    ])
    statistics.value = statsRes.data
    studentStatusList.value = statusRes.data
    statsDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.checkin-card-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.checkin-card-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  color: var(--app-text, #2c3d55);
}
</style>
