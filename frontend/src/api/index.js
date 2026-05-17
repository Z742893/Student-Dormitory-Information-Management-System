import request from './request'

export const userApi = {
  login: (data) => request.post('/user/login', data),
  register: (data) => request.post('/user/register', data),
  registerStudent: (data) => request.post('/user/register/student', data)
}

export const studentApi = {
  list: () => request.get('/student/list'),
  add: (data) => request.post('/student/add', data),
  update: (data) => request.put('/student/update', data),
  delete: (id) => request.delete(`/student/${id}`),
  search: (keyword) => request.get('/student/search', { params: { keyword } })
}

export const buildingApi = {
  list: () => request.get('/building/list'),
  add: (data) => request.post('/building/add', data),
  update: (data) => request.put('/building/update', data),
  delete: (id) => request.delete(`/building/${id}`)
}

export const roomApi = {
  listByBuilding: (buildingNo) => request.get(`/room/list/${buildingNo}`),
  available: (buildingNo) => request.get(`/room/available/${buildingNo}`),
  add: (data) => request.post('/room/add', data),
  update: (data) => request.put('/room/update', data),
  delete: (buildingNo, roomNo) => request.delete(`/room/${buildingNo}/${roomNo}`)
}

export const accommodationApi = {
  checkIn: (data) => request.post('/accommodation/checkin', data),
  transfer: (data) => request.post('/accommodation/transfer', data),
  checkOut: (studentId) => request.post(`/accommodation/checkout/${studentId}`)
}

export const repairApi = {
  list: () => request.get('/repair/list'),
  listByStudent: (studentId) => request.get(`/repair/student/${studentId}`),
  submit: (data) => request.post('/repair/submit', data),
  updateStatus: (id, status) => request.put(`/repair/status/${id}`, null, { params: { status } })
}

export const visitorApi = {
  list: () => request.get('/visitor/list'),
  register: (data) => request.post('/visitor/register', data),
  approve: (id, remark) => request.put(`/visitor/approve/${id}`, null, { params: { remark } }),
  reject: (id, remark) => request.put(`/visitor/reject/${id}`, null, { params: { remark } }),
  recordExit: (id) => request.put(`/visitor/exit/${id}`)
}

export const checkinApi = {
  // 签到任务管理（管理员）
  listTasks: () => request.get('/checkin/task/list'),
  getTask: (id) => request.get(`/checkin/task/${id}`),
  addTask: (data) => request.post('/checkin/task/add', data),
  updateTask: (data) => request.put('/checkin/task/update', data),
  deleteTask: (id) => request.delete(`/checkin/task/${id}`),
  endTask: (id) => request.put(`/checkin/task/end/${id}`),
  
  // 签到统计（管理员）
  getStatistics: (taskId) => request.get(`/checkin/statistics/${taskId}`),
  getStudentStatus: (taskId) => request.get(`/checkin/status/${taskId}`),
  
  // 学生签到
  getActiveTasks: () => request.get('/checkin/active'),
  doCheckin: (taskId, studentId) => request.post(`/checkin/do/${taskId}/${studentId}`),
  checkStatus: (taskId, studentId) => request.get(`/checkin/check/${taskId}/${studentId}`)
}
