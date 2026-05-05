<template>
  <div>
    <!-- 错误提示 -->
    <el-alert
      v-if="showError"
      title="无法加载学生信息"
      type="error"
      :closable="false"
      style="margin-bottom: 20px"
    >
      <template #default>
        <p>{{ errorMessage }}</p>
        <el-button
          type="primary"
          size="small"
          @click="handleRelogin"
          style="margin-top: 10px"
        >
          退出重新登录
        </el-button>
      </template>
    </el-alert>

    <el-card v-if="!showError">
      <h3>我的信息</h3>
      <el-descriptions :column="2" border style="margin-top: 20px">
        <el-descriptions-item label="学号">{{
          studentInfo.studentId
        }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{
          studentInfo.name
        }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{
          studentInfo.gender
        }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{
          studentInfo.phone
        }}</el-descriptions-item>
        <el-descriptions-item label="学院">{{
          studentInfo.college
        }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{
          studentInfo.className
        }}</el-descriptions-item>
        <el-descriptions-item label="入住状态">
          <el-tag :type="studentInfo.status === '已入住' ? 'success' : 'info'">
            {{ studentInfo.status }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card
      style="margin-top: 20px"
      v-if="!showError && studentInfo.status === '已入住'"
    >
      <h3>住宿信息</h3>
      <el-descriptions :column="2" border style="margin-top: 20px">
        <el-descriptions-item label="宿舍楼">{{
          studentInfo.buildingNo
        }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{
          studentInfo.roomNo
        }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { studentApi } from "../../api";
import { useUserStore } from "../../store/user";

const router = useRouter();
const userStore = useUserStore();
userStore.loadUserFromToken();

const studentInfo = ref({});
const showError = ref(false);
const errorMessage = ref("");

const loadInfo = async () => {
  console.log("当前用户信息:", {
    userId: userStore.userId,
    username: userStore.username,
    role: userStore.role,
    studentId: userStore.studentId,
  });

  if (!userStore.studentId) {
    showError.value = true;
    errorMessage.value = "您的登录信息已过期或不完整，请退出重新登录。";
    console.error("studentId 为空，token 可能不包含学号信息");
    return;
  }

  try {
    const res = await studentApi.search(userStore.studentId);
    if (res.data && res.data.length > 0) {
      studentInfo.value = res.data[0];
      showError.value = false;
    } else {
      showError.value = true;
      errorMessage.value = `未找到学号为 ${userStore.studentId} 的学生信息。`;
    }
  } catch (error) {
    console.error("加载学生信息失败:", error);
    showError.value = true;
    errorMessage.value = "加载学生信息失败，请稍后重试。";
  }
};

const handleRelogin = () => {
  localStorage.removeItem("token");
  userStore.clearUser();
  router.push("/login");
};

onMounted(() => {
  loadInfo();
});
</script>
