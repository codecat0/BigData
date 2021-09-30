package com.codecat.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class zkClient {
    private static String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    private static int sessionTimeout = 2000;
    private ZooKeeper zkClient = null;

    // 创建Zookeeper客户端
    @Before
    public void init() throws IOException {
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            // 收到事件通知后的回调函数（用户的业务逻辑）
            public void process(WatchedEvent watchedEvent) {

//                System.out.println(watchedEvent.getType() + "----" + watchedEvent.getPath());
//
//                // 再次启动监听
//                List<String> children = null;
//                try {
//                    children = zkClient.getChildren("/", true);
//                } catch (KeeperException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                for (String child : children) {
//                    System.out.println(child);
//                }

            }
        });
    }

    // 创建子节点
    @Test
    public void create() throws KeeperException, InterruptedException {
        String nodeCreated = zkClient.create("/codecat", "love game".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    // 获取子节点并监听节点变化
    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        List<String> children = zkClient.getChildren("/", true);

        for (String child : children) {
            System.out.println(child);
        }

        // 延时阻塞
        Thread.sleep(Long.MAX_VALUE);
    }

    // 判断Znode是否存在
    @Test
    public void exists() throws KeeperException, InterruptedException {
        Stat stat = zkClient.exists("/codecat", true);
        System.out.println(stat == null ? "/codecat not exist" : "/codecat exist");
    }
}
