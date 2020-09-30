package com.example.producehelper.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SupplierGoods implements Serializable
{
    private static final long serialVersionUID = -1132836715395432896L;

    private Integer pkno;

    private Date ctime;

    private Date mtime;

    private String cuser;

    private String muser;

    private String supplierId;

    private String goodsId;

    private String goodsBarCode;

    private BigDecimal goodsOrderPrice;

    private BigDecimal goodsSalePrice;

    private Integer minOrderCount;

    private String deliveryType;

    private Date activeTime;

    private String purchaseId;

    private String purchaseName;

    private Integer spec;

    private Integer deliveryDay;
}
