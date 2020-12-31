package com.example.producehelper.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.producehelper.dataSource.DynamicDataSource;
import com.example.producehelper.model.GoodsStockRecord;
import com.example.producehelper.model.StationDataSource;
import com.example.producehelper.model.StationSelected;
import com.example.producehelper.model.common.Constants;
import com.example.producehelper.model.common.ExecuteRunSqlResult;
import com.example.producehelper.service.inf.IExecuteSQLService;
import com.example.producehelper.util.DateUtil;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;

@Service
@Transactional(rollbackFor = Exception.class)
public class ExecuteSQLServiceImpl implements IExecuteSQLService {
    @Value("${file.sqlLogFile}")
    private String sqlLogFile;

    @Autowired
    private DynamicDataSource dynamicDataSource;

    @Autowired
    @Qualifier("stations")
    private Set<StationDataSource> stationDataSource;

    public void runSql() throws Exception {
        Set<String> stationIds = new HashSet<>();
        for (StationDataSource stationDataSource : stationDataSource) {
            stationIds.add(stationDataSource.getStationId());
        }
        executeSqlOnStation(stationIds, "sql/run.sql");
    }

    public void runSql(String[] stationIds) throws Exception {
        executeSqlOnStation(Arrays.asList(stationIds), "sql/run.sql");
    }

    public void myRunSql(String sql) throws Exception {
        Set<String> stationIds = new HashSet<>();
        for (StationDataSource stationDataSource : stationDataSource) {
            stationIds.add(stationDataSource.getStationId());
        }
        myExecuteSql(sql, stationIds);
    }

    public void myRunSql(String sql, String[] stationIds) throws Exception {
        myExecuteSql(sql, Arrays.asList(stationIds));
    }

    private void myExecuteSql(String sql, Collection<String> stationIds) throws Exception {
        Set<String> result = new HashSet<String>();
        List<String> myList = new ArrayList<String>();
        List<String> failList = new ArrayList<String>(); // 失败站点
        List<String> successList = new ArrayList<String>(); // 执行成功站点
        Map<String, String> map = new HashMap<String, String>();
        for (String stationId : stationIds) {
            DynamicDataSource.setDataSourceKey(stationId);
            Connection connection = null;
            SqlRunner runner = null;
            try {
                connection = dynamicDataSource.getConnection(); // 获取数据库链接
                runner = new SqlRunner(connection);
                zzzz(stationId, runner);
                //runner.selectOne(sql);
                successList.add(stationId);
            } catch (Exception e) {
                System.out.println("发生异常站点：" + stationId);
                e.printStackTrace();
                if (connection != null) {
                    connection.rollback();
                }
                failList.add(stationId);
            } finally {
                runner.closeConnection();
            }
        }
        System.out.println("没数据站点：" + myList);
        System.out.println("失败站点：" + failList);
        System.out.println("处理成功站点：" + successList);
        for(String stationId : map.keySet()) {
            System.out.println(stationId + "  " + map.get(stationId));
        }
        //System.out.println("有数据站点：" + result.size());
    }
    
    private void zzzz(String stationId, SqlRunner runner) throws SQLException {
        String sql = "INSERT INTO c_daily_shift_record(cuser, ctime, muser, mtime, settlement_date, work_shift_num, begin_shift_time, close_shift_time, daily_shift_status)"
                + " VALUES ('xiaqi', NOW(), 'xiaqi', NOW(), ?, '0', NOW(), NOW(), '2')";
        Date date = new Date();
        while(true) {
            String latestDate = DateUtil.parseDateToStr(date, DateUtil.DATE_FORMAT_YYYY_MM_DD);
            runner.insert(sql, latestDate);
            date = DateUtils.addDays(date, -1);
        }
    }
    
    /**
     * 回滚收货单, 修改库存
     * @param stationId
     * @param runner
     * @throws SQLException
     */
    private void callbackReceiveBill(String stationId, SqlRunner runner) throws SQLException {
        //String sql = "SELECT goods_id,stock_record_count FROM b_goods_stock_record WHERE ctime LIKE '2020-12-21 15:32%'";
        String sql = "SELECT goods_id,stock_record_count FROM b_goods_stock_record WHERE remark='申请退货,冻结库存' AND ctime='2020-12-22 16:50:52'";
        List<Map<String, Object>> list = runner.selectAll(sql);
        for (Map<String, Object> map : list) {
            String goodsId = (String) map.get("GOODS_ID");
            int num = (int) map.get("STOCK_RECORD_COUNT");
            int stock = getStock(runner, goodsId);
            int newStock = stock + num;
            sql = "INSERT INTO b_goods_stock_record(ctime,mtime,cuser,muser,goods_id,goods_count_old,goods_count_now,stock_record_count,stock_record_type,employee_id,remark)\n"
                    + " VALUES (NOW(), NOW(), 'xiaqi', 'xiaqi', ?, ?, ?, ?, ?, 'xiaqi', '回滚退货单')";
            runner.insert(sql, goodsId, stock, newStock, num, "2");
            sql = "UPDATE b_goods_stock SET muser='xiaqi', mtime=NOW(), goods_count=? WHERE goods_id=?";
            runner.update(sql, newStock, goodsId);
        }
    }

