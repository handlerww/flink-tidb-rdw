DROP DATABASE IF EXISTS test;
CREATE DATABASE test; 
USE test;

create table wide_stuff(
  stuff_id int primary key,
  base_id int,
  base_location varchar(20),
  stuff_name varchar(20)
);

DROP DATABASE IF EXISTS tpcc;
CREATE DATABASE tpcc; 
CREATE TABLE `tpcc`.`wide_customer_warehouse`  (
  `c_id` int(11) NOT NULL,
  `c_d_id` int(11) NOT NULL,
  `c_w_id` int(11) NOT NULL,
  `c_first` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `c_middle` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `c_last` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `c_street_1` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `c_street_2` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `c_city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `c_state` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `c_zip` char(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `c_phone` char(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `c_since` datetime(0) NULL DEFAULT NULL,
  `c_credit` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `c_credit_lim` decimal(12, 2) NULL DEFAULT NULL,
  `c_discount` decimal(4, 4) NULL DEFAULT NULL,
  `c_balance` decimal(12, 2) NULL DEFAULT NULL,
  `c_ytd_payment` decimal(12, 2) NULL DEFAULT NULL,
  `c_payment_cnt` int(11) NULL DEFAULT NULL,
  `c_delivery_cnt` int(11) NULL DEFAULT NULL,
  `c_data` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_street_1` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_street_2` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_state` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_zip` char(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_tax` decimal(4, 4) NULL DEFAULT NULL,
  `w_ytd` decimal(12, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`c_w_id`, `c_d_id`, `c_id`),
  INDEX `idx_customer`(`c_w_id`, `c_d_id`, `c_last`, `c_first`)
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;


CREATE TABLE `tpcc`.`wide_new_order`  (
  `no_o_id` int(11) NOT NULL,
  `no_d_id` int(11) NOT NULL,
  `no_w_id` int(11) NOT NULL,
  `d_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_street_1` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_street_2` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_state` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_zip` char(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_tax` decimal(4, 4) NULL DEFAULT NULL,
  `d_ytd` decimal(12, 2) NULL DEFAULT NULL,
  `d_next_o_id` int(11) NULL DEFAULT NULL,
  `w_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_street_1` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_street_2` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_state` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_zip` char(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `w_tax` decimal(4, 4) NULL DEFAULT NULL,
  `w_ytd` decimal(12, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`no_o_id`)
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;

CREATE TABLE `tpcc`.`wide_order_line_district`  (
  `ol_o_id` int(11) NOT NULL,
  `ol_d_id` int(11) NOT NULL,
  `ol_w_id` int(11) NOT NULL,
  `ol_number` int(11) NOT NULL,
  `ol_i_id` int(11) NOT NULL,
  `ol_supply_w_id` int(11) NULL DEFAULT NULL,
  `ol_delivery_d` datetime(0) NULL DEFAULT NULL,
  `ol_quantity` int(11) NULL DEFAULT NULL,
  `ol_amount` decimal(6, 2) NULL DEFAULT NULL,
  `ol_dist_info` char(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_name` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_street_1` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_street_2` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_city` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_state` char(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_zip` char(9) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL,
  `d_tax` decimal(4, 4) NULL DEFAULT NULL,
  `d_ytd` decimal(12, 2) NULL DEFAULT NULL,
  `d_next_o_id` int(11) NULL DEFAULT NULL,

  PRIMARY KEY (`ol_w_id`, `ol_d_id`, `ol_o_id`, `ol_number`)
) CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Compact;