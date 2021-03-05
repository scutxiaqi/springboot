package com.example.producehelper.controller.stationUpdateBatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.producehelper.model.StationSelected;
import com.example.producehelper.service.inf.IExecuteSQLService;

/**
 * 执行sql脚本
 */
@RestController
@RequestMapping("/sqlRunner")
public class ExecuteSQLController
{
    @Autowired
    private IExecuteSQLService executeSQLService;

    /**
     * 在指定站点上执行run.sql里的内容
     * @param stationSelected
     * @return
     */
    @PostMapping("/run")
    public String runSql(@RequestBody StationSelected stationSelected)
    {
        try
        {
            return executeSQLService.runSql(stationSelected);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "FAIL";
        }
    }

    /**
     * 在指定站点上执行init.sql里的内容
     * @param stationSelected
     * @return
     */
    @PostMapping("/init")
    public String init(@RequestBody StationSelected stationSelected)
    {
        try
        {
            return executeSQLService.init(stationSelected);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "FAIL";
        }
    }
}