    /**
     * 获取库存
     * 
     * @param runner
     * @param goodsId
     * @return
     * @throws SQLException
     */
    private int getStock(SqlRunner runner, String goodsId) throws SQLException {
        String sql = "select goods_count from b_goods_stock WHERE goods_id=?";
        Map<String, Object> map = runner.selectOne(sql, goodsId);
        int num = (int) map.get("GOODS_COUNT");
        return num;
    }

    private String gggg(String stationId, SqlRunner runner) throws SQLException {
        String  sql = "SELECT LEFT(staff_shift_id,8) as staff FROM c_orders WHERE order_status='2' GROUP BY LEFT(staff_shift_id,8) ORDER BY LEFT(staff_shift_id,8) DESC LIMIT 1";
        Map<String, Object>  map = runner.selectOne(sql);
        if(map == null) {
            return null;
        }
        return (String) map.get("STAFF");
    }
    
    /**
     * 检查站点非能切换后还可以改库存的站点
     * @param stationId
     * @param runner
     * @return
     * @throws SQLException
     */
    private boolean mmmm(String stationId, SqlRunner runner) throws SQLException {
        //String sql = "SELECT COUNT(*) AS num FROM b_goods_stock_record WHERE remark='站点非能切换'";
        //Map<String, Object> map = runner.selectOne(sql);
        //long num = (long) map.get("NUM");
        //if(num > 0) {
        String  sql = "SELECT COUNT(*) AS num FROM s_config WHERE varname='is_update_stock' AND defvalue='1'";
            Map<String, Object>  map = runner.selectOne(sql);
            long num = (long) map.get("NUM");
            if(num > 0) {
                return true;
            }
        //}
        return false;
    }
    
    /**
     * 商品变价数据订正
     * 
     * @param stationId
     * @param runner
     * @throws SQLException
     */
    private void priceChange(String stationId, SqlRunner runner) throws SQLException {
        String sql = "UPDATE b_goods_price_change SET muser='xiaqi', mtime=NOW(), original_price=70 WHERE goods_id='6901028223980'";
        runner.update(sql);
    }
    
    /**
     * 输出sql
     * 
     * @param map
     * @throws IOException
     */
    private void printSql(Map<String, List<String>> map) throws IOException {
        File file = new File("C:\\Users\\123456\\Desktop\\goods.sql");
        // %s是字符串类型占位符
        String ss = "INSERT INTO b_goods_station_total(ctime,mtime,cuser,muser,goods_id,goods_state,station_id) VALUES(NOW(),NOW(),'xiaqi','xiaqi','%s','0','%s');";
        for (String stationId : map.keySet()) {
            List<String> list = map.get(stationId);
            if (list.isEmpty()) {
                continue;
            }
            for (String goodsId : list) {
                String mySql = String.format(ss, goodsId, stationId) + "\r\n";
                FileUtils.writeStringToFile(file, mySql, "utf-8", true);
            }
        }
    }

    /**
     * 数据订正全量商品下发
     * 
     * @param runner
     * @throws SQLException
     */
    private List<String> hahaha(String stationId, SqlRunner runner) throws SQLException {
        String sql = "SELECT goods_id FROM b_goods WHERE goods_state=0";
        List<Map<String, Object>> list = runner.selectAll(sql);
        List<String> goodsIdList = new ArrayList<String>();
        for (Map<String, Object> map : list) {
            String goodsId = (String) map.get("GOODS_ID");
            goodsIdList.add(goodsId);
        }
        return goodsIdList;
        // System.out.println("站点 " + stationId + " 下架商品： " + goodsIdList);
    }

