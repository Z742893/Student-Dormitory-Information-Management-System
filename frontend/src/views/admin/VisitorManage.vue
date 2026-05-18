<template>
  <div>
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="visitorName" label="访客姓名" />
        <el-table-column prop="idCard" label="身份证号" />
        <el-table-column prop="visitedStudentId" label="被访学生" />
        <el-table-column prop="entryTime" label="进入时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.entryTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="exitTime" label="离开时间" width="180">
          <template #default="{ row }">
            {{ row.exitTime ? formatDateTime(row.exitTime) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === '待审批' ? 'warning' : row.status === '已授权' ? 'success' : 'danger'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="adminRemark" label="备注" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button v-if="row.status === '待审批'" size="small" type="success" 
              @click="handleApprove(row.id)">授权</el-button>
            <el-button v-if="row.status === '待审批'" size="small" type="danger" 
              @click="handleReject(row.id)">拒绝</el-button>
            <el-button v-if="row.status === '已授权' && !row.exitTime" size="small" 
              @click="handleExit(row.id)">登记离开</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { visitorApi } from '../../api'
import { formatDateTime } from '../../utils/dateFormat'

const tableData = ref([])

const loadData = async () => {
  const res = await visitorApi.list()
  tableData.value = res.data
}

const handleApprove = async (id) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入备注', '授权访客', {
      inputPlaceholder: '可选'
    })
    await visitorApi.approve(id, value)
    ElMessage.success('已授权')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleReject = async (id) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入拒绝原因', '拒绝访客', {
      inputPlaceholder: '必填',
      inputValidator: (val) => !!val
    })
    await visitorApi.reject(id, value)
    ElMessage.success('已拒绝')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleExit = async (id) => {
  try {
    await visitorApi.recordExit(id)
    ElMessage.success('已登记离开')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>
