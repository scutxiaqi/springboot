package com.example.producehelper.model.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ExecuteRunSqlResult implements Serializable {
    private static final long serialVersionUID = -2227971786141855341L;

    public static final Map<String, Boolean> result = new HashMap<>(0);

    public static Boolean getExecuteResult(String stationId) {
        return result.get(stationId);
    }

    public static void setExecuteResult(String stationId, Boolean res) {
        result.put(stationId, res);
    }
}
