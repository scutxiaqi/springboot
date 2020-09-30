-- 供应商商品表
DROP TABLE IF EXISTS b_supplier_goods_temp_init;
CREATE TABLE `b_supplier_goods_temp_init` (
    `pkno` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `mtime` datetime DEFAULT NULL COMMENT '修改时间',
    `cuser` varchar(20) NOT NULL COMMENT '创建人',
    `muser` varchar(20) DEFAULT NULL COMMENT '修改人',
    `supplier_id` varchar(20) NOT NULL COMMENT '供应商编码',
    `goods_id` varchar(50) NOT NULL COMMENT '商品编码',
    `goods_bar_code` varchar(50) NOT NULL COMMENT '商品条形码',
    `goods_order_price` decimal(15,4) NOT NULL COMMENT '订货价格',
    `min_order_count` int(11) DEFAULT NULL COMMENT '最小起订量',
    `delivery_type` varchar(20) DEFAULT NULL COMMENT '配送方式',
    `active_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
    `append_file_url` varchar(300) DEFAULT NULL COMMENT '上传附件的地址，供应商送货的文档，excel文件或其他',
    `purchase_id` varchar(20) DEFAULT NULL COMMENT '采购方编码',
    `purchase_name` varchar(255) DEFAULT NULL COMMENT '采购方名称',
    `spec` int(11) DEFAULT NULL COMMENT '叫货规格',
    `delivery_day` int(11) DEFAULT NULL COMMENT '配送周期(天)',
    `goods_sale_price` decimal(9,2) DEFAULT NULL COMMENT '建议零售价',
    PRIMARY KEY (`pkno`) USING BTREE,
    KEY `index_1` (`goods_id`),
    KEY `index_2` (`supplier_id`),
    KEY `index_3` (`purchase_id`),
    KEY `index_5` (`goods_id`,`purchase_id`) USING BTREE,
    KEY `index_4` (`goods_id`,`supplier_id`,`purchase_id`,`active_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商商品信息表';

-- 删除定时器
DROP EVENT IF EXISTS goods_stock_daily_event;
DROP PROCEDURE IF EXISTS goods_stock_statistic_proc;

CREATE TABLE IF NOT EXISTS `b_goods_discount` (
    `pkno` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `ctime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `mtime` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
    `cuser` varchar(20) NOT NULL COMMENT '创建人',
    `muser` varchar(20) NOT NULL COMMENT '修改人',
    `order_id` varchar(50) NOT NULL COMMENT '促销批次号',
    `goods_id` varchar(50) NOT NULL COMMENT '商品编号',
    `discount_price` decimal(9,2) NOT NULL COMMENT '价格/折扣值',
    `discount_type` varchar(3) NOT NULL COMMENT '折扣类型 1-会员价|2-促销价',
    `max_count` int(11) DEFAULT '-1' COMMENT '最大销售数量 -1-无限制',
    `param_1` varchar(10) DEFAULT NULL COMMENT '参数1',
    `param_2` varchar(10) DEFAULT NULL COMMENT '参数2',
    `active_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生效时间',
    `expire_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '失效时间',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `is_delete` char(1) DEFAULT '0' COMMENT '是否删除 0-未删除|1-已删除',
    PRIMARY KEY (`pkno`),
    KEY `index1` (`order_id`,`goods_id`,`discount_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='门店可经营商品促销明细表';

-- 促销
-- INSERT INTO `s_dictionary`(`cuser`, `muser`, `table_name`, `field_name`, `field_value`, `field_text`) VALUES ('system', 'system', 'b_goods_discount', 'discount_type', '1', '会员价');
-- INSERT INTO `s_dictionary`(`cuser`, `muser`, `table_name`, `field_name`, `field_value`, `field_text`) VALUES ('system', 'system', 'b_goods_discount', 'discount_type', '2', '促销价');

UPDATE `s_dictionary` SET field_text = '退货回供应商' WHERE table_name = 'b_goods_stock_record' and field_name = 'stock_record_type' and field_value = '4';

-- 修改b_goods_record的订货价字段精度
ALTER TABLE `b_goods_stock_record`
    MODIFY COLUMN `price` decimal(11, 4) NULL DEFAULT NULL COMMENT '单价(入库时对应进货单价)' AFTER `stock_record_type`;

ALTER TABLE `b_goods_stock_daily_statistics`
    MODIFY COLUMN `end_value` decimal(15, 4) NULL DEFAULT 0.0000 COMMENT '期末价值' AFTER `end_stock`;

ALTER TABLE `b_goods_stock_daily_statistics`
    MODIFY COLUMN `begin_value` decimal(15, 4) NULL DEFAULT 0.0000 COMMENT '期初价值' AFTER `begin_stock`;