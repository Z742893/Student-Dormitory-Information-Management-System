-- ----------------------------
-- 创建数据库
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `dormitory_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE `dormitory_db`;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `accommodation`
-- ----------------------------
DROP TABLE IF EXISTS `accommodation`;
CREATE TABLE `accommodation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` varchar(20) NOT NULL,
  `building_no` varchar(20) NOT NULL,
  `room_no` varchar(20) NOT NULL,
  `check_in_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `check_out_date` timestamp NULL DEFAULT NULL,
  `status` varchar(20) DEFAULT '在住' COMMENT '状态：在住/已退宿',
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  KEY `building_no` (`building_no`,`room_no`),
  CONSTRAINT `accommodation_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE,
  CONSTRAINT `accommodation_ibfk_2` FOREIGN KEY (`building_no`, `room_no`) REFERENCES `room` (`building_no`, `room_no`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of accommodation
-- ----------------------------
INSERT INTO `accommodation` VALUES ('1', '2024001', 'A1', '101', '2025-11-05 10:39:19', '2025-11-05 10:47:13', '已退宿');
INSERT INTO `accommodation` VALUES ('2', '2024001', 'A1', '101', '2025-11-05 10:47:43', '2025-11-05 10:58:37', '已退宿');
INSERT INTO `accommodation` VALUES ('3', '2024001', 'A1', '102', '2025-11-05 10:58:37', '2025-11-05 13:50:49', '已退宿');
INSERT INTO `accommodation` VALUES ('4', '2025002', 'B1', '101', '2025-11-05 13:50:40', null, '在住');
INSERT INTO `accommodation` VALUES ('5', '2024001', 'A1', 'A1-101', '2025-11-05 13:50:49', null, '在住');
INSERT INTO `accommodation` VALUES ('6', '2025003', 'B1', '101', '2025-11-05 13:53:38', '2025-11-05 13:54:07', '已退宿');

-- ----------------------------
-- Table structure for `building`
-- ----------------------------
DROP TABLE IF EXISTS `building`;
CREATE TABLE `building` (
  `building_no` varchar(20) NOT NULL,
  `building_name` varchar(100) NOT NULL,
  `floors` int NOT NULL,
  `gender_limit` varchar(10) NOT NULL COMMENT '性别限制：男/女/混',
  `admin_phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`building_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of building
-- ----------------------------
INSERT INTO `building` VALUES ('A1', 'A栋1号楼', '6', '男', '13900139001');
INSERT INTO `building` VALUES ('B1', 'B栋1号楼', '6', '女', '13900139002');
INSERT INTO `building` VALUES ('C1', 'C栋101', '6', '男', '13051641241');
INSERT INTO `building` VALUES ('C栋', 'C栋1号楼', '5', '混', '13956141224');

-- ----------------------------
-- Table structure for `checkin_record`
-- ----------------------------
DROP TABLE IF EXISTS `checkin_record`;
CREATE TABLE `checkin_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` bigint NOT NULL COMMENT '签到任务ID',
  `student_id` varchar(20) NOT NULL COMMENT '学生学号',
  `checkin_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_task_student` (`task_id`,`student_id`),
  KEY `task_id` (`task_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `checkin_record_ibfk_1` FOREIGN KEY (`task_id`) REFERENCES `checkin_task` (`id`) ON DELETE CASCADE,
  CONSTRAINT `checkin_record_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of checkin_record
-- ----------------------------
INSERT INTO `checkin_record` VALUES ('1', '2', '2024001', '2025-11-28 18:34:20');

-- ----------------------------
-- Table structure for `checkin_task`
-- ----------------------------
DROP TABLE IF EXISTS `checkin_task`;
CREATE TABLE `checkin_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_name` varchar(100) NOT NULL COMMENT '签到任务名称',
  `description` varchar(500) DEFAULT NULL COMMENT '任务描述',
  `start_time` timestamp NOT NULL COMMENT '签到开始时间',
  `end_time` timestamp NOT NULL COMMENT '签到结束时间',
  `status` varchar(20) DEFAULT '进行中' COMMENT '状态：进行中/已结束',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of checkin_task
-- ----------------------------
INSERT INTO `checkin_task` VALUES ('2', '宿舍签到', '宿舍签到', '2025-11-28 18:30:00', '2025-11-28 20:30:00', '进行中', '2025-11-28 18:34:03');

-- ----------------------------
-- Table structure for `repair`
-- ----------------------------
DROP TABLE IF EXISTS `repair`;
CREATE TABLE `repair` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` varchar(20) NOT NULL,
  `building_no` varchar(20) DEFAULT NULL,
  `room_no` varchar(20) DEFAULT NULL,
  `repair_type` varchar(50) NOT NULL COMMENT '报修类型：水管/电灯/门窗/其他',
  `description` text,
  `status` varchar(20) DEFAULT '待处理' COMMENT '状态：待处理/已处理',
  `submit_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `handle_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `repair_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of repair
-- ----------------------------
INSERT INTO `repair` VALUES ('1', '2024001', 'A1', '101', '水管', '水管漏水', '已处理', '2025-11-05 10:54:25', '2025-11-05 10:58:07');
INSERT INTO `repair` VALUES ('2', '2024001', 'A1', 'A1-101', '门窗', '门窗坏了', '已处理', '2025-11-05 13:51:27', '2025-11-05 13:51:53');

-- ----------------------------
-- Table structure for `room`
-- ----------------------------
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
  `building_no` varchar(20) NOT NULL,
  `room_no` varchar(20) NOT NULL,
  `total_beds` int NOT NULL,
  `occupied_beds` int DEFAULT '0',
  `remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`building_no`,`room_no`),
  CONSTRAINT `room_ibfk_1` FOREIGN KEY (`building_no`) REFERENCES `building` (`building_no`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of room
-- ----------------------------
INSERT INTO `room` VALUES ('A1', '101', '4', '0', null);
INSERT INTO `room` VALUES ('A1', '102', '4', '0', null);
INSERT INTO `room` VALUES ('A1', 'A1-101', '4', '1', '');
INSERT INTO `room` VALUES ('A1', 'A105', '4', '0', '男寝');
INSERT INTO `room` VALUES ('B1', '101', '4', '1', null);
INSERT INTO `room` VALUES ('C栋', '101', '4', '0', '混寝');

-- ----------------------------
-- Table structure for `student`
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
  `student_id` varchar(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `gender` varchar(10) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `college` varchar(100) DEFAULT NULL,
  `class_name` varchar(50) DEFAULT NULL,
  `status` varchar(20) DEFAULT '未入住' COMMENT '入住状态：未入住/已入住',
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`student_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `student_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES ('2024001', '张三', '男', '13800138001', '计算机学院', '软件1班', '已入住', '2');
INSERT INTO `student` VALUES ('2025002', '王艳', '女', '13951751213', '软件工程', '1班', '已入住', '3');
INSERT INTO `student` VALUES ('2025003', '万源', '男', '13951651512', '软件工程', '1班', '未入住', '4');

-- ----------------------------
-- Table structure for `user`
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `role` varchar(20) NOT NULL COMMENT '角色：STUDENT/ADMIN',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', 'admin', 'admin123', 'ADMIN', '2025-11-05 10:33:17');
INSERT INTO `user` VALUES ('2', 'student1', '123456', 'STUDENT', '2025-11-05 10:33:17');
INSERT INTO `user` VALUES ('3', 'student2', '123456', 'STUDENT', '2025-11-05 11:01:40');
INSERT INTO `user` VALUES ('4', 'student3', '123456', 'STUDENT', '2025-11-05 13:53:00');

-- ----------------------------
-- Table structure for `visitor`
-- ----------------------------
DROP TABLE IF EXISTS `visitor`;
CREATE TABLE `visitor` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `visitor_name` varchar(50) NOT NULL,
  `id_card` varchar(18) NOT NULL,
  `visited_student_id` varchar(20) NOT NULL,
  `entry_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `exit_time` timestamp NULL DEFAULT NULL,
  `status` varchar(20) DEFAULT '待审批' COMMENT '状态：待审批/已授权/已拒绝',
  `admin_remark` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `visited_student_id` (`visited_student_id`),
  CONSTRAINT `visitor_ibfk_1` FOREIGN KEY (`visited_student_id`) REFERENCES `student` (`student_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of visitor
-- ----------------------------
INSERT INTO `visitor` VALUES ('1', '张二', '357148141221245145', '2024001', '2025-11-05 10:57:58', '2025-11-05 11:37:55', '已授权', '同意');
INSERT INTO `visitor` VALUES ('2', '张一', '357248141221245', '2024001', '2025-11-05 11:38:29', null, '待审批', null);
INSERT INTO `visitor` VALUES ('3', '王艳梅', '357041753312844', '2025002', '2025-11-05 11:40:41', null, '已拒绝', '还未申请入住');
INSERT INTO `visitor` VALUES ('4', '张二', '357148141221245145', '2024001', '2025-11-05 13:51:40', '2025-11-05 13:52:04', '已授权', '同意');
