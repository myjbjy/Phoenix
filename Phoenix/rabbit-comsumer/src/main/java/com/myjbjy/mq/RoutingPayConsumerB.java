package com.myjbjy.mq;

import com.rabbitmq.client.*;

import java.io.IOException;

/**
 * 构建路由模式的消费者，监听消费消息
 * @author myj
 */
public class RoutingPayConsumerB {

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.122");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("imooc");
        factory.setPassword("imooc");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String routing_queue_pay = "routing_queue_pay";
        channel.queueDeclare(routing_queue_pay, true, false, false, null);


        Consumer consumer = new DefaultConsumer(channel){
            /**
             * 重写消息配送方法
             * @param consumerTag 消息的标签（标识）
             * @param envelope  信封（一些信息，比如交换机路由等等信息）
             * @param properties 配置信息
             * @param body 收到的消息数据
             */
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body) {
                System.out.println("body = " + new String(body));
            }
        };
        /**
         * queue: 监听的队列名
         * autoAck: 是否自动确认，true：告知mq消费者已经消费的确认通知
         * callback: 回调函数，处理监听到的消息
         */
        channel.basicConsume(routing_queue_pay, true, consumer);

    }

}
