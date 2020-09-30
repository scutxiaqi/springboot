package com.example.producehelper.controller.stock;

import com.alibaba.druid.support.spring.stat.annotation.Stat;
import com.example.producehelper.dataSource.DynamicDataSource;
import com.example.producehelper.model.StationDataSource;
import com.example.producehelper.model.StationSelected;
import com.example.producehelper.service.inf.IStockDailyStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/stockDailyStatistics")
public class StockDailyStatisticsController
{
    @Autowired
    private IStockDailyStatisticsService stockDailyStatisticsService;

    @Autowired
    @Qualifier("stations")
    private Set<StationDataSource> stationDataSourceList;

    /**
     * 初始化b_goods_stock_daily_statistics表数据
     * @return
     */
    @PostMapping("/update")
    public String updateStockDailyStatistics(@RequestBody StationSelected stationSelected)
    {
        try
        {
            Set<StationDataSource> stations = getStations(stationSelected);
            for (StationDataSource station : stations)
            {
                DynamicDataSource.setDataSourceKey(station.getStationId());
                stockDailyStatisticsService.updateStockDailyStatistics(station);
                DynamicDataSource.clearDataSourceKey();
            }
            return "SUCCESS";
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return "ERROR";
    }

    private Set<StationDataSource> getStations(StationSelected stationSelected)
    {
        String allSelected = stationSelected.getSelected();
        if ("all".equals(allSelected))
        {
            return stationDataSourceList;
        }
        else
        {
            Set<StationDataSource> stations = new LinkedHashSet<>();
            List<String> stationIds = stationSelected.getStations();
            for (StationDataSource station : stationDataSourceList)
            {
                if (stationIds.contains(station.getStationId()))
                {
                    stations.add(station);
                }
            }
            return stations;
        }
    }
}
