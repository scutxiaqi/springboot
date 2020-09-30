package com.example.producehelper.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GoodsStock implements Serializable
{
    private static final long serialVersionUID = 1325457736613185065L;

    private Long pkno;

    private String cuser;

    private Date ctime;

    private String muser;

    private Date mtime;

    private String goodsId;

    private Integer goodsCount;

    private String firstCategoryId;
}
