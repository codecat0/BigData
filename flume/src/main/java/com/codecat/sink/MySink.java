package com.codecat.sink;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MySink extends AbstractSink implements Configurable {
    // 创建Logger对象
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSink.class);

    private String prefix;
    private String suffix;

    @Override
    public void configure(Context context) {
        // 读取配置文件内容，有默认值
        prefix = context.getString("prefix", "hello");

        // 读取配置文件内容，无默认值
        suffix = context.getString("suffix");
    }

    @Override
    public Status process() throws EventDeliveryException {
        // 声明返回值状态信息
        Status status;
        // 获取当前sink绑定的channel
        Channel channel = getChannel();
        // 获取事务
        Transaction transaction = channel.getTransaction();
        // 声明事件
        Event event;
        // 开启事务
        transaction.begin();
        // 读取channel中的事件，直到读取到事件结束循环
        while (true) {
            event = channel.take();
            if (event != null) {
                break;
            }
        }
        try {
            // 处理事件（打印）
            LOG.info(prefix + new String(event.getBody()) + suffix);

            // 事务提交
            transaction.commit();
            status = Status.READY;
        } catch (Exception e) {
            // 遇到异常，事务回滚
            transaction.rollback();
            status = Status.BACKOFF;
        } finally {
            // 关闭事务
            transaction.close();
        }
        return status;
    }
}
