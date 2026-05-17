<template>
  <div>
    <el-card>
      <el-table :data="tableData" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="studentId" label="学生学号" />
        <el-table-column prop="buildingNo" label="楼号" />
        <el-table-column prop="roomNo" label="房间号" />
        <el-table-column prop="repairType" label="报修类型" />
        <el-table-column prop="description" label="描述" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === '待处理' ? 'warning' : 'success'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.submitTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button v-if="row.status === '待处理'" size="small" type="success" 
              @click="handleStatus(row.id, '已处理')">
              标记已处理
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { repairApi } from '../../api'
import { formatDateTime } from '../../utils/dateFormat'

const tableData = ref([])

const loadData = async () => {
  const res = await repairApi.list()
  tableData.value = res.data
}

const handleStatus = async (id, status) => {
  try {
    await repairApi.updateStatus(id, status)
    ElMessage.success('操作成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>
