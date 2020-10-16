package com.example.producehelper.service.impl;

import java.io.File;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.producehelper.dataSource.DynamicDataSource;
import com.example.producehelper.model.StationDataSource;
import com.example.producehelper.model.StationSelected;
import com.example.producehelper.model.common.Constants;
import com.example.producehelper.model.common.ExecuteRunSqlResult;
import com.example.producehelper.service.inf.IExecuteSQLService;

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
    
    public void runSql(String sql)  throws Exception{
        Set<String> stationIds = new HashSet<>();
        for (StationDataSource stationDataSource : stationDataSource) {
            stationIds.add(stationDataSource.getStationId());
        }
        executeSql(sql, stationIds);
    }

    public void runSql(String sql, String[] stationIds) throws Exception{
        executeSql(sql, Arrays.asList(stationIds));
    }
    
    private void executeSql(String sql, Collection<String> stationIds) throws IOException, SQLException {
        Set<String> result = new HashSet<String>();
        List<String> failList = new ArrayList<String>();
        for (String item : stationIds) {
            DynamicDataSource.setDataSourceKey(item);
            // 获取数据库链接
            Connection connection = null;
            SqlRunner runner = null;
            try {
                connection = dynamicDataSource.getConnection();
                runner = new SqlRunner(connection);
                Map<String, Integer> stockMap = getStock(runner);
                Map<String, Integer> orderMap = getOrder(runner);
                String updateSql = "UPDATE b_goods_stock SET muser='xiaqi', mtime=NOW(), goods_count=? WHERE goods_id=?";
                for (String goodsId : stockMap.keySet()) {
                    Integer orderCount = orderMap.get(goodsId);
                    if (orderCount == null) {
                        continue;
                    }
                    Integer oldCount = stockMap.get(goodsId);
                    Integer newCount = oldCount - orderCount;
                    runner.update(sql, goodsId, oldCount, newCount, orderCount);
                    runner.update(updateSql, newCount, goodsId);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (connection != null) {
                    connection.rollback();
                }
            } finally {
                runner.closeConnection();
            }
        }
        System.out.println(failList);
        System.out.println(result);
    }
    
    private Map<String, Integer> getOrder(SqlRunner runner) throws SQLException {
        Map<String, Integer> map = new HashMap<String, Integer>();
        String sql = "SELECT goods_id,SUM(sale_count) AS sale_count FROM c_order_goods WHERE sub_order_status in ('1','2') GROUP BY goods_id";
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
                ExecuteRunSqlResult.setExecuteResult(stationId, true);
            } catch (Exception e) {
                System.out.println("-----------------" + stationId + "执行失败-----------------");
                writer.println("-----------------" + stationId + "执行失败-----------------");
                writer.println();
                writer.println();
                if (connection != null) {
                    connection.rollback();
                }
                ExecuteRunSqlResult.setExecuteResult(stationId, false);
            } finally {
                // 释放链接，不释放会导致数据库链接一直被占用，后续的请求无法获取
                if (runner != null) {
                    runner.closeConnection();
                }
            }
        }
        DynamicDataSource.setDataSourceKey(Constants.DEVELOP_STATION_ID);
        writer.close();
    }
}
