<template>
  <div>
    <!-- 问候卡片 -->
    <el-card class="greeting-card">
      <div class="greeting-content">
        <div class="greeting-text">
          <h2>{{ greeting }}，管理员！</h2>
          <p class="welcome-text">欢迎使用学生宿舍信息管理系统</p>
        </div>
        <div class="datetime-info">
          <div class="date">{{ currentDate }}</div>
          <div class="time">{{ currentTime }}</div>
          <div class="week">{{ currentWeek }}</div>
        </div>
      </div>
    </el-card>

    <!-- 统计指标：不对称网格（总数占左列两行，其余错落） -->
    <div class="stat-dashboard-grid">
      <el-card class="stat-card g-stat-total">
        <div class="stat-total-inner">
          <div class="stat-total-icon-wrap">
            <LineIcon name="user" :size="28" />
          </div>
          <div class="stat-total-text">
            <div class="stat-label">学生总数</div>
            <div class="stat-value stat-value--hero">{{ totalStudents }}</div>
            <div class="stat-total-hint">在册学生人数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card g-stat-in stat-card--compact">
        <div class="stat-compact-row">
          <div class="stat-icon stat-icon--success stat-icon--sm">
            <LineIcon name="home" :size="18" />
          </div>
          <div class="stat-info">
            <div class="stat-label">已入住</div>
            <div class="stat-value">{{ accommodatedStudents }}</div>
            <div class="stat-detail">入住率 {{ accommodationRate }}%</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card g-stat-repair stat-card--compact">
        <div class="stat-compact-row">
          <div class="stat-icon stat-icon--warning stat-icon--sm">
            <LineIcon name="wrench" :size="18" />
          </div>
          <div class="stat-info">
            <div class="stat-label">报修记录</div>
            <div class="stat-value">{{ totalRepairs }}</div>
            <div class="stat-detail">待处理 {{ pendingRepairs }} 条</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card g-stat-visitor stat-card--compact">
        <div class="stat-compact-row stat-compact-row--wide">
          <div class="stat-icon stat-icon--danger stat-icon--sm">
            <LineIcon name="users" :size="18" />
          </div>
          <div class="stat-info stat-info--spread">
            <div>
              <div class="stat-label">访客记录</div>
              <div class="stat-value">{{ totalVisitors }}</div>
            </div>
            <div class="stat-side-pill">
              <span class="pill-label">待审批</span>
              <span class="pill-num">{{ pendingVisitors }}</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表：布局与类型均调整 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :lg="10">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>学生性别比例</span>
              <span class="card-header-sub">横向对比人数</span>
            </div>
          </template>
          <div ref="genderChartRef" class="chart-box chart-box--md"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="14">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>入住情况统计</span>
              <span class="card-header-sub">堆叠占比条</span>
            </div>
          </template>
          <div ref="accommodationChartRef" class="chart-box chart-box--md"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>每月学生数量变化</span>
              <span class="card-header-sub">柱状分布（近 6 段）</span>
            </div>
          </template>
          <div ref="monthlyChartRef" class="chart-box chart-box--lg"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row chart-row--last">
      <el-col :xs="24" :lg="11">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>报修类型统计</span>
              <span class="card-header-sub">横向条形</span>
            </div>
          </template>
          <div ref="repairTypeChartRef" class="chart-box chart-box--md"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="13">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>访客审批状态</span>
              <span class="card-header-sub">南丁格尔玫瑰图</span>
            </div>
          </template>
          <div ref="visitorStatusChartRef" class="chart-box chart-box--md"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import LineIcon from '../../components/LineIcon.vue'
import * as echarts from 'echarts'
import { studentApi, repairApi, visitorApi } from '../../api'

// 统计数据
const totalStudents = ref(0)
const accommodatedStudents = ref(0)
const accommodationRate = ref(0)
const totalRepairs = ref(0)
const pendingRepairs = ref(0)
const totalVisitors = ref(0)
const pendingVisitors = ref(0)

// 时间相关
const currentDate = ref('')
const currentTime = ref('')
const currentWeek = ref('')
const greeting = ref('你好')

// 图表引用
const genderChartRef = ref(null)
const accommodationChartRef = ref(null)
const monthlyChartRef = ref(null)
const repairTypeChartRef = ref(null)
const visitorStatusChartRef = ref(null)

let timer = null
let charts = []

