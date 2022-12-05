/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80030
 Source Host           : localhost:3306
 Source Schema         : im_server

 Target Server Type    : MySQL
 Target Server Version : 80030
 File Encoding         : 65001

 Date: 01/12/2022 00:50:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_friend
-- ----------------------------
DROP TABLE IF EXISTS `sys_friend`;
CREATE TABLE `sys_friend`  (
  `id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `friend_id` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_friend_id`(`friend_id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_friend
-- ----------------------------
INSERT INTO `sys_friend` VALUES (1, 1, 2);

-- ----------------------------
-- Table structure for sys_friend_add_request
-- ----------------------------
DROP TABLE IF EXISTS `sys_friend_add_request`;
CREATE TABLE `sys_friend_add_request`  (
  `id` bigint NOT NULL,
  `sender_id` bigint NOT NULL,
  `receiver_id` bigint NOT NULL,
  `send_time` datetime NOT NULL,
  `handle_time` timestamp NULL DEFAULT NULL COMMENT '处理好友请求的时间',
  `accepted` smallint NOT NULL DEFAULT 0 COMMENT '是否同意了好友请求',
  `remark` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '好友添加备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_receiver_id`(`receiver_id`) USING BTREE,
  INDEX `idx_send_time`(`send_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '好友添加请求记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_friend_add_request
-- ----------------------------
INSERT INTO `sys_friend_add_request` VALUES (1596489357656801281, 1596190150966370306, 1596081816133148674, '2022-11-26 21:01:39', NULL, 0, 'Hello,I\'m Sabrina');
INSERT INTO `sys_friend_add_request` VALUES (1596489357656801321, 1596488521287409666, 1596190150966370306, '2022-11-26 21:46:06', NULL, 0, 'Hello!!!');

-- ----------------------------
-- Table structure for sys_private_chat_message
-- ----------------------------
DROP TABLE IF EXISTS `sys_private_chat_message`;
CREATE TABLE `sys_private_chat_message`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `sender_id` bigint NOT NULL,
  `content` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `send_time` timestamp NOT NULL,
  `receiver_id` bigint NOT NULL,
  `is_read` smallint NOT NULL DEFAULT 0,
  `extend_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `extend_size` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `type` smallint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_send_time`(`send_time`) USING BTREE,
  INDEX `idx_sender_receiver_id`(`sender_id`, `receiver_id`) USING BTREE,
  INDEX `idx_receiver_read_send_time`(`receiver_id`, `is_read`, `send_time`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_private_chat_message
-- ----------------------------
INSERT INTO `sys_private_chat_message` VALUES ('1234123', 1, 'Hello, I am Test-User-1!', '2022-11-30 22:17:10', 2, 0, NULL, NULL, 1);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL,
  `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `gender` enum('男','女') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `register_time` datetime NOT NULL,
  `password` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `profile` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像',
  `last_login_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'Test-User-1', '男', '2022-11-30 22:02:19', '123456', NULL, NULL);
INSERT INTO `sys_user` VALUES (2, 'Test-User-2', '女', '2022-11-30 22:02:45', '123456', NULL, NULL);
INSERT INTO `sys_user` VALUES (1596081816133148674, 'Paddi', '男', '2022-11-25 18:02:13', '123456', NULL, NULL);
INSERT INTO `sys_user` VALUES (1596190150966370306, 'Sabrina', '男', '2022-11-26 01:12:42', '123456', NULL, NULL);
INSERT INTO `sys_user` VALUES (1596202767231352834, 'Miko', '男', '2022-11-26 02:02:50', '123456', NULL, NULL);
INSERT INTO `sys_user` VALUES (1596371099784048642, 'Paddi-Yan', '女', '2022-11-26 13:11:44', '123456', NULL, NULL);
INSERT INTO `sys_user` VALUES (1596487042942091265, 'Pikachu', '女', '2022-11-26 20:52:27', '123456', NULL, NULL);
INSERT INTO `sys_user` VALUES (1596487396299612162, 'TaylorSwift', '女', '2022-11-26 20:53:51', '123456', NULL, NULL);
INSERT INTO `sys_user` VALUES (1596488521287409666, 'Troye', '男', '2022-11-26 20:58:19', '123456', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;
