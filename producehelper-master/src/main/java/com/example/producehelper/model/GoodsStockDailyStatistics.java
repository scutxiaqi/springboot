package com.example.producehelper.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodsStockDailyStatistics implements Serializable
{
    private static final long serialVersionUID = -8434895768351833812L;

    private Long pkno;

    private Date ctime;

    private Date mtime;

    private String cuser;

    private String muser;

    private String goodsId;

    private Date statisticsDate;

    private Long beginStock;

    private BigDecimal beginValue;

    private Long receiveCount;

    private Long returnCount;

    private Long saleCount;

    private Long adjustCount;

    private Long endStock;

    private BigDecimal endValue;

    private BigDecimal transferAvgPrice;

}
