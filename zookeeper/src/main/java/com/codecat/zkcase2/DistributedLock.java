package com.codecat.zkcase2;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DistributedLock {

    private String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    private int sessionTimeout = 2000;
    private ZooKeeper zk = null;

    private String rootNode = "locks";
    private String subNode = "seq-";

    private String waitPath;

    private CountDownLatch connectLatch = new CountDownLatch(1);
    private CountDownLatch waitLatch = new CountDownLatch(1);
    private String currentNode;

    public DistributedLock() throws IOException, InterruptedException, KeeperException {
        zk = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    connectLatch.countDown();
                }

                if (watchedEvent.getType() == Event.EventType.NodeDeleted && watchedEvent.getPath().equals(waitPath)){
                    waitLatch.countDown();
                }
            }
        });

        connectLatch.await();

        Stat stat = zk.exists("/" + rootNode, false);

        if (stat == null) {
            System.out.println("根节点不存在");
            zk.create("/" + rootNode, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void zkLock() {
        try {
            currentNode = zk.create("/" + rootNode + "/" + subNode, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

            Thread.sleep(10);

            List<String> childrenNodes = zk.getChildren("/" + rootNode, false);

            if (childrenNodes.size() == 1) {
                return;
            } else {
                Collections.sort(childrenNodes);

                String thisNode = currentNode.substring(("/" + rootNode + "/").length());

                int index = childrenNodes.indexOf(thisNode);

                if (index == -1) {
                    System.out.println("数据异常");
                } else if (index == 0) {
                    return;
                } else {
                    waitPath = "/" + rootNode + "/" +childrenNodes.get(index - 1);

                    zk.getData(waitPath, true, null);

                    waitLatch.await();

                    return;
                }
            }

        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void zkUnLock() {
        try {
            zk.delete(currentNode, -1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

}
