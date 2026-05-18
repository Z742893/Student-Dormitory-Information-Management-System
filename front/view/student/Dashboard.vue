<template>
  <div>
    <!-- 问候卡片 -->
    <el-card class="greeting-card">
      <div class="greeting-content">
        <div class="greeting-text">
          <h2>{{ greeting }}，{{ studentName }}同学！</h2>
          <p class="welcome-text">欢迎使用学生宿舍信息管理系统</p>
        </div>
        <div class="datetime-info">
          <div class="date">{{ currentDate }}</div>
          <div class="time">{{ currentTime }}</div>
          <div class="week">{{ currentWeek }}</div>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon stat-icon--primary">
              <LineIcon name="home" :size="22" />
            </div>
            <div class="stat-info">
              <div class="stat-label">入住状态</div>
              <div class="stat-value">{{ accommodationStatus }}</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon stat-icon--success">
              <LineIcon name="wrench" :size="22" />
            </div>
            <div class="stat-info">
              <div class="stat-label">我的报修</div>
              <div class="stat-value">{{ repairCount }} 条</div>
              <div class="stat-detail">待处理: {{ pendingRepairCount }} 条</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon stat-icon--warning">
              <LineIcon name="users" :size="22" />
            </div>
            <div class="stat-info">
              <div class="stat-label">访客记录</div>
              <div class="stat-value">{{ visitorCount }} 条</div>
              <div class="stat-detail">
                待审批: {{ pendingVisitorCount }} 条
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 系统介绍 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>系统介绍</span>
        </div>
      </template>
      <div class="system-intro">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="intro-section">
              <h3>
                <LineIcon class="intro-heading-icon" name="info" :size="18" /> 系统功能
              </h3>
              <ul>
                <li>
                  <LineIcon name="check" :size="15" tone="muted" /> 查看个人信息和住宿信息
                </li>
                <li>
                  <LineIcon name="check" :size="15" tone="muted" /> 在线提交宿舍报修申请
                </li>
                <li>
                  <LineIcon name="check" :size="15" tone="muted" /> 访客登记和记录查询
                </li>
                <li>
                  <LineIcon name="check" :size="15" tone="muted" /> 实时查看处理进度
                </li>
              </ul>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="intro-section">
              <h3>
                <LineIcon class="intro-heading-icon" name="help" :size="18" /> 使用指南
              </h3>
              <ul>
                <li>
                  <LineIcon name="chevron-right" :size="14" tone="muted" />
                  <strong>我的信息：</strong>查看个人资料和宿舍信息
                </li>
                <li>
                  <LineIcon name="chevron-right" :size="14" tone="muted" />
                  <strong>我的报修：</strong>提交报修申请，跟踪处理状态
                </li>
                <li>
                  <LineIcon name="chevron-right" :size="14" tone="muted" />
                  <strong>访客登记：</strong>为访客登记，等待管理员审批
                </li>
              </ul>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <!-- 快捷操作 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>快捷操作</span>
        </div>
      </template>
      <div class="quick-actions">
        <el-button type="primary" @click="goToPage('/student/info')">
          <LineIcon name="user" :size="17" tone="on-primary" /> 我的信息
        </el-button>
        <el-button type="success" @click="goToPage('/student/repair')">
          <LineIcon name="wrench" :size="17" tone="on-primary" /> 提交报修
        </el-button>
        <el-button type="warning" @click="goToPage('/student/visitor')">
          <LineIcon name="users" :size="17" tone="on-primary" /> 访客登记
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import LineIcon from "../../components/LineIcon.vue";
import { repairApi, visitorApi, studentApi } from "../../api";
import { useUserStore } from "../../store/user";

const router = useRouter();
const userStore = useUserStore();
userStore.loadUserFromToken();

const studentName = ref("同学");
const accommodationStatus = ref("未入住");
const repairCount = ref(0);
const pendingRepairCount = ref(0);
const visitorCount = ref(0);
const pendingVisitorCount = ref(0);

const currentDate = ref("");
const currentTime = ref("");
const currentWeek = ref("");
const greeting = ref("你好");

let timer = null;

