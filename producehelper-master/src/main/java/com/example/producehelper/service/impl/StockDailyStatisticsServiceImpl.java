package com.example.producehelper.service.impl;

import com.example.producehelper.mapper.SConfigMapper;
import com.example.producehelper.mapper.StockDailyStatisticsMapper;
import com.example.producehelper.model.*;
import com.example.producehelper.service.inf.IStockDailyStatisticsService;
import com.example.producehelper.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StockDailyStatisticsServiceImpl implements IStockDailyStatisticsService
{
    @Value("${file.sqlLogFile}")
    private String sqlLogFile;

    @Autowired
    private StockDailyStatisticsMapper stockDailyStatisticsMapper;

    @Autowired
    private SConfigMapper sConfigMapper;

    /**
     * 初始化b_goods_stock_daily_statistics表后，第二天需要手动调用该接口统计前一天的数据
     * @return
     * @throws Exception
     */
    public String updateStockDailyStatistics_v1() throws Exception
    {
        Date date = stockDailyStatisticsMapper.getUpdateTime();

        Date endTime = DateUtils.addDate(DateUtils.getToday(), 0, 0, 0, 8, 0, 0, 0 );
//        Date endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-08-17 11:00:00");
        List<GoodsStockRecord> latestRecords = stockDailyStatisticsMapper.getLatestRecords(date, endTime);

        if (latestRecords == null || latestRecords.isEmpty())
        {
            System.out.println("不需要处理");
            return "FINISH";
        }

        Map<String, List<GoodsStockRecord>> goodsId2RecordList = latestRecords.stream().collect(Collectors.groupingBy(GoodsStockRecord::getGoodsId));

//        Date statisticsDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-08-17 00:00:00");
        Date statisticsDate = DateUtils.addDate(DateUtils.getToday(), 0, 0, -1, 0, 0, 0, 0);
        List<GoodsStockDailyStatistics> stockDailyStatistics = stockDailyStatisticsMapper.getAllStockDailyStatistics(statisticsDate);
        Map<String, GoodsStockDailyStatistics> goodsId2StatisticsList = stockDailyStatistics.stream().collect(Collectors.toMap(goodsStockDailyStatistics -> goodsStockDailyStatistics.getGoodsId(), goodsStockDailyStatistics -> goodsStockDailyStatistics));

        List<GoodsStockDailyStatistics> updateList = new ArrayList<>(0);
        for (Map.Entry<String, List<GoodsStockRecord>> entry : goodsId2RecordList.entrySet())
        {
            String goodsId = entry.getKey();
            List<GoodsStockRecord> records = entry.getValue();
            GoodsStockDailyStatistics statistics = goodsId2StatisticsList.get(goodsId);
            Integer count = 0;
            for (GoodsStockRecord record : records)
            {
                Integer recordType = record.getStockRecordType();
                switch (recordType)
                {
                    case 22:
                        count -= record.getStockRecordCount();
                        break;
                    case 23:
                        count += record.getStockRecordCount();
                        break;
                }
            }
            if (count != 0)
            {
                statistics.setEndStock(statistics.getEndStock() + count);
                statistics.setReceiveCount(statistics.getReceiveCount() + count);
                statistics.setEndValue(statistics.getTransferAvgPrice().multiply(new BigDecimal(statistics.getEndStock())));
                updateList.add(statistics);
            }
        }

        System.out.println(updateList);

        for (GoodsStockDailyStatistics statistics : updateList)
        {
            stockDailyStatisticsMapper.updateStatistics(statistics);
        }

        return "SUCCESS";
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateStockDailyStatistics(StationDataSource station)
    {
        String stationName = sConfigMapper.findDefValueByName("station_name");
        log.info("[{}]开始执行", stationName);

        //获取当前库存
        Date currentTime = new Date();
        List<GoodsStock> goodsStocksList = stockDailyStatisticsMapper.getAllGoodsStock();
        if (goodsStocksList == null || goodsStocksList.isEmpty())
        {
            log.warn("没有库存，不用修改");
            stockDailyStatisticsMapper.clearGoodsDailyStock();
            return "SKIP";
        }
        Map<String, Integer> goodsId2StockMap = goodsStocksList.stream().collect(Collectors.toMap(GoodsStock::getGoodsId, GoodsStock::getGoodsCount));
        Map<String, String> goodsId2TypeMap = goodsStocksList.stream().collect(Collectors.toMap(GoodsStock::getGoodsId, GoodsStock::getFirstCategoryId));

        //获取最近日结日
        Map<String, Object> lastDailyShift = stockDailyStatisticsMapper.getLastCloseTime();
        Date lastCloseTime = (Date) lastDailyShift.get("close_shift_time");
        String lastDailyDay = (String) lastDailyShift.get("settlement_date");
        if (lastCloseTime == null)
        {
            log.warn("没有日结，不用修改");
            stockDailyStatisticsMapper.clearGoodsDailyStock();
            return "SKIP";
        }

        //统计当前时间点库存与上次日结结束时间之间的库存差异
        if (lastCloseTime.compareTo(currentTime) > 0)
        {
            log.warn("日结时间超过当前时间！！");
            return "";
        }
        List<GoodsStockRecord> goodsStockRecords = stockDailyStatisticsMapper.getLatestRecords(lastCloseTime, currentTime);
        if (goodsStockRecords != null && !goodsStockRecords.isEmpty())
        {
            Map<String, List<GoodsStockRecord>> goodsId2RecordList = goodsStockRecords.stream().collect(Collectors.groupingBy(GoodsStockRecord::getGoodsId));
            for (Map.Entry<String, List<GoodsStockRecord>> entry : goodsId2RecordList.entrySet())
            {
                String goodsId = entry.getKey();
                List<GoodsStockRecord> records = entry.getValue();
                Integer count = 0;
                for (GoodsStockRecord record : records)
                {
                    count += (record.getGoodsCountOld() - record.getGoodsCountNow());
                }
                if (count != 0)
                {
                    Integer nowStock = goodsId2StockMap.get(goodsId);
                    goodsId2StockMap.put(goodsId, nowStock + count);
                }
            }
        }

        //获取最新订货价
        List<SupplierGoods> supplierGoods = stockDailyStatisticsMapper.getAllSupplierGoods();
        Map<String, SupplierGoods> goodsId2OrderPrice = supplierGoods.stream().collect(Collectors.toMap(SupplierGoods::getGoodsId, supplierGood -> supplierGood, (v1, v2) -> (v1.getPkno() > v2.getPkno() ? v1 : v2)));

        //调整上一次日结日的库存变动记录
        Date lastDailyDate = DateUtils.parseStrToDate(lastDailyDay, "yyyy-MM-dd");
        List<GoodsStockDailyStatistics> goodsStockDailyStatistics = stockDailyStatisticsMapper.getAllStockDailyStatistics(lastDailyDate);

        Map<String, GoodsStockDailyStatistics> goodsId2DailyStock = new HashMap<>(0);
        if (goodsStockDailyStatistics != null && !goodsStockDailyStatistics.isEmpty())
        {
            goodsId2DailyStock = goodsStockDailyStatistics.stream().collect(Collectors.toMap(GoodsStockDailyStatistics::getGoodsId, dailyStock->dailyStock));
        }

        for (Map.Entry<String, Integer> entry : goodsId2StockMap.entrySet())
        {
            String goodsId = entry.getKey();
            Integer endStock = entry.getValue();
            BigDecimal orderPrice = goodsId2OrderPrice.get(goodsId) == null ? new BigDecimal("0.0000") : goodsId2OrderPrice.get(goodsId).getGoodsOrderPrice();
            if (!"01".equals(goodsId2TypeMap.get(goodsId)))
            {
                orderPrice = orderPrice.multiply(new BigDecimal("1.05"));
            }

            GoodsStockDailyStatistics dailyStockStatistics = goodsId2DailyStock.get(goodsId);
            if (dailyStockStatistics == null)
            {
                dailyStockStatistics = new GoodsStockDailyStatistics();
                dailyStockStatistics.setCtime(lastCloseTime);
                dailyStockStatistics.setCuser("system");
                dailyStockStatistics.setMtime(lastCloseTime);
                dailyStockStatistics.setMuser("system");
                dailyStockStatistics.setGoodsId(goodsId);
                dailyStockStatistics.setStatisticsDate(lastDailyDate);
                dailyStockStatistics.setBeginStock(0L);
                dailyStockStatistics.setBeginValue(new BigDecimal("0.0000"));
                dailyStockStatistics.setReceiveCount(0L);
                dailyStockStatistics.setReturnCount(0L);
                dailyStockStatistics.setSaleCount(0L);
                dailyStockStatistics.setAdjustCount(endStock.longValue());
                dailyStockStatistics.setEndStock(endStock.longValue());
                dailyStockStatistics.setEndValue(orderPrice.multiply(new BigDecimal(endStock)));
                dailyStockStatistics.setTransferAvgPrice(orderPrice);
                stockDailyStatisticsMapper.insertDailyStockStatistics(dailyStockStatistics);
            }
            else
            {
                dailyStockStatistics.setCtime(lastCloseTime);
                dailyStockStatistics.setMtime(lastCloseTime);
                Long adjustCount = endStock-dailyStockStatistics.getBeginStock()-dailyStockStatistics.getAdjustCount()+dailyStockStatistics.getSaleCount();
                dailyStockStatistics.setAdjustCount(adjustCount);
                dailyStockStatistics.setEndStock(endStock.longValue());
                dailyStockStatistics.setEndValue(orderPrice.multiply(new BigDecimal(endStock)));
                dailyStockStatistics.setTransferAvgPrice(orderPrice);
                stockDailyStatisticsMapper.updateStatistics(dailyStockStatistics);
            }
        }

        //清除上次日结日之后的数据
        Long deleteNums = stockDailyStatisticsMapper.getStockDailyStatisticsAfter(lastDailyDate);
        if (deleteNums > 0)
        {
            stockDailyStatisticsMapper.deleteStockDailyStatisticsAfter(lastDailyDate);
        }

        log.info("[{}]执行结束", stationName);
        return "SUCCESS";
    }
}
