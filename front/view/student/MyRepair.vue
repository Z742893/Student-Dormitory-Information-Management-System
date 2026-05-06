<template>
  <div>
    <el-card>
      <el-button
        type="primary"
        @click="openDialog"
        style="margin-bottom: 20px"
        :disabled="!studentInfo.buildingNo"
      >
        提交报修
      </el-button>
      <el-alert
        v-if="!studentInfo.buildingNo"
        title="您还未入住宿舍，无法提交报修"
        type="warning"
        :closable="false"
        style="margin-bottom: 20px"
      />

      <el-table :data="tableData" border>
        <el-table-column prop="repairType" label="报修类型" />
        <el-table-column prop="buildingNo" label="楼号" />
        <el-table-column prop="roomNo" label="房间号" />
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
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="提交报修" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="宿舍信息">
          <el-input
            :value="`${studentInfo.buildingNo} - ${studentInfo.roomNo}`"
            disabled
            placeholder="您的宿舍信息"
          />
        </el-form-item>
        <el-form-item label="报修类型">
          <el-select
            v-model="form.repairType"
            placeholder="请选择报修类型"
            style="width: 100%"
          >
            <el-option label="水管" value="水管" />
            <el-option label="电灯" value="电灯" />
            <el-option label="门窗" value="门窗" />
            <el-option label="空调" value="空调" />
            <el-option label="网络" value="网络" />
            <el-option label="家具" value="家具" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请详细描述问题"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { repairApi, studentApi } from "../../api";
import { formatDateTime } from "../../utils/dateFormat";
import { useUserStore } from "../../store/user";

const userStore = useUserStore();
userStore.loadUserFromToken();

const tableData = ref([]);
const dialogVisible = ref(false);
const studentInfo = ref({
  studentId: userStore.studentId || "",
  buildingNo: "",
  roomNo: "",
});

const form = reactive({
  studentId: userStore.studentId || "",
  repairType: "",
  buildingNo: "",
  roomNo: "",
  description: "",
});

// 加载学生信息（包含住宿信息）
const loadStudentInfo = async () => {
  if (!userStore.studentId) {
    ElMessage.error("无法获取学生信息，请重新登录");
    return;
  }
  
  try {
    const res = await studentApi.search(userStore.studentId);
    if (res.data && res.data.length > 0) {
      const student = res.data[0];
      studentInfo.value = {
        studentId: student.studentId,
        buildingNo: student.buildingNo || "",
        roomNo: student.roomNo || "",
      };
    }
  } catch (error) {
    console.error("加载学生信息失败:", error);
  }
};

// 加载报修记录
const loadData = async () => {
  if (!userStore.studentId) {
    return;
  }
  
  try {
    const res = await repairApi.listByStudent(userStore.studentId);
    tableData.value = res.data || [];
  } catch (error) {
    console.error("加载报修记录失败:", error);
  }
};

// 打开报修对话框
const openDialog = () => {
  if (!studentInfo.value.buildingNo) {
    ElMessage.warning("您还未入住宿舍，无法提交报修");
    return;
  }

  // 重置表单并填充宿舍信息
  form.repairType = "";
  form.description = "";
  form.buildingNo = studentInfo.value.buildingNo;
  form.roomNo = studentInfo.value.roomNo;
  form.studentId = studentInfo.value.studentId;

  dialogVisible.value = true;
};

// 提交报修
const handleSubmit = async () => {
  if (!form.repairType) {
    ElMessage.warning("请选择报修类型");
    return;
  }

  if (!form.description || form.description.trim() === "") {
    ElMessage.warning("请填写问题描述");
    return;
  }

  try {
    await repairApi.submit({
      studentId: form.studentId,
      repairType: form.repairType,
      buildingNo: form.buildingNo,
      roomNo: form.roomNo,
      description: form.description,
    });

    ElMessage.success("提交成功，请等待管理员处理");
    dialogVisible.value = false;

    // 重新加载报修记录
    loadData();
  } catch (error) {
    console.error("提交报修失败:", error);
  }
};

onMounted(() => {
  loadStudentInfo();
  loadData();
});
</script>