// 获取问候语
const getGreeting = () => {
  const hour = new Date().getHours();
  if (hour < 6) return "夜深了";
  if (hour < 9) return "早上好";
  if (hour < 12) return "上午好";
  if (hour < 14) return "中午好";
  if (hour < 18) return "下午好";
  if (hour < 22) return "晚上好";
  return "夜深了";
};

// 更新时间
const updateDateTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");

  const weekDays = [
    "星期日",
    "星期一",
    "星期二",
    "星期三",
    "星期四",
    "星期五",
    "星期六",
  ];

  currentDate.value = `${year}年${month}月${day}日`;
  currentTime.value = `${hours}:${minutes}:${seconds}`;
  currentWeek.value = weekDays[now.getDay()];
  greeting.value = getGreeting();
};

// 加载学生信息
const loadStudentInfo = async () => {
  if (!userStore.studentId) return;

  try {
    const res = await studentApi.search(userStore.studentId);
    if (res.data && res.data.length > 0) {
      const student = res.data[0];
      studentName.value = student.name;

      if (student.status === "已入住" && student.buildingNo && student.roomNo) {
        accommodationStatus.value = `${student.buildingNo}-${student.roomNo}`;
      } else {
        accommodationStatus.value = "未入住";
      }
    }
  } catch (error) {
    console.error("加载学生信息失败:", error);
  }
};

// 加载报修统计
const loadRepairStats = async () => {
  if (!userStore.studentId) return;

  try {
    const res = await repairApi.listByStudent(userStore.studentId);
    const repairs = res.data || [];
    repairCount.value = repairs.length;
    pendingRepairCount.value = repairs.filter(
      (r) => r.status === "待处理"
    ).length;
  } catch (error) {
    console.error("加载报修统计失败:", error);
  }
};

// 加载访客统计
const loadVisitorStats = async () => {
  if (!userStore.studentId) return;

  try {
    const res = await visitorApi.list();
    const visitors = (res.data || []).filter(
      (v) => v.visitedStudentId === userStore.studentId
    );
    visitorCount.value = visitors.length;
    pendingVisitorCount.value = visitors.filter(
      (v) => v.status === "待审批"
    ).length;
  } catch (error) {
    console.error("加载访客统计失败:", error);
  }
};

// 跳转页面
const goToPage = (path) => {
  router.push(path);
};

onMounted(() => {
  updateDateTime();
  timer = setInterval(updateDateTime, 1000);

  loadStudentInfo();
  loadRepairStats();
  loadVisitorStats();
});

onUnmounted(() => {
  if (timer) {
    clearInterval(timer);
  }
});
</script>

<style scoped>
.greeting-card {
  background: linear-gradient(120deg, #e1edff 0%, #d6e5ff 45%, #eef5ff 100%);
  color: #2c3d55;
  border: 1px solid #c5d9ff;
  box-shadow: 0 2px 12px rgba(80, 110, 160, 0.08);
}

.greeting-card :deep(.el-card__body) {
  padding: 30px;
}

.greeting-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.greeting-text h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: bold;
}

.welcome-text {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.datetime-info {
  text-align: right;
}

.date {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.time {
  font-size: 32px;
  font-weight: bold;
  font-family: "Courier New", monospace;
  margin-bottom: 5px;
}

.week {
  font-size: 18px;
  opacity: 0.9;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(80, 110, 160, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 14px;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-icon--primary {
  background: var(--el-color-primary);
}

.stat-icon--success {
  background: var(--el-color-success);
}

.stat-icon--warning {
  background: var(--el-color-warning);
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #5a6b82;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #2c3d55;
}

.stat-detail {
  font-size: 12px;
  color: #5b7fd4;
  margin-top: 5px;
}

.card-header {
  font-size: 18px;
  font-weight: bold;
}

.system-intro {
  padding: 10px 0;
}

.intro-heading-icon {
  margin-right: 6px;
  vertical-align: middle;
}

.intro-section h3 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
  color: var(--el-color-primary);
}

.intro-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.intro-section li {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  color: #3d4f66;
}

.intro-section li .line-icon {
  flex-shrink: 0;
}

.quick-actions {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.quick-actions .el-button {
  flex: 1;
  min-width: 150px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}
</style>
