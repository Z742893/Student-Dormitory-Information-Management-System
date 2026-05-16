<template>
  <div>
    <el-card>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="入住" name="checkin">
          <el-form :model="checkinForm" label-width="100px">
            <el-form-item label="学生信息">
              <div style="display: flex; gap: 10px; align-items: center">
                <el-input
                  v-model="checkinForm.studentId"
                  placeholder="学号"
                  style="width: 150px"
                  readonly
                />
                <el-input
                  v-model="checkinForm.studentName"
                  placeholder="姓名"
                  style="width: 150px"
                  readonly
                />
                <el-button type="primary" @click="openStudentDrawer('checkin')"
                  >选择学生</el-button
                >
              </div>
            </el-form-item>
            <el-form-item label="宿舍楼">
              <el-select
                v-model="checkinForm.buildingNo"
                @change="loadAvailableRooms"
              >
                <el-option
                  v-for="b in buildings"
                  :key="b.buildingNo"
                  :label="b.buildingName"
                  :value="b.buildingNo"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="房间号">
              <el-select v-model="checkinForm.roomNo">
                <el-option
                  v-for="r in availableRooms"
                  :key="r.roomNo"
                  :label="`${r.roomNo} (${r.occupiedBeds}/${r.totalBeds})`"
                  :value="r.roomNo"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleCheckIn"
                >确认入住</el-button
              >
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="调宿" name="transfer">
          <el-form :model="transferForm" label-width="100px">
            <el-form-item label="学生信息">
              <div style="display: flex; gap: 10px; align-items: center">
                <el-input
                  v-model="transferForm.studentId"
                  placeholder="学号"
                  style="width: 150px"
                  readonly
                />
                <el-input
                  v-model="transferForm.studentName"
                  placeholder="姓名"
                  style="width: 150px"
                  readonly
                />
                <el-button type="primary" @click="openStudentDrawer('transfer')"
                  >选择学生</el-button
                >
              </div>
            </el-form-item>
            <el-form-item label="新宿舍楼">
              <el-select
                v-model="transferForm.newBuildingNo"
                @change="loadNewRooms"
              >
                <el-option
                  v-for="b in buildings"
                  :key="b.buildingNo"
                  :label="b.buildingName"
                  :value="b.buildingNo"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="新房间号">
              <el-select v-model="transferForm.newRoomNo">
                <el-option
                  v-for="r in newRooms"
                  :key="r.roomNo"
                  :label="`${r.roomNo} (${r.occupiedBeds}/${r.totalBeds})`"
                  :value="r.roomNo"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleTransfer"
                >确认调宿</el-button
              >
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="退宿" name="checkout">
          <el-form label-width="100px">
            <el-form-item label="学生信息">
              <div style="display: flex; gap: 10px; align-items: center">
                <el-input
                  v-model="checkoutStudentId"
                  placeholder="学号"
                  style="width: 150px"
                  readonly
                />
                <el-input
                  v-model="checkoutStudentName"
                  placeholder="姓名"
                  style="width: 150px"
                  readonly
                />
                <el-button type="primary" @click="openStudentDrawer('checkout')"
                  >选择学生</el-button
                >
              </div>
            </el-form-item>
            <el-form-item>
              <el-button type="danger" @click="handleCheckOut"
                >确认退宿</el-button
              >
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 学生选择抽屉 -->
    <el-drawer v-model="drawerVisible" title="选择学生" size="60%">
      <div style="padding: 0 20px">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索学号或姓名"
          style="margin-bottom: 20px"
          @keyup.enter="searchStudents"
        >
          <template #append>
            <el-button @click="searchStudents" class="btn-with-line-icon">
              <LineIcon name="search" :size="16" />
              搜索
            </el-button>
          </template>
        </el-input>

        <el-table
          :data="studentList"
          border
          highlight-current-row
          @row-click="selectStudent"
          style="cursor: pointer"
        >
          <el-table-column prop="studentId" label="学号" width="120" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="gender" label="性别" width="60" />
          <el-table-column prop="phone" label="手机号" width="130" />
          <el-table-column prop="college" label="学院" />
          <el-table-column prop="className" label="班级" width="120" />
          <el-table-column prop="status" label="入住状态" width="100">
            <template #default="{ row }">
              <el-tag
                :type="row.status === '已入住' ? 'success' : 'info'"
                size="small"
              >
                {{ row.status }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          style="margin-top: 20px; justify-content: center"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { accommodationApi, buildingApi, roomApi, studentApi } from "../../api";
import LineIcon from "../../components/LineIcon.vue";

const activeTab = ref("checkin");
const buildings = ref([]);
const availableRooms = ref([]);
const newRooms = ref([]);
const checkoutStudentId = ref("");
const checkoutStudentName = ref("");

// 抽屉相关
const drawerVisible = ref(false);
const currentFormType = ref(""); // 'checkin', 'transfer', 'checkout'
const searchKeyword = ref("");
const allStudents = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const checkinForm = reactive({
  studentId: "",
  studentName: "",
  buildingNo: "",
  roomNo: "",
});

const transferForm = reactive({
  studentId: "",
  studentName: "",
  newBuildingNo: "",
  newRoomNo: "",
});

// 计算分页后的学生列表
const studentList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return allStudents.value.slice(start, end);
});

