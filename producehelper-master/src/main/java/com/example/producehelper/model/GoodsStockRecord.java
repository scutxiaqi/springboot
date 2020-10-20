package com.example.producehelper.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import cn.afterturn.easypoi.excel.annotation.Excel;

@Data
public class GoodsStockRecord implements Serializable
{
    private static final long serialVersionUID = -57058356527035662L;

    private Integer pkno;

    private String cuser;

    private Date ctime;

    private String muser;

    private Date mtime;

    @Excel(name = "商品编码", width = 18)
    private String goodsId;

    private Integer goodsCountOld;

    private Integer goodsCountNow;

    private Integer stockRecordCount;

    private Integer stockRecordType;
    
    @Excel(name = "订货价")
    @JsonFormat(pattern = "\\d{1,8}\\.\\d{4}",shape = JsonFormat.Shape.STRING)
    private BigDecimal price;
}
