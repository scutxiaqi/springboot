package com.example.producehelper.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.producehelper.model.StationDataSource;
import com.example.producehelper.service.inf.SSHService;
import com.example.producehelper.util.RemoteUtil;
import com.jcraft.jsch.Session;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SSHServiceImpl implements SSHService {
    @Autowired
    private Set<StationDataSource> stations;
    @Autowired
    private Map<String, StationDataSource> stationMap;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 100, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1));

    private List<String> noSessionList = new CopyOnWriteArrayList<String>();
    private List<String> uploadList = new CopyOnWriteArrayList<String>();
    private List<String> execList = new CopyOnWriteArrayList<String>();
    private List<String> successList = new CopyOnWriteArrayList<String>();
    
    /**
     * 发布bos项目
     * 
     * @param stationId
     * @throws Exception
     */
    public void publishBos(String stationId) {
        StationDataSource station = stationMap.get(stationId);
        Session session = RemoteUtil.getSession(station);
        if(session == null) {
            noSessionList.add(stationId);
            return;
        }
        boolean flag = RemoteUtil.upload(session, "C:\\MyFiles\\work2\\zj-bos\\target\\zj-bos.jar", "/home/bos/app/");
        if(!flag) {
            uploadList.add(stationId);
            return;
        }
        flag = RemoteUtil.exec(session, "bash --login -c 'BosManager restart'");
        if(!flag) {
            execList.add(stationId);
            return;
        }
        successList.add(stationId);
        session.disconnect();
    }

    /**
     * 发布bos项目
     * 
     * @param stationId
     * @throws Exception
     */
    public void publishBos(String[] stationIds){
        CountDownLatch latch = new CountDownLatch(stationIds.length);
        for (String stationId : stationIds) {
            executor.execute(() -> {
                publishBos(stationId);
                latch.countDown();
            });
        }
        executor.execute(() -> {
            try {
                latch.await();
            } catch (InterruptedException e) {
            }
            System.out.println("连接失败的站点：" + noSessionList);
            System.out.println("上传失败的站点：" + uploadList);
            System.out.println("执行失败的站点：" + execList);
            System.out.println("成功站点数量：" + successList.size());
        });
    }
}