    /**
     * 数据订正移动平均成本价
     * 
     * @param runner
     * @param priceMap
     * @throws SQLException
     */
    private void ddd(SqlRunner runner, Map<String, BigDecimal> priceMap) throws SQLException {
        String sql = "SELECT goods_id FROM b_goods_stock_daily_statistics WHERE transfer_avg_price=0 AND statistics_date = (SELECT max(statistics_date) FROM b_goods_stock_daily_statistics)";
        List<Map<String, Object>> list = runner.selectAll(sql);
        sql = "UPDATE b_goods_stock_daily_statistics a, (SELECT max(statistics_date) AS statistics_date FROM b_goods_stock_daily_statistics) b \n"
                + "SET a.muser='xiaqi', a.mtime=NOW(), a.transfer_avg_price=? \n"
                + "WHERE a.transfer_avg_price=0 AND a.statistics_date=b.statistics_date AND a.goods_id=?";
        for (Map<String, Object> map : list) {
            for (Object item : map.values()) {
                String goodsId = (String) item;
                BigDecimal price = priceMap.get(goodsId);
                if (price == null) {
                    continue;
                }
                runner.update(sql, price, goodsId);
            }
        }
    }

    private Map<String, BigDecimal> getPrice() throws Exception {
        File file = new File("C:\\Users\\123456\\Desktop\\111.xlsx");
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        List<GoodsStockRecord> list = ExcelImportUtil.importExcel(new FileInputStream(file), GoodsStockRecord.class, params);
        Map<String, BigDecimal> map = new HashMap<>();
        for (GoodsStockRecord item : list) {
            map.put(item.getGoodsId(), item.getPrice());
        }
        return map;
    }

    /**
     * 站点切换人工变更库存
     * 
     * @param runner
     * @throws SQLException
     */
    private void exe1019(SqlRunner runner) throws SQLException {
        String sql = "INSERT INTO b_goods_stock_record(ctime,mtime,cuser,muser,goods_id,goods_count_old,goods_count_now,stock_record_count,stock_record_type,employee_id,remark)\n"
                + " VALUES (NOW(), NOW(), 'xiaqi', 'xiaqi', ?, ?, ?, ?, ?, 'xiaqi', '站点非能切换')";
        clear(runner, sql);
        sale(runner, sql);
    }

    /**
     * 不能修改库存
     */
    private void updateStockState(SqlRunner runner) throws SQLException {
        String sql = "UPDATE s_config SET mtime=NOW(), defvalue='0' WHERE varname='is_update_stock'";
        runner.update(sql);
    }

    /**
     * 库存清0
     * 
     * @param runner
     * @param sql
     * @throws SQLException
     */
    private void clear(SqlRunner runner, String sql) throws SQLException {
        Map<String, Integer> stockMap = getStock(runner);
        for (String goodsId : stockMap.keySet()) {
            Integer oldCount = stockMap.get(goodsId);
            if (oldCount == null || oldCount == 0) {
                continue;
            }
            runner.insert(sql, goodsId, oldCount, 0, -oldCount, "2");
        }
        sql = "UPDATE b_goods_stock SET muser='xiaqi', mtime=NOW(), goods_count=0 where goods_count<>0";
        runner.update(sql);
    }

    /**
     * 库存扣减销售数据
     * 
     * @param runner
     * @param sql
     * @throws SQLException
     */
    private void sale(SqlRunner runner, String sql) throws SQLException {
        Map<String, Integer> stockMap = getStock(runner);
        Map<String, Integer> orderMap = getOrder(runner);
        String updateSql = "UPDATE b_goods_stock SET muser='xiaqi', mtime=NOW(), goods_count=? WHERE goods_id=?";
        for (String goodsId : stockMap.keySet()) {
            Integer orderCount = orderMap.get(goodsId);
            if (orderCount == null || orderCount == 0) {
                continue;
            }
            Integer oldCount = stockMap.get(goodsId);
            Integer newCount = oldCount - orderCount;
            runner.insert(sql, goodsId, oldCount, newCount, orderCount, "22");
            runner.update(updateSql, newCount, goodsId);
        }
    }

    private Map<String, Integer> getOrder(SqlRunner runner) throws SQLException {
        Map<String, Integer> map = new HashMap<String, Integer>();
        String sql = "SELECT goods_id,SUM(sale_count) AS sale_count FROM c_order_goods WHERE sub_order_status in ('2') GROUP BY goods_id";
        List<Map<String, Object>> list = runner.selectAll(sql);
        for (Map<String, Object> item : list) {
            BigDecimal count = (BigDecimal) item.get("SALE_COUNT");
            map.put((String) item.get("GOODS_ID"), count.intValue());
        }
        return map;
    }

