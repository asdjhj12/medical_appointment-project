/*
 Navicat Premium Data Transfer

 Source Server         : JAVA-Data Source
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : medical_appointment

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 05/07/2025 23:01:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for appointment
-- ----------------------------
DROP TABLE IF EXISTS `appointment`;
CREATE TABLE `appointment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `patient_id` bigint NULL DEFAULT NULL,
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `schedule_id` bigint NOT NULL COMMENT '排班ID',
  `appointment_number` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预约编号',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0-待诊，1-已完成，2-已取消）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '症状描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_doctor_id`(`doctor_id` ASC) USING BTREE,
  INDEX `idx_schedule_id`(`schedule_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 66 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预约表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of appointment
-- ----------------------------
INSERT INTO `appointment` VALUES (65, 19, 4, 3, '2025070543CD6E', 2, 'asdas', '2025-07-05 14:30:06', '2025-07-05 14:30:06', 0);

-- ----------------------------
-- Table structure for department
-- ----------------------------
DROP TABLE IF EXISTS `department`;
CREATE TABLE `department`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '科室名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '科室描述',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '科室表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of department
-- ----------------------------
INSERT INTO `department` VALUES (1, '内科', '负责内科疾病的诊断和治疗', 1, '2025-05-26 17:37:18', '2025-05-26 17:37:18', 0);
INSERT INTO `department` VALUES (2, '外科', '负责外科手术和创伤治疗', 1, '2025-05-26 17:37:18', '2025-05-26 17:37:18', 0);
INSERT INTO `department` VALUES (3, '儿科', '专门治疗儿童疾病', 1, '2025-05-26 17:37:18', '2025-05-26 17:37:18', 0);
INSERT INTO `department` VALUES (4, '妇产科', '负责妇科疾病和产科服务', 1, '2025-05-26 17:37:18', '2025-05-26 17:37:18', 0);
INSERT INTO `department` VALUES (5, '眼科', '负责眼部疾病的诊断和治疗', 1, '2025-05-26 17:37:18', '2025-05-26 17:37:18', 0);
INSERT INTO `department` VALUES (6, '脑科', '负责检查大脑问题', 1, '2025-05-28 16:33:42', '2025-05-28 16:35:21', 1);
INSERT INTO `department` VALUES (7, '脑科就看见', '负责检查大脑问题', 1, '2025-05-28 16:35:50', '2025-05-28 16:36:15', 1);

-- ----------------------------
-- Table structure for doctor
-- ----------------------------
DROP TABLE IF EXISTS `doctor`;
CREATE TABLE `doctor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `doctor_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '医生姓名',
  `department_id` bigint NOT NULL COMMENT '科室ID',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '职称',
  `specialty` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '专长',
  `introduction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '简介',
  `gender` tinyint NULL DEFAULT 1 COMMENT '性别 0-女 1-男',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-禁用，1-启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_department_id`(`department_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '医生表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of doctor
-- ----------------------------
INSERT INTO `doctor` VALUES (1, 3, '张医生', 1, '主任医师', '心脏病学', '拥有30年心脏病治疗经验', 1, '13800000001', '110101198501010001', 1, '2025-05-28 11:21:20', '2025-05-28 21:05:43', 0);
INSERT INTO `doctor` VALUES (2, 4, '李医生', 2, '副主任医师', '骨科手术', '擅长各类骨科手术', 0, '13800000002', '110101199001010002', 1, '2025-05-28 11:21:20', '2025-05-28 21:05:45', 0);
INSERT INTO `doctor` VALUES (3, 5, '张伟', 2, '主任医师', '骨科手术', '擅长各类骨科手术', 1, '13800138000', '11010519491231002X', 1, '2025-05-28 11:34:01', '2025-05-28 21:05:49', 0);
INSERT INTO `doctor` VALUES (4, 6, '李娜', 2, '副主任医师', '骨科手术', '擅长骨科疾病的诊断与手术治疗。', 0, '13900139000', '11010519800101004X', 1, '2025-05-28 11:34:01', '2025-05-28 21:07:06', 0);
INSERT INTO `doctor` VALUES (5, 7, '王强', 3, '主治医师', '儿童常见病', '专注于儿童感冒、发烧、腹泻等常见病的诊治。', 1, '13700137000', '11010519780315001X', 1, '2025-05-28 11:34:01', '2025-05-29 10:38:32', 0);

-- ----------------------------
-- Table structure for medical_record
-- ----------------------------
DROP TABLE IF EXISTS `medical_record`;
CREATE TABLE `medical_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `appointment_id` bigint NOT NULL COMMENT '预约ID',
  `patient_id` bigint NOT NULL COMMENT '患者ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '诊断结果',
  `treatment_plan` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '治疗方案',
  `remarks` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `medicine_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用药详情（JSON）',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '病历状态（0-未处理，1-已诊断，2-已取药）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  `visit_time` datetime NULL DEFAULT NULL COMMENT '就诊日期',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '症状描述/主诉',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_appointment_id`(`appointment_id` ASC) USING BTREE,
  INDEX `idx_patient_id`(`patient_id` ASC) USING BTREE,
  INDEX `idx_doctor_id`(`doctor_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '病历表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medical_record
-- ----------------------------
INSERT INTO `medical_record` VALUES (15, 65, 19, 4, 'asdasd', 'asdasd', '吃清淡点食物', '[{\"dosageUsage\":\"一天三次，一次三片，吃半个月\",\"id\":125,\"medicalRecordId\":15,\"medicineId\":2,\"medicineName\":\"阿莫西林胶囊\",\"quantity\":45,\"unit\":\"粒\"},{\"dosageUsage\":\"一天三次，一次三片，吃半个月\",\"id\":126,\"medicalRecordId\":15,\"medicineId\":5,\"medicineName\":\"布洛芬胶囊\",\"quantity\":45,\"unit\":\"粒\"}]', 2, '2025-07-05 14:30:07', '2025-07-05 14:30:07', 0, '2025-07-05 17:20:24', 'asdas');

-- ----------------------------
-- Table structure for medical_record_medicine
-- ----------------------------
DROP TABLE IF EXISTS `medical_record_medicine`;
CREATE TABLE `medical_record_medicine`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `medical_record_id` bigint NOT NULL COMMENT '病历ID',
  `medicine_id` bigint NOT NULL COMMENT '药品ID',
  `medicine_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品名称',
  `quantity` int NOT NULL COMMENT '数量',
  `unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '单位',
  `dosage_usage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用法',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `medical_record_id`(`medical_record_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 127 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medical_record_medicine
-- ----------------------------
INSERT INTO `medical_record_medicine` VALUES (125, 15, 2, '阿莫西林胶囊', 45, '粒', '一天三次，一次三片，吃半个月');
INSERT INTO `medical_record_medicine` VALUES (126, 15, 5, '布洛芬胶囊', 45, '粒', '一天三次，一次三片，吃半个月');

-- ----------------------------
-- Table structure for medicine_inventory
-- ----------------------------
DROP TABLE IF EXISTS `medicine_inventory`;
CREATE TABLE `medicine_inventory`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '药品ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品名称',
  `specification` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规格',
  `manufacturer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生产厂家',
  `category` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '药品类别',
  `unit` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '单位',
  `price` decimal(10, 2) NULL DEFAULT NULL COMMENT '单价',
  `stock_quantity` int NOT NULL DEFAULT 0 COMMENT '库存数量',
  `min_stock` int NOT NULL DEFAULT 0 COMMENT '最低库存',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态：1-正常，0-停用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '药品库存表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medicine_inventory
-- ----------------------------
INSERT INTO `medicine_inventory` VALUES (1, '感冒灵颗粒', '10g/袋', '北京同仁堂', '感冒药', '袋', 12.50, 400, 50, 1, '2025-05-26 21:29:13', '2025-05-26 21:29:13', 0);
INSERT INTO `medicine_inventory` VALUES (2, '阿莫西林胶囊', '500mg/粒', '华北制药', '消炎药', '粒', 5.00, 310, 100, 1, '2025-05-26 21:29:13', '2025-05-26 21:29:13', 0);
INSERT INTO `medicine_inventory` VALUES (3, '硝苯地平缓释片', '20mg/片', '辉瑞制药', '降压药', '片', 8.75, 580, 60, 1, '2025-05-26 21:29:13', '2025-05-26 21:29:13', 0);
INSERT INTO `medicine_inventory` VALUES (4, '头孢克肟片', '100mg/片', '广州白云山', '抗生素', '片', 15.00, 400, 40, 1, '2025-05-26 21:29:13', '2025-05-26 21:29:13', 0);
INSERT INTO `medicine_inventory` VALUES (5, '布洛芬胶囊', '400mg/粒', '中美史克', '止痛药', '粒', 3.50, 420, 150, 1, '2025-05-26 21:29:13', '2025-05-26 21:29:13', 0);
INSERT INTO `medicine_inventory` VALUES (6, '维生素C片', '500mg/片', '养生堂', '维生素', '片', 2.00, 900, 200, 1, '2025-05-26 21:29:13', '2025-05-26 21:29:13', 0);
INSERT INTO `medicine_inventory` VALUES (7, '氯雷他定片', '10mg/片', '默沙东', '抗过敏药', '片', 12.00, 2000, 30, 1, '2025-05-26 21:29:13', '2025-05-26 21:29:13', 0);
INSERT INTO `medicine_inventory` VALUES (8, '二甲双胍片', '500mg/片', '诺和诺德', '糖尿病药', '片', 4.50, 810, 90, 1, '2025-05-26 21:29:13', '2025-05-26 21:29:13', 0);
INSERT INTO `medicine_inventory` VALUES (9, '奥美拉唑肠溶胶囊', '20mg/粒', '阿斯利康', '胃药', '粒', 10.00, 900, 50, 1, '2025-05-26 21:29:13', '2025-05-26 21:29:13', 0);

-- ----------------------------
-- Table structure for medicine_stock_record
-- ----------------------------
DROP TABLE IF EXISTS `medicine_stock_record`;
CREATE TABLE `medicine_stock_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `medicine_id` bigint NOT NULL COMMENT '药品ID',
  `medicine_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品名称',
  `type` tinyint NOT NULL COMMENT '操作类型（1-入库，2-出库）',
  `quantity` int NOT NULL COMMENT '操作数量',
  `before_stock` int NOT NULL COMMENT '操作前库存',
  `after_stock` int NOT NULL COMMENT '操作后库存',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作人姓名',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `operate_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_medicine_id`(`medicine_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 135 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '药品库存操作记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medicine_stock_record
-- ----------------------------
INSERT INTO `medicine_stock_record` VALUES (129, 8, '二甲双胍片', 2, 90, 810, 720, 10, 'admin', '用户取药', '2025-06-24 09:42:12', '2025-06-24 09:42:12', '2025-06-24 09:42:12', 0);
INSERT INTO `medicine_stock_record` VALUES (130, 5, '布洛芬胶囊', 2, 90, 510, 420, 10, 'admin', '用户取药', '2025-06-24 09:42:12', '2025-06-24 09:42:12', '2025-06-24 09:42:12', 0);
INSERT INTO `medicine_stock_record` VALUES (131, 2, '阿莫西林胶囊', 2, 45, 355, 310, 10, 'admin', '用户取药', '2025-07-05 17:20:41', '2025-07-05 17:20:41', '2025-07-05 17:20:41', 0);
INSERT INTO `medicine_stock_record` VALUES (132, 5, '布洛芬胶囊', 2, 45, 465, 420, 10, 'admin', '用户取药', '2025-07-05 17:20:41', '2025-07-05 17:20:41', '2025-07-05 17:20:41', 0);
INSERT INTO `medicine_stock_record` VALUES (133, 2, '阿莫西林胶囊', 2, 45, 310, 265, 10, 'admin', '用户取药', '2025-07-05 17:48:20', '2025-07-05 17:48:20', '2025-07-05 17:48:20', 0);
INSERT INTO `medicine_stock_record` VALUES (134, 5, '布洛芬胶囊', 2, 45, 420, 375, 10, 'admin', '用户取药', '2025-07-05 17:48:20', '2025-07-05 17:48:20', '2025-07-05 17:48:20', 0);

-- ----------------------------
-- Table structure for patient
-- ----------------------------
DROP TABLE IF EXISTS `patient`;
CREATE TABLE `patient`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `patient_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '患者姓名',
  `gender` tinyint NULL DEFAULT 1 COMMENT '性别 0-女 1-男',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `emergency_contact` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '紧急联系电话',
  `medical_history` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '病史',
  `allergies` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '过敏史',
  `blood_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '血型',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '患者信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of patient
-- ----------------------------
INSERT INTO `patient` VALUES (1, 8, '王患者', 1, 28, '13800000003', '110101199501010003', '北京市海淀区', '王父', '13900000001', NULL, NULL, NULL, NULL, '2025-05-28 11:21:11', '2025-05-28 19:14:45', 0);
INSERT INTO `patient` VALUES (2, 9, '赵患者', 0, 25, '13800000004', '110101200001010004', '北京市朝阳区', '赵母', '13900000002', NULL, NULL, NULL, NULL, '2025-05-28 11:21:11', '2025-05-28 19:14:48', 0);
INSERT INTO `patient` VALUES (19, 1, 'sdfsdfsdf', 2, NULL, '17856789567', '350628200305137787', 'sdfsdfsdf', NULL, NULL, NULL, NULL, NULL, NULL, '2025-07-05 00:01:05', '2025-07-05 00:01:05', 0);

-- ----------------------------
-- Table structure for schedule
-- ----------------------------
DROP TABLE IF EXISTS `schedule`;
CREATE TABLE `schedule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `doctor_id` bigint NOT NULL COMMENT '医生ID',
  `department_id` bigint NOT NULL COMMENT '科室ID',
  `schedule_date` date NOT NULL COMMENT '排班日期',
  `period` tinyint NOT NULL COMMENT '时段（1-上午，2-下午，3-晚上）',
  `max_appointments` int NOT NULL DEFAULT 20 COMMENT '最大预约数',
  `current_appointments` int NOT NULL DEFAULT 0 COMMENT '当前预约数',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0-停诊，1-正常）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_doctor_id`(`doctor_id` ASC) USING BTREE,
  INDEX `idx_department_id`(`department_id` ASC) USING BTREE,
  INDEX `idx_schedule_date`(`schedule_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 112 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '排班表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of schedule
-- ----------------------------
INSERT INTO `schedule` VALUES (1, 2, 1, '2025-05-26', 1, 20, 9, 1, '2025-05-26 08:00:00', '2025-06-11 10:48:04', 0);
INSERT INTO `schedule` VALUES (2, 3, 2, '2025-05-26', 2, 15, 5, 1, '2025-05-26 08:00:00', '2025-05-30 19:27:48', 0);
INSERT INTO `schedule` VALUES (3, 4, 3, '2025-05-26', 3, 10, 9, 1, '2025-05-26 08:00:00', '2025-05-29 16:42:44', 0);
INSERT INTO `schedule` VALUES (4, 1, 1, '2025-05-29', 1, 20, 11, 1, '2025-05-28 21:41:20', '2025-06-16 12:10:27', 0);
INSERT INTO `schedule` VALUES (105, 2, 2, '2025-06-10', 2, 4, 0, 1, '2025-06-06 17:46:49', '2025-06-06 17:49:11', 1);
INSERT INTO `schedule` VALUES (106, 1, 1, '2025-06-06', 2, 3, 0, 1, '2025-06-06 17:49:38', '2025-06-06 17:49:38', 0);
INSERT INTO `schedule` VALUES (107, 2, 2, '2025-05-27', 3, 1, 0, 1, '2025-06-06 23:49:16', '2025-06-06 23:49:19', 1);
INSERT INTO `schedule` VALUES (108, 5, 3, '2025-06-25', 2, 2, 0, 1, '2025-06-10 21:21:54', '2025-06-10 21:22:04', 1);
INSERT INTO `schedule` VALUES (109, 2, 2, '2025-06-30', 3, 10, 0, 1, '2025-06-24 00:22:30', '2025-06-24 00:22:52', 1);
INSERT INTO `schedule` VALUES (110, 4, 0, '2025-06-26', 2, 10, 0, 1, '2025-06-24 10:10:14', '2025-06-24 10:10:26', 1);
INSERT INTO `schedule` VALUES (111, 1, 1, '2025-07-13', 3, 3, 0, 1, '2025-07-05 18:01:49', '2025-07-05 18:01:49', 0);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `gender` tinyint NULL DEFAULT 1 COMMENT '性别 0-女 1-男',
  `birth_date` date NULL DEFAULT NULL COMMENT '出生日期',
  `id_card` varchar(18) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像地址',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `register_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '注册IP',
  `register_time` datetime NULL DEFAULT NULL COMMENT '注册时间',
  `is_verified` tinyint NULL DEFAULT 0 COMMENT '是否已验证 0-未验证 1-已验证',
  `is_deleted` tinyint NULL DEFAULT 0 COMMENT '是否已删除 0-未删除 1-已删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '用户状态：0-禁用，1-正常',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER' COMMENT '用户角色：ADMIN-管理员，USER-普通用户',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  UNIQUE INDEX `uk_id_card`(`id_card` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 70 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'asdasdasd', '$2a$10$j8bQK.ok2qZCBfVTPTuMZOB8DysdTkGvHWkJHOpYeldI3MiT5t2We', 'sdfsdfsdf', '17856789567', '1589786789@qq.com', 2, NULL, '350628200305137787', 'sdfsdfsdf', NULL, NULL, '0:0:0:0:0:0:0:1', '2025-05-26 13:02:29', 0, 0, '2025-05-26 13:02:29', '2025-06-06 23:51:39', 0, 1, 'USER');
INSERT INTO `user` VALUES (2, 'user2', '$2a$10$tolgHGCH98TQ9tTYaLqa1OQvlitx6bFsVezYrIG0.94bRZj3X1G8i', 'user2', '17867856789', '1349867564@qq.com', 1, NULL, '350628200305285532', 'user2', NULL, NULL, '0:0:0:0:0:0:0:1', '2025-05-28 11:26:31', 0, 0, '2025-05-28 11:26:31', '2025-05-28 11:26:50', 0, 1, 'USER');
INSERT INTO `user` VALUES (3, 'doctor1', '$2a$10$tolgHGCH98TQ9tTYaLqa1OQvlitx6bFsVezYrIG0.94bRZj3X1G8i', '张医生', '13800000001', 'doctor1@example.com', 1, '1985-01-01', '110101198501010001', NULL, NULL, NULL, NULL, NULL, 0, 0, '2025-05-28 11:21:55', '2025-05-28 11:38:30', 0, 1, 'DOCTOR');
INSERT INTO `user` VALUES (4, 'doctor2', '$2a$10$tolgHGCH98TQ9tTYaLqa1OQvlitx6bFsVezYrIG0.94bRZj3X1G8i', '李医生', '13800000002', 'doctor2@example.com', 0, '1990-01-01', '110101199001010002', NULL, NULL, NULL, NULL, NULL, 0, 0, '2025-05-28 11:21:55', '2025-05-28 11:39:53', 0, 1, 'DOCTOR');
INSERT INTO `user` VALUES (5, 'doctor3', '$2a$10$tolgHGCH98TQ9tTYaLqa1OQvlitx6bFsVezYrIG0.94bRZj3X1G8i', '张伟', '13800138000', 'zhangwei@example.com', 1, '1970-01-01', '11010519491231002X', '北京市朝阳区', 'http://example.com/avatar/zhangwei.jpg', NULL, '0:0:0:0:0:0:0:1', '2025-05-28 11:37:28', 1, 0, '2025-05-28 11:37:28', '2025-05-28 11:39:56', 0, 1, 'DOCTOR');
INSERT INTO `user` VALUES (6, 'doctor4', '$2a$10$TeRYz89WXsjp6gA5Lv997usVAE.wDbqzeABGWSg3NN4Cry8H0ZgCS', '李娜', '13900139089', 'lina@example.com', 0, '1980-01-01', '11010519800101004X', '北京市海淀区', '', NULL, '0:0:0:0:0:0:0:1', '2025-05-28 11:37:28', 1, 0, '2025-05-28 11:37:28', '2025-05-30 00:22:30', 0, 1, 'DOCTOR');
INSERT INTO `user` VALUES (7, 'doctor5', '$2a$10$tolgHGCH98TQ9tTYaLqa1OQvlitx6bFsVezYrIG0.94bRZj3X1G8i', '王强', '13700137000', 'wangqiang@example.com', 1, '1978-03-15', '11010519780315001X', '北京市丰台区', 'http://example.com/avatar/wangqiang.jpg', NULL, '0:0:0:0:0:0:0:1', '2025-05-28 11:37:28', 1, 0, '2025-05-28 11:37:28', '2025-05-28 11:41:37', 0, 1, 'DOCTOR');
INSERT INTO `user` VALUES (8, 'patient1', '$2a$10$tolgHGCH98TQ9tTYaLqa1OQvlitx6bFsVezYrIG0.94bRZj3X1G8i', '王患者', '13800000003', 'patient1@example.com', 1, '1995-01-01', '110101199501010003', NULL, NULL, NULL, NULL, NULL, 0, 0, '2025-05-28 11:21:55', '2025-05-28 11:40:11', 0, 1, 'PATIENT');
INSERT INTO `user` VALUES (9, 'patient2', '$2a$10$tolgHGCH98TQ9tTYaLqa1OQvlitx6bFsVezYrIG0.94bRZj3X1G8i', '赵患者', '13800000004', 'patient2@example.com', 0, '2000-01-01', '110101200001010004', NULL, NULL, NULL, NULL, NULL, 0, 0, '2025-05-28 11:21:55', '2025-05-28 11:40:32', 0, 1, 'PATIENT');
INSERT INTO `user` VALUES (10, 'admin', '$2a$10$tolgHGCH98TQ9tTYaLqa1OQvlitx6bFsVezYrIG0.94bRZj3X1G8i', 'admin', '17867956789', '1789567890@qq.com', 1, NULL, '350628200202275532', 'asdasdasdasd', NULL, NULL, '0:0:0:0:0:0:0:1', '2025-05-27 22:27:07', 0, 0, '2025-05-27 22:27:06', '2025-05-28 11:40:36', 0, 1, 'ADMIN');
INSERT INTO `user` VALUES (16, '123123123', '$2a$10$oc3CVXZ6WPDf0BZBwzp3aO6spF8Ybil9VrzLl/v/bJRsIK7PeZRXy', '123123123', '16789789090', '1256789078@qq.com', 1, NULL, '350628200606054567', 'asd', NULL, NULL, '0:0:0:0:0:0:0:1', '2025-06-05 10:55:21', 0, 0, '2025-06-05 10:55:20', '2025-06-05 10:55:20', 0, 1, 'USER');

SET FOREIGN_KEY_CHECKS = 1;
