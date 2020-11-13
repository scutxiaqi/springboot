package com.example.producehelper.service.inf;

public interface SSHService {
    /**
     * 发布bos项目
     * 
     * @param stationId
     * @throws Exception
     */
    public void publishBos(String stationId);

    public void publishBos(String[] stationIds);
}