    private Map<String, Integer> getStock(SqlRunner runner) throws SQLException {
        Map<String, Integer> map = new HashMap<String, Integer>();
        String sql = "select * from b_goods_stock";
        List<Map<String, Object>> list = runner.selectAll(sql);
        for (Map<String, Object> item : list) {
            map.put((String) item.get("GOODS_ID"), (Integer) item.get("GOODS_COUNT"));
        }
        return map;
    }

    @Override
    public String runSql(StationSelected stationSelected) throws Exception {
        Set<String> stationIds = getStations(stationSelected);

        if (stationIds == null || stationIds.isEmpty()) {
            System.out.println("所选站点为空");
            return "所选站点为空";
        }

        String sqlFilePath = "sql/run.sql";

        executeSqlOnStation(stationIds, sqlFilePath);

        return "FINISH";
    }

    @Override
    public String init(StationSelected stationSelected) throws Exception {
        Set<String> stationIds = getStations(stationSelected);

        if (stationIds == null || stationIds.isEmpty()) {
            System.out.println("所选站点为空");
            return "所选站点为空";
        }

        String sqlFilePath = "sql/init.sql";

        executeSqlOnStation(stationIds, sqlFilePath);

        return "FINISH";
    }

    private Set<String> getStations(StationSelected stationSelected) {
        Set<String> stationIds;
        String allSelected = stationSelected.getSelected();
        if ("all".equals(allSelected)) {
            stationIds = new LinkedHashSet<>(0);
            for (StationDataSource stationDataSource : stationDataSource) {
                stationIds.add(stationDataSource.getStationId());
            }
        } else {
            stationIds = new LinkedHashSet<>(stationSelected.getStations());
        }
        return stationIds;
    }

    private void executeSqlOnStation(Collection<String> stationIds, String sqlFilePath) throws IOException, SQLException {
        ClassPathResource sqlResource = new ClassPathResource(sqlFilePath);

        String logRootPath = new File("logs").getCanonicalPath();
        File logFile = new File(logRootPath, sqlLogFile);
        PrintWriter writer = new PrintWriter(new FileWriter(logFile), true);
        List<String> failList = new ArrayList<>();
        List<String> successList = new ArrayList<String>(); // 执行成功站点
        for (String stationId : stationIds) {
            if (ExecuteRunSqlResult.getExecuteResult(stationId) != null && ExecuteRunSqlResult.getExecuteResult(stationId)) {
                writer.println("-----------------" + stationId + "之前已执行成功，不再重复执行-----------------");
                writer.println();
                writer.println();
                continue;
            }
            DynamicDataSource.setDataSourceKey(stationId);
            // 获取数据库链接
            Connection connection = null;
            ScriptRunner runner = null;
            try {
                connection = dynamicDataSource.getConnection();
                runner = new ScriptRunner(connection);
                connection.setAutoCommit(false);// 设置不自动提交
                /**
                 * setStopOnError参数作用：遇见错误是否停止；<br>
                 * （1）false，遇见错误不会停止，会继续执行，会打印异常信息，并不会抛出异常，当前方法无法捕捉异常无法进行回滚操作，无法保证在一个事务内执行； <br>
                 * （2）true，遇见错误会停止执行，打印并抛出异常，捕捉异常，并进行回滚，保证在一个事务内执行；
                 */
                runner.setStopOnError(true);
                // 设置是否输出日志，不设置自动将日志输出到控制台.参数为null表示不输出日志，
                runner.setLogWriter(writer);
                runner.setErrorLogWriter(writer);
                // writer.println("-----------------" + stationId + "执行开始-----------------");
                runner.runScript(new InputStreamReader(sqlResource.getInputStream(), "UTF-8"));
                writer.println("-----------------" + stationId + "执行完成-----------------");
                successList.add(stationId);
            } catch (Exception e) {
                System.out.println("-----------------" + stationId + "执行失败-----------------");
                writer.println("-----------------" + stationId + "执行失败-----------------");
                writer.println();
                writer.println();
                if (connection != null) {
                    connection.rollback();
                }
                failList.add("\"" + stationId + "\"");
            } finally {
                // 释放链接，不释放会导致数据库链接一直被占用，后续的请求无法获取
                if (runner != null) {
                    runner.closeConnection();
                }
            }
        }
        System.out.println("不成功站点：" + failList);
        System.out.println("成功站点数量：" + successList.size());
        DynamicDataSource.setDataSourceKey(Constants.DEVELOP_STATION_ID);
        writer.close();
    }
}
