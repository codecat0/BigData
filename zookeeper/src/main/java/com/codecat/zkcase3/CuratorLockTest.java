package com.codecat.zkcase3;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class CuratorLockTest {
    private String rootNode = "/locks";
    private String connectString = "hadoop102:2181,hadoop103:2181,hadoop104:2181";
    private int connectionTimeout = 2000;
    private int sessionTimeout = 2000;

    public static void main(String[] args) {
        new CuratorLockTest().test();
    }

    private void test() {
        final InterProcessMutex lock1 = new InterProcessMutex(getCuratoFramework(), rootNode);
        final InterProcessMutex lock2 = new InterProcessMutex(getCuratoFramework(), rootNode);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    lock1.acquire();
                    System.out.println("线程1 获取锁");
                    lock1.acquire();
                    System.out.println("线程1 再次获取锁");

                    Thread.sleep(5 * 1000);

                    lock1.release();
                    System.out.println("线程1 释放锁");
                    lock1.release();
                    System.out.println("线程1 再次释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    lock2.acquire();
                    System.out.println("线程2 获取锁");
                    lock2.acquire();
                    System.out.println("线程2 再次获取锁");

                    Thread.sleep(5 * 1000);

                    lock2.release();
                    System.out.println("线程2 释放锁");
                    lock2.release();
                    System.out.println("线程2 再次释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private CuratorFramework getCuratoFramework() {
        RetryPolicy policy = new ExponentialBackoffRetry(3000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .connectionTimeoutMs(connectionTimeout)
                .sessionTimeoutMs(sessionTimeout)
                .retryPolicy(policy).build();

        client.start();
        System.out.println("zookeeper 初始化完成...");
        return client;
    }
}