// 获取问候语
const getGreeting = () => {
  const hour = new Date().getHours()
  if (hour < 6) return '夜深了'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 18) return '下午好'
  if (hour < 22) return '晚上好'
  return '夜深了'
}

// 更新时间
const updateDateTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')
  
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  
  currentDate.value = `${year}年${month}月${day}日`
  currentTime.value = `${hours}:${minutes}:${seconds}`
  currentWeek.value = weekDays[now.getDay()]
  greeting.value = getGreeting()
}

// 加载统计数据
const loadStatistics = async () => {
  try {
    // 加载学生数据
    const studentsRes = await studentApi.list()
    const students = studentsRes.data || []
    totalStudents.value = students.length
    accommodatedStudents.value = students.filter(s => s.status === '已入住').length
    accommodationRate.value = totalStudents.value > 0 
      ? Math.round((accommodatedStudents.value / totalStudents.value) * 100) 
      : 0

    // 加载报修数据
    const repairsRes = await repairApi.list()
    const repairs = repairsRes.data || []
    totalRepairs.value = repairs.length
    pendingRepairs.value = repairs.filter(r => r.status === '待处理').length

    // 加载访客数据
    const visitorsRes = await visitorApi.list()
    const visitors = visitorsRes.data || []
    totalVisitors.value = visitors.length
    pendingVisitors.value = visitors.filter(v => v.status === '待审批').length

    // 绘制图表
    await nextTick()
    drawGenderChart(students)
    drawAccommodationChart(students)
    drawMonthlyChart(students)
    drawRepairTypeChart(repairs)
    drawVisitorStatusChart(visitors)
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const chartTextStyle = { color: '#5a6b82', fontSize: 12 }

// 性别：横向条形图（原环形饼图）
const drawGenderChart = (students) => {
  if (!genderChartRef.value) return

  const chart = echarts.init(genderChartRef.value)
  charts.push(chart)

  const maleCount = students.filter(s => s.gender === '男').length
  const femaleCount = students.filter(s => s.gender === '女').length

  const option = {
    textStyle: chartTextStyle,
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        const p = params[0]
        return `${p.name}<br/>${p.marker}${p.value} 人`
      }
    },
    grid: { left: '18%', right: '14%', top: '10%', bottom: '10%' },
    xAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#e1edff', type: 'dashed' } },
      axisLabel: { color: '#5a6b82' }
    },
    yAxis: {
      type: 'category',
      data: ['女生', '男生'],
      inverse: true,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#2c3d55', fontWeight: 600 }
    },
    series: [
      {
        type: 'bar',
        data: [
          {
            value: femaleCount,
            itemStyle: {
              color: '#a9c6ff',
              borderRadius: [0, 8, 8, 0]
            }
          },
          {
            value: maleCount,
            itemStyle: {
              color: '#5b7fd4',
              borderRadius: [0, 8, 8, 0]
            }
          }
        ],
        barWidth: 28,
        label: {
          show: true,
          position: 'right',
          color: '#2c3d55',
          formatter: '{c} 人'
        }
      }
    ]
  }

  chart.setOption(option)
}

// 入住：单条堆叠横向条（原饼图）
const drawAccommodationChart = (students) => {
  if (!accommodationChartRef.value) return

  const chart = echarts.init(accommodationChartRef.value)
  charts.push(chart)

  const accommodated = students.filter(s => s.status === '已入住').length
  const notAccommodated = students.filter(s => s.status === '未入住').length

  const option = {
    textStyle: chartTextStyle,
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => {
        if (!params || !params.length) return ''
        const sum = params.reduce((s, p) => s + p.value, 0)
        const lines = params.map(
          p => `${p.marker}${p.seriesName}: ${p.value} 人`
        )
        const inVal = params.find(p => p.seriesName === '已入住')?.value ?? 0
        const pct = sum > 0 ? Math.round((100 * inVal) / sum) : 0
        lines.push(`已入住占比: ${pct}%`)
        return lines.join('<br/>')
      }
    },
    legend: {
      top: 8,
      right: 12,
      textStyle: { color: '#5a6b82' }
    },
    grid: { left: '3%', right: '4%', top: 48, bottom: 28 },
    xAxis: {
      type: 'value',
      max: accommodated + notAccommodated || 1,
      splitLine: { show: false },
      axisLabel: { color: '#5a6b82' }
    },
    yAxis: {
      type: 'category',
      data: ['入住概览'],
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#5a6b82', fontWeight: 500 }
    },
    series: [
      {
        name: '已入住',
        type: 'bar',
        stack: 'stay',
        data: [accommodated],
        itemStyle: {
          color: '#4668bd',
          borderRadius: [6, 0, 0, 6]
        }
      },
      {
        name: '未入住',
        type: 'bar',
        stack: 'stay',
        data: [notAccommodated],
        itemStyle: {
          color: '#d6e5ff',
          borderRadius: [0, 6, 6, 0]
        }
      }
    ]
  }

  chart.setOption(option)
}

