package com.myjbjy.mq;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 构建工作队列模式的消费者，监听消费消息
 */
public class WorkQueuesConsumerA {

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.122");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("imooc");
        factory.setPassword("imooc");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare("work_queue", true, false, false, null);


        Consumer consumer = new DefaultConsumer(channel){
            /**
             * 重写消息配送方法
             * @param consumerTag 消息的标签（标识）
             * @param envelope  信封（一些信息，比如交换机路由等等信息）
             * @param properties 配置信息
             * @param body 收到的消息数据
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) throws IOException {
                System.out.println("body = " + new String(body));
            }
        };
        /**
         * queue: 监听的队列名
         * autoAck: 是否自动确认，true：告知mq消费者已经消费的确认通知
         * callback: 回调函数，处理监听到的消息
         */
        channel.basicConsume("work_queue", true, consumer);

    }

}
