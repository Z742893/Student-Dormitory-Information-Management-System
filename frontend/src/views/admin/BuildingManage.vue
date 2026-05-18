<template>
  <div>
    <el-card>
      <el-button type="primary" @click="showDialog()" style="margin-bottom: 20px">添加宿舍楼</el-button>
      
      <el-table :data="tableData" border>
        <el-table-column prop="buildingNo" label="楼号" />
        <el-table-column prop="buildingName" label="楼名" />
        <el-table-column prop="floors" label="层数" />
        <el-table-column prop="genderLimit" label="性别限制" />
        <el-table-column prop="adminPhone" label="管理员电话" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="showDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.buildingNo)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑宿舍楼' : '添加宿舍楼'">
      <el-form :model="form" label-width="100px">
        <el-form-item label="楼号">
          <el-input v-model="form.buildingNo" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="楼名">
          <el-input v-model="form.buildingName" />
        </el-form-item>
        <el-form-item label="层数">
          <el-input-number v-model="form.floors" :min="1" />
        </el-form-item>
        <el-form-item label="性别限制">
          <el-select v-model="form.genderLimit">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
            <el-option label="混" value="混" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理员电话">
          <el-input v-model="form.adminPhone" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { buildingApi } from '../../api'

const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  buildingNo: '',
  buildingName: '',
  floors: 1,
  genderLimit: '',
  adminPhone: ''
})

const loadData = async () => {
  const res = await buildingApi.list()
  tableData.value = res.data
}

const showDialog = (row) => {
  if (row) {
    Object.assign(form, row)
    isEdit.value = true
  } else {
    Object.keys(form).forEach(key => form[key] = key === 'floors' ? 1 : '')
    isEdit.value = false
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await buildingApi.update(form)
    } else {
      await buildingApi.add(form)
    }
    ElMessage.success('操作成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该宿舍楼吗？', '提示', { type: 'warning' })
    await buildingApi.delete(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>
