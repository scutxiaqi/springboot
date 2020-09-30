package com.example.producehelper.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GoodsStockRecord implements Serializable
{
    private static final long serialVersionUID = -57058356527035662L;

    private Integer pkno;

    private String cuser;

    private Date ctime;

    private String muser;

    private Date mtime;

    private String goodsId;

    private Integer goodsCountOld;

    private Integer goodsCountNow;

    private Integer stockRecordCount;

    private Integer stockRecordType;
}
