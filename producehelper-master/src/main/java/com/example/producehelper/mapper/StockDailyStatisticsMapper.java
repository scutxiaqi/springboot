package com.example.producehelper.mapper;

import com.example.producehelper.model.GoodsStock;
import com.example.producehelper.model.GoodsStockDailyStatistics;
import com.example.producehelper.model.GoodsStockRecord;
import com.example.producehelper.model.SupplierGoods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StockDailyStatisticsMapper
{
    Date getUpdateTime();

    List<GoodsStockRecord> getLatestRecords(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    List<GoodsStockDailyStatistics> getAllStockDailyStatistics(@Param("statisticsDate") Date statisticsDate);

    void updateStatistics(GoodsStockDailyStatistics statistics);

    List<GoodsStock> getAllGoodsStock();

    void clearGoodsDailyStock();

    Map<String, Object> getLastCloseTime();

    List<SupplierGoods> getAllSupplierGoods();

    void insertDailyStockStatistics(GoodsStockDailyStatistics dailyStockStatistics);

    Long getStockDailyStatisticsAfter(@Param("statisticsDate") Date lastDailyDate);

    void deleteStockDailyStatisticsAfter(@Param("statisticsDate") Date lastDailyDate);


}
