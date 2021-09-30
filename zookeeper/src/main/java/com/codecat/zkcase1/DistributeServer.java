package com.codecat.zkcase1;

import org.apache.zookeeper.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DistributeServer {

    private String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zk = null;
    private String parentNode = "/servers/";

    // 创建到zk的客户端连接
    public void getConnect() throws IOException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }

    // 注册服务器
    private void registServer(String hostname) throws KeeperException, InterruptedException {
        String create = zk.create(parentNode + hostname, hostname.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname + " is online " + create);
    }

    // 业务功能
    private void business(String hostname) throws InterruptedException {
        System.out.println(hostname + " is working ...");

        Thread.sleep(Long.MAX_VALUE);
    }

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        // 1. 获取 zk 连接
        DistributeServer server = new DistributeServer();
        server.getConnect();

        // 2. 利用 zk 连接注册服务器信息
        server.registServer(args[0]);

        // 3. 启动业务功能
        server.business(args[0]);
    }
}
