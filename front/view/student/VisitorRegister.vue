<template>
  <div>
    <el-card>
      <h3>访客登记</h3>
      <el-form
        :model="form"
        label-width="100px"
        style="margin-top: 20px; max-width: 600px"
      >
        <el-form-item label="被访学生">
          <el-input
            :value="`${studentInfo.name}（${studentInfo.studentId}）`"
            disabled
            placeholder="当前登录学生"
          />
        </el-form-item>
        <el-form-item label="访客姓名">
          <el-input v-model="form.visitorName" placeholder="请输入访客姓名" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input
            v-model="form.idCard"
            placeholder="请输入访客身份证号"
            maxlength="18"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">提交登记</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <h3>我的访客记录</h3>
      <el-table :data="visitorList" border style="margin-top: 20px">
        <el-table-column prop="visitorName" label="访客姓名" width="120" />
        <el-table-column prop="idCard" label="身份证号" width="180" />
        <el-table-column prop="entryTime" label="登记时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.entryTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="exitTime" label="离开时间" width="180">
          <template #default="{ row }">
            {{ row.exitTime ? formatDateTime(row.exitTime) : "-" }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="审批状态" width="100">
          <template #default="{ row }">
            <el-tag
              :type="
                row.status === '待审批'
                  ? 'warning'
                  : row.status === '已授权'
                  ? 'success'
                  : 'danger'
              "
              size="small"
            >
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="adminRemark" label="管理员备注" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { visitorApi, studentApi } from "../../api";
import { formatDateTime } from "../../utils/dateFormat";
import { useUserStore } from "../../store/user";

const userStore = useUserStore();
userStore.loadUserFromToken();

const studentInfo = ref({
  studentId: userStore.studentId || "",
  name: "",
});

const visitorList = ref([]);

const form = reactive({
  visitorName: "",
  idCard: "",
  visitedStudentId: userStore.studentId || "",
});

// 加载学生信息
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
        name: student.name,
      };
      form.visitedStudentId = student.studentId;
    }
  } catch (error) {
    console.error("加载学生信息失败:", error);
  }
};

// 加载访客记录
const loadVisitorList = async () => {
  try {
    const res = await visitorApi.list();
    // 过滤出当前学生的访客记录
    visitorList.value = (res.data || []).filter(
      (v) => v.visitedStudentId === studentInfo.value.studentId
    );
  } catch (error) {
    console.error("加载访客记录失败:", error);
  }
};

// 身份证号验证
const validateIdCard = (idCard) => {
  // 简单验证：18位数字或17位数字+X
  const reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
  return reg.test(idCard);
};

// 提交登记
const handleSubmit = async () => {
  // 表单验证
  if (!form.visitorName || form.visitorName.trim() === "") {
    ElMessage.warning("请输入访客姓名");
    return;
  }

  if (!form.idCard || form.idCard.trim() === "") {
    ElMessage.warning("请输入访客身份证号");
    return;
  }

  if (!validateIdCard(form.idCard)) {
    ElMessage.warning("请输入正确的身份证号");
    return;
  }

  try {
    await visitorApi.register({
      visitorName: form.visitorName.trim(),
      idCard: form.idCard.trim(),
      visitedStudentId: form.visitedStudentId,
    });

    ElMessage.success("登记成功，请等待管理员审批");

    // 重置表单
    resetForm();

    // 重新加载访客记录
    loadVisitorList();
  } catch (error) {
    console.error("提交登记失败:", error);
  }
};

// 重置表单
const resetForm = () => {
  form.visitorName = "";
  form.idCard = "";
};

onMounted(() => {
  loadStudentInfo();
  loadVisitorList();
});
</script>
