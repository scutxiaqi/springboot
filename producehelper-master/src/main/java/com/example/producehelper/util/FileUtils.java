package com.example.producehelper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import com.example.producehelper.model.StationDataSource;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;

public class FileUtils {
    /**
     * 读取excel文件
     * 
     * @return
     */
    public static List<StationDataSource> readFromExcel(File file, Class entityClass) {
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);

        List<StationDataSource> stationDataSourceList;
        try (InputStream in = new FileInputStream(file)) {
            stationDataSourceList = ExcelImportUtil.importExcel(in, entityClass, params);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return stationDataSourceList;
    }
}