// 每月人数：圆角竖向柱状（原面积折线）
const drawMonthlyChart = (students) => {
  if (!monthlyChartRef.value) return

  const chart = echarts.init(monthlyChartRef.value)
  charts.push(chart)

  const monthlyData = {}
  students.forEach(s => {
    if (s.studentId && s.studentId.length >= 7) {
      const year = s.studentId.substring(0, 4)
      const month = s.studentId.substring(4, 6)
      const key = `${year}-${month}`
      monthlyData[key] = (monthlyData[key] || 0) + 1
    }
  })

  const sortedKeys = Object.keys(monthlyData).sort()
  const months = sortedKeys.slice(-6)
  const values = months.map(key => monthlyData[key])

  const option = {
    textStyle: chartTextStyle,
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '3%', top: 28, bottom: months.some(m => String(m).length > 8) ? 56 : 36 },
    xAxis: {
      type: 'category',
      data: months,
      axisLabel: {
        rotate: months.some(m => String(m).length > 8) ? 35 : 0,
        color: '#5a6b82'
      },
      axisLine: { lineStyle: { color: '#d6e5ff' } }
    },
    yAxis: {
      type: 'value',
      name: '人数',
      nameTextStyle: { color: '#5a6b82' },
      splitLine: { lineStyle: { color: '#e1edff', type: 'dashed' } },
      axisLabel: { color: '#5a6b82' }
    },
    series: [
      {
        type: 'bar',
        data: values.map(v => ({
          value: v,
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#b8d0ff' },
              { offset: 1, color: '#5b7fd4' }
            ]),
            borderRadius: [8, 8, 0, 0]
          }
        })),
        barWidth: '38%',
        label: {
          show: true,
          position: 'top',
          color: '#2c3d55',
          fontSize: 11
        }
      }
    ]
  }

  chart.setOption(option)
}

// 报修类型：横向条形（原竖向柱）
const drawRepairTypeChart = (repairs) => {
  if (!repairTypeChartRef.value) return

  const chart = echarts.init(repairTypeChartRef.value)
  charts.push(chart)

  const typeCount = {}
  repairs.forEach(r => {
    const type = r.repairType || '其他'
    typeCount[type] = (typeCount[type] || 0) + 1
  })

  const types = Object.keys(typeCount)
  const values = types.map(t => typeCount[t])

  const option = {
    textStyle: chartTextStyle,
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' }
    },
    grid: { left: '4%', right: '12%', top: '6%', bottom: '6%', containLabel: true },
    xAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: '#e1edff', type: 'dashed' } },
      axisLabel: { color: '#5a6b82' }
    },
    yAxis: {
      type: 'category',
      data: types,
      inverse: true,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#2c3d55', width: 72, overflow: 'truncate' }
    },
    series: [
      {
        type: 'bar',
        data: values,
        barWidth: 18,
        itemStyle: {
          borderRadius: [0, 8, 8, 0],
          color: new echarts.graphic.LinearGradient(1, 0, 0, 0, [
            { offset: 0, color: '#e1edff' },
            { offset: 1, color: '#5b7fd4' }
          ])
        },
        label: {
          show: true,
          position: 'right',
          color: '#2c3d55',
          formatter: '{c}'
        }
      }
    ]
  }

  chart.setOption(option)
}

