package com.example.producehelper.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.io.Serializable;

@Data
public class StationDataSource implements Serializable
{
    private static final long serialVersionUID = 5305696345895003948L;

    @Excel(name = "站点编号")
    private String stationId;

    @Excel(name = "站点名称")
    private String stationName;

    @Excel(name = "ip")
    private String stationIp;

    @Excel(name = "用户名")
    private String userName;

    @Excel(name = "数据库密码")
    private String dataBasePass;

    @Excel(name = "工控机密码")
    private String sshPass;

    @Excel(name = "子公司编号")
    private String subUnitCode;

    @Excel(name = "能否正常连接")
    private String isActive;

}
