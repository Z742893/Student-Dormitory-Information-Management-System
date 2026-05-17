<template>
  <div class="room-manage-page">
    <el-card class="room-card" shadow="never">
      <template #header>
        <div class="room-card-header">
          <div class="room-card-header-text">
            <h2 class="room-title">房间管理</h2>
            <p class="room-desc">选择宿舍楼后查看列表，可添加 / 编辑房间或筛选空床位房间</p>
          </div>
        </div>
      </template>

      <div class="room-toolbar">
        <div class="room-toolbar-left">
          <span class="room-toolbar-label">宿舍楼</span>
          <el-select
            v-model="selectedBuilding"
            class="room-select-building"
            placeholder="请选择宿舍楼"
            @change="loadRooms"
          >
            <el-option
              v-for="b in buildings"
              :key="b.buildingNo"
              :label="b.buildingName"
              :value="b.buildingNo"
            />
          </el-select>
        </div>
        <div class="room-toolbar-actions">
          <el-button type="primary" :disabled="!selectedBuilding" @click="showDialog()">
            添加房间
          </el-button>
          <el-button :disabled="!selectedBuilding" @click="showAvailable">查看空房间</el-button>
        </div>
      </div>

      <div class="room-table-wrap">
        <el-table :data="tableData" border>
          <el-table-column prop="buildingNo" label="楼号" min-width="100" />
          <el-table-column prop="roomNo" label="房间号" min-width="110" />
          <el-table-column prop="totalBeds" label="床位总数" min-width="100" align="center" />
          <el-table-column prop="occupiedBeds" label="已住人数" min-width="100" align="center" />
          <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
          <el-table-column label="操作" width="180" fixed="right" align="center">
            <template #default="{ row }">
              <el-button size="small" @click="showDialog(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <p v-if="selectedBuilding && tableData.length === 0" class="room-empty-hint">
          当前楼栋暂无房间数据，可点击「添加房间」录入
        </p>
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      class="room-dialog"
      :title="isEdit ? '编辑房间' : '添加房间'"
      width="520px"
      align-center
      destroy-on-close
    >
      <el-form :model="form" class="room-form" label-width="96px" label-position="right">
        <el-form-item label="楼号">
          <el-select v-model="form.buildingNo" class="room-form-control" :disabled="isEdit">
            <el-option
              v-for="b in buildings"
              :key="b.buildingNo"
              :label="b.buildingName"
              :value="b.buildingNo"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="form.roomNo" class="room-form-control" :disabled="isEdit" placeholder="如 101" />
        </el-form-item>
        <el-form-item label="床位总数">
          <el-input-number v-model="form.totalBeds" class="room-form-control" :min="1" :max="99" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            placeholder="选填"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="room-dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { roomApi, buildingApi } from '../../api'

const buildings = ref([])
const selectedBuilding = ref('')
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  buildingNo: '',
  roomNo: '',
  totalBeds: 4,
  remark: ''
})

const loadBuildings = async () => {
  const res = await buildingApi.list()
  buildings.value = res.data
}

const loadRooms = async () => {
  if (!selectedBuilding.value) {
    tableData.value = []
    return
  }
  const res = await roomApi.listByBuilding(selectedBuilding.value)
  tableData.value = res.data
}

const showAvailable = async () => {
  if (!selectedBuilding.value) {
    ElMessage.warning('请先选择宿舍楼')
    return
  }
  const res = await roomApi.available(selectedBuilding.value)
  tableData.value = res.data
}

const showDialog = (row) => {
  if (row) {
    Object.assign(form, row)
    isEdit.value = true
  } else {
    form.buildingNo = selectedBuilding.value
    form.roomNo = ''
    form.totalBeds = 4
    form.remark = ''
    isEdit.value = false
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await roomApi.update(form)
    } else {
      await roomApi.add(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadRooms()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该房间吗？', '提示', { type: 'warning' })
    await roomApi.delete(row.buildingNo, row.roomNo)
    ElMessage.success('删除成功')
    loadRooms()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadBuildings()
})
</script>

<style scoped>
.room-manage-page {
  max-width: 1200px;
  margin: 0 auto;
}

.room-card :deep(.el-card__header) {
  padding: 18px 22px;
  border-bottom: 1px solid var(--sky-200, #d6e5ff);
}

.room-card :deep(.el-card__body) {
  padding: 22px 22px 24px;
}

.room-card-header-text {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.room-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--app-text, #2c3d55);
  letter-spacing: 0.02em;
}

.room-desc {
  margin: 0;
  font-size: 13px;
  line-height: 1.5;
  color: var(--app-text-muted, #5a6b82);
}

.room-toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px 20px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid var(--sky-200, #d6e5ff);
}

.room-toolbar-left {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px 14px;
}

.room-toolbar-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--app-text-muted, #5a6b82);
  white-space: nowrap;
}

.room-select-building {
  width: min(100%, 260px);
  min-width: 200px;
}

.room-toolbar-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

.room-table-wrap {
  margin-top: 4px;
}

.room-table-wrap :deep(.el-table) {
  margin-top: 0;
}

.room-empty-hint {
  margin: 14px 0 0;
  padding: 12px 14px;
  font-size: 13px;
  color: var(--app-text-muted, #5a6b82);
  text-align: center;
  background: var(--sky-50, #eef5ff);
  border-radius: 8px;
  border: 1px dashed var(--sky-300, #c5d9ff);
}

.room-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.room-form :deep(.el-form-item:last-child) {
  margin-bottom: 4px;
}

.room-form-control {
  width: 100%;
  max-width: 320px;
}

.room-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