const loadBuildings = async () => {
  const res = await buildingApi.list();
  buildings.value = res.data;
};

const loadAvailableRooms = async () => {
  if (checkinForm.buildingNo) {
    const res = await roomApi.available(checkinForm.buildingNo);
    availableRooms.value = res.data;
  }
};

const loadNewRooms = async () => {
  if (transferForm.newBuildingNo) {
    const res = await roomApi.available(transferForm.newBuildingNo);
    newRooms.value = res.data;
  }
};

// 打开学生选择抽屉
const openStudentDrawer = async (formType) => {
  currentFormType.value = formType;
  drawerVisible.value = true;
  searchKeyword.value = "";
  await loadAllStudents();
};

// 加载所有学生
const loadAllStudents = async () => {
  try {
    const res = await studentApi.list();
    allStudents.value = res.data || [];
    total.value = allStudents.value.length;
    currentPage.value = 1;
  } catch (error) {
    console.error(error);
    ElMessage.error("加载学生列表失败");
  }
};

// 搜索学生
const searchStudents = async () => {
  if (searchKeyword.value.trim()) {
    try {
      const res = await studentApi.search(searchKeyword.value);
      allStudents.value = res.data || [];
      total.value = allStudents.value.length;
      currentPage.value = 1;
    } catch (error) {
      console.error(error);
      ElMessage.error("搜索失败");
    }
  } else {
    await loadAllStudents();
  }
};

// 选择学生
const selectStudent = (row) => {
  if (currentFormType.value === "checkin") {
    checkinForm.studentId = row.studentId;
    checkinForm.studentName = row.name;
  } else if (currentFormType.value === "transfer") {
    transferForm.studentId = row.studentId;
    transferForm.studentName = row.name;
  } else if (currentFormType.value === "checkout") {
    checkoutStudentId.value = row.studentId;
    checkoutStudentName.value = row.name;
  }
  drawerVisible.value = false;
  ElMessage.success(`已选择学生：${row.name}（${row.studentId}）`);
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

const handleCheckIn = async () => {
  if (!checkinForm.studentId) {
    ElMessage.warning("请选择学生");
    return;
  }
  if (!checkinForm.buildingNo || !checkinForm.roomNo) {
    ElMessage.warning("请选择宿舍楼和房间");
    return;
  }
  try {
    await accommodationApi.checkIn({
      studentId: checkinForm.studentId,
      buildingNo: checkinForm.buildingNo,
      roomNo: checkinForm.roomNo,
    });
    ElMessage.success("入住成功");
    checkinForm.studentId = "";
    checkinForm.studentName = "";
    checkinForm.roomNo = "";
  } catch (error) {
    console.error(error);
  }
};

const handleTransfer = async () => {
  if (!transferForm.studentId) {
    ElMessage.warning("请选择学生");
    return;
  }
  if (!transferForm.newBuildingNo || !transferForm.newRoomNo) {
    ElMessage.warning("请选择新宿舍楼和房间");
    return;
  }
  try {
    await accommodationApi.transfer({
      studentId: transferForm.studentId,
      newBuildingNo: transferForm.newBuildingNo,
      newRoomNo: transferForm.newRoomNo,
    });
    ElMessage.success("调宿成功");
    transferForm.studentId = "";
    transferForm.studentName = "";
    transferForm.newRoomNo = "";
  } catch (error) {
    console.error(error);
  }
};

const handleCheckOut = async () => {
  if (!checkoutStudentId.value) {
    ElMessage.warning("请选择学生");
    return;
  }
  try {
    await accommodationApi.checkOut(checkoutStudentId.value);
    ElMessage.success("退宿成功");
    checkoutStudentId.value = "";
    checkoutStudentName.value = "";
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  loadBuildings();
});
</script>

<style scoped>
.btn-with-line-icon {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style>
