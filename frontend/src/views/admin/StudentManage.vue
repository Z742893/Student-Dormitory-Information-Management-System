<template>
  <div>
    <el-card>
      <div style="margin-bottom: 20px">
        <el-button type="primary" @click="showDialog()">添加学生</el-button>
        <el-input
          v-model="keyword"
          placeholder="搜索学号/姓名"
          style="width: 300px; margin-left: 10px"
          @keyup.enter="search"
        />
        <el-button @click="search">搜索</el-button>
      </div>

      <el-table :data="tableData" border>
        <el-table-column prop="studentId" label="学号" />
        <el-table-column prop="name" label="姓名" />
        <el-table-column prop="gender" label="性别" />
        <el-table-column prop="phone" label="手机号" />
        <el-table-column prop="college" label="学院" />
        <el-table-column prop="className" label="班级" />
        <el-table-column prop="status" label="入住状态" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" @click="showDialog(row)">编辑</el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(row.studentId)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑学生' : '添加学生'"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item label="学号" v-if="isEdit">
          <el-input v-model="form.studentId" disabled />
        </el-form-item>
        <el-form-item label="学号" v-else>
          <el-input
            v-model="generatedStudentId"
            disabled
            placeholder="自动生成"
          />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="form.college" />
        </el-form-item>
        <el-form-item label="班级">
          <el-input v-model="form.className" />
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
import { ref, reactive, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { studentApi } from "../../api";

const tableData = ref([]);
const dialogVisible = ref(false);
const keyword = ref("");
const isEdit = ref(false);
const generatedStudentId = ref("");

const form = reactive({
  studentId: "",
  name: "",
  gender: "",
  phone: "",
  college: "",
  className: "",
});

const loadData = async () => {
  const res = await studentApi.list();
  tableData.value = res.data;
};

const search = async () => {
  if (keyword.value) {
    const res = await studentApi.search(keyword.value);
    tableData.value = res.data;
  } else {
    loadData();
  }
};

// 生成学号：格式为 年份 + 3位序号，例如 2024001
const generateStudentId = () => {
  const year = new Date().getFullYear();

  // 找出当前年份的最大序号
  const currentYearStudents = tableData.value.filter(
    (s) => s.studentId && s.studentId.startsWith(year.toString())
  );

  let maxSeq = 0;
  currentYearStudents.forEach((s) => {
    const seq = parseInt(s.studentId.substring(4));
    if (!isNaN(seq) && seq > maxSeq) {
      maxSeq = seq;
    }
  });

  // 新序号 = 最大序号 + 1
  const newSeq = maxSeq + 1;

  // 格式化为3位数字，不足补0
  return `${year}${newSeq.toString().padStart(3, "0")}`;
};

const showDialog = (row) => {
  if (row) {
    // 编辑模式
    Object.assign(form, row);
    isEdit.value = true;
    generatedStudentId.value = "";
  } else {
    // 添加模式
    Object.keys(form).forEach((key) => (form[key] = ""));
    isEdit.value = false;
    generatedStudentId.value = generateStudentId();
  }
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      // 编辑学生
      await studentApi.update(form);
      ElMessage.success("更新成功");
    } else {
      // 添加学生，使用生成的学号
      const newStudent = {
        ...form,
        studentId: generatedStudentId.value,
      };
      await studentApi.add(newStudent);
      ElMessage.success("添加成功");
    }
    dialogVisible.value = false;
    loadData();
  } catch (error) {
    console.error(error);
  }
};

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm("确定删除该学生吗？", "提示", {
      type: "warning",
    });
    await studentApi.delete(id);
    ElMessage.success("删除成功");
    loadData();
  } catch (error) {
    console.error(error);
  }
};

onMounted(() => {
  loadData();
});
</script>
