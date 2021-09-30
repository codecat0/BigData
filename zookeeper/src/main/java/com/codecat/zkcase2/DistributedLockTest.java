package com.codecat.zkcase2;

import org.apache.zookeeper.KeeperException;

import java.io.IOException;

public class DistributedLockTest {
    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        final DistributedLock lock1 = new DistributedLock();
        final DistributedLock lock2 = new DistributedLock();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.zkLock();
                    System.out.println("线程1 获取锁");
                    Thread.sleep(5 * 1000);

                    lock1.zkUnLock();
                    System.out.println("线程1 释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock2.zkLock();
                    System.out.println("线程2 获取锁");
                    Thread.sleep(5 * 1000);

                    lock2.zkUnLock();
                    System.out.println("线程2 释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
