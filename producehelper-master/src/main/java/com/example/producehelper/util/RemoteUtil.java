package com.example.producehelper.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.example.producehelper.model.StationDataSource;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteUtil {
    private final static int CONNECT_TIMEOUT = 1000 * 60 * 10;

    public static Session getSession(StationDataSource station) {
        JSch jsch = new JSch(); // 创建JSch对象
        Session session;
        try {
            session = jsch.getSession(station.getUserName(), station.getStationIp());// 根据用户名，主机ip，默认端口22获取一个Session对象
            session.setPassword(station.getSshPass());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setTimeout(CONNECT_TIMEOUT); // 设置timeout时间
            session.connect(); // 通过Session建立链接
            if (session.isConnected()) {
                //log.info("Session connected.host=({})", station.getStationIp());
                return session;
            }
        } catch (JSchException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 远程执行
     * 
     * @param session
     * @param command 命令或者脚本
     * @return
     * @throws JSchException
     */
    public static boolean exec(Session session, String command) {
        //log.info(">> {}", command);
        ChannelExec channel = null;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            InputStream in = channel.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            channel.connect();
            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                //log.info("   {}", inputLine);
            }
            reader.close();
            in.close();
        } catch (Exception e) {
            return false;
        } finally {
            if (channel != null) {
                try {
                    channel.disconnect();
                } catch (Exception e) {
                    log.error("JSch channel disconnect error:", e);
                }
            }
        }
        return true;
    }

    /**
     * 上传文件
     * 
     * @param session
     * @param file    要上传的文件
     * @param dst     目标文件名为dst，若dst为目录，则目标文件名将与file文件名相同
     * @throws Exception
     */
    public static boolean upload(Session session, String file, String dst) {
        String oldpath = dst + "zj-bos0.jar";
        String newpath = dst + "zj-bos.jar";
        ChannelSftp sftp = null;
        try {
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect();
            // 以默认文件传输模式OVERWRITE（完全覆盖模式）上传，即如果目标文件已经存在，传输的文件将完全覆盖目标文件，产生新的文件。
            sftp.put(file, oldpath, new FileProgressMonitor(new File(file).length()));
            //log.info("sftp upload file success!");
            sftp.rename(oldpath, newpath);
        } catch (SftpException | JSchException e) {
            return false;
        } finally {
            if (sftp != null) {
                sftp.quit();
                sftp.disconnect();
            }
        }
        return true;
    }
}