// 访客审批：南丁格尔玫瑰（原普通饼图）
const drawVisitorStatusChart = (visitors) => {
  if (!visitorStatusChartRef.value) return

  const chart = echarts.init(visitorStatusChartRef.value)
  charts.push(chart)

  const statusCount = {
    待审批: 0,
    已授权: 0,
    已拒绝: 0
  }

  visitors.forEach(v => {
    if (Object.prototype.hasOwnProperty.call(statusCount, v.status)) {
      statusCount[v.status]++
    }
  })

  const option = {
    textStyle: chartTextStyle,
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} 条 ({d}%)'
    },
    legend: {
      bottom: 6,
      left: 'center',
      textStyle: { color: '#5a6b82' }
    },
    series: [
      {
        type: 'pie',
        roseType: 'area',
        radius: [22, '62%'],
        center: ['50%', '46%'],
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          formatter: '{b}\n{c}条',
          color: '#2c3d55',
          fontSize: 11
        },
        data: [
          { value: statusCount['待审批'], name: '待审批', itemStyle: { color: '#b8d0ff' } },
          { value: statusCount['已授权'], name: '已授权', itemStyle: { color: '#a9c6ff' } },
          { value: statusCount['已拒绝'], name: '已拒绝', itemStyle: { color: '#5b7fd4' } }
        ]
      }
    ]
  }

  chart.setOption(option)
}

// 窗口大小改变时重绘图表
const handleResize = () => {
  charts.forEach(chart => chart.resize())
}

onMounted(() => {
  updateDateTime()
  timer = setInterval(updateDateTime, 1000)
  loadStatistics()
  
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
  
  charts.forEach(chart => chart.dispose())
  charts = []
  
  window.removeEventListener('resize', handleResize)
})
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
  font-family: 'Courier New', monospace;
  margin-bottom: 5px;
}

.week {
  font-size: 18px;
  opacity: 0.9;
}

.stat-dashboard-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: auto auto;
  gap: 16px;
  margin-top: 20px;
}

.g-stat-total {
  grid-column: 1;
  grid-row: 1 / span 2;
}

.g-stat-in {
  grid-column: 2;
  grid-row: 1;
}

.g-stat-repair {
  grid-column: 3;
  grid-row: 1;
}

.g-stat-visitor {
  grid-column: 2 / span 2;
  grid-row: 2;
}

.stat-card {
  cursor: default;
  transition: box-shadow 0.25s ease;
}

.stat-card:hover {
  box-shadow: 0 6px 18px rgba(80, 110, 160, 0.1);
}

.stat-card--compact :deep(.el-card__body) {
  padding: 16px 18px;
}

.stat-total-inner {
  display: flex;
  align-items: center;
  gap: 18px;
  min-height: 120px;
  padding: 6px 4px;
}

.stat-total-icon-wrap {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  background: linear-gradient(145deg, #d6e5ff, #a9c6ff);
  color: #2c3d55;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-total-text {
  flex: 1;
  min-width: 0;
}

.stat-value--hero {
  font-size: 42px;
  line-height: 1.1;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.stat-total-hint {
  margin-top: 8px;
  font-size: 13px;
  color: #5a6b82;
}

.stat-compact-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-compact-row--wide {
  justify-content: flex-start;
}

.stat-info--spread {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.stat-side-pill {
  text-align: right;
  padding: 8px 14px;
  border-radius: 10px;
  background: #eef5ff;
  border: 1px solid #d6e5ff;
}

.pill-label {
  display: block;
  font-size: 11px;
  color: #5a6b82;
  margin-bottom: 2px;
}

.pill-num {
  font-size: 20px;
  font-weight: 700;
  color: #5b7fd4;
}

.chart-row {
  margin-top: 16px;
}

.chart-row--last {
  margin-bottom: 8px;
}

.chart-card :deep(.el-card__header) {
  padding: 14px 18px;
  border-bottom: 1px solid #e1edff;
}

.chart-box {
  width: 100%;
}

.chart-box--md {
  height: 300px;
}

.chart-box--lg {
  height: 320px;
}

.card-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
  font-size: 16px;
  font-weight: 700;
  color: #2c3d55;
}

.card-header-sub {
  font-size: 12px;
  font-weight: 400;
  color: #5a6b82;
}

@media (max-width: 992px) {
  .stat-dashboard-grid {
    grid-template-columns: 1fr 1fr;
  }

  .g-stat-total {
    grid-column: 1 / -1;
    grid-row: auto;
  }

  .g-stat-in {
    grid-column: 1;
    grid-row: auto;
  }

  .g-stat-repair {
    grid-column: 2;
    grid-row: auto;
  }

  .g-stat-visitor {
    grid-column: 1 / -1;
    grid-row: auto;
  }

  .stat-total-inner {
    min-height: auto;
  }

  .stat-value--hero {
    font-size: 36px;
  }
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 10px;
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

.stat-icon--danger {
  background: var(--el-color-danger);
}

.stat-icon.stat-icon--sm {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  flex-shrink: 0;
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
</style>
