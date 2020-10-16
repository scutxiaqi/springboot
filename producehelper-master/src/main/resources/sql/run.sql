ALTER TABLE b_goods_price_change ADD COLUMN original_price decimal(10,2) DEFAULT 0 COMMENT '原价';
ALTER TABLE b_goods_history ADD COLUMN goods_order_price decimal(10,2) DEFAULT 0 COMMENT '订货价';
ALTER TABLE b_goods_history ADD COLUMN goods_sale_price decimal(10,2) DEFAULT 0 COMMENT '零售价';
UPDATE b_goods_price SET muser='xiaqi', mtime=NOW(), is_delete=0 WHERE is_delete=1;
INSERT INTO `s_config` (`ctime`, `mtime`, `muser`, `cuser`, `varname`, `description`, `vartype`, `defvalue`) VALUES (NOW(), NOW(), 'xiaqi', 'xiaqi', 'store_state', '便利店状态：1-可以卖 | 0-不能卖', 'string', '1');
INSERT INTO `s_config` (`ctime`, `mtime`, `muser`, `cuser`, `varname`, `description`, `vartype`, `defvalue`) VALUES (NOW(), NOW(), 'xiaqi', 'xiaqi', 'is_update_stock', '是否可以修改库存：1-是 | 0-否', 'string', '1');