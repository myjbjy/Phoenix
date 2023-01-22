package com.myjbjy.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 构建工作队列的生产者，发送消息
 * @author myj
 */
public class WorkQueuesProducer {

    public static void main(String[] args) throws Exception {

        // 1. 创建连接工厂以及相关的参数配置
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.122");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("imooc");
        factory.setPassword("imooc");

        // 2. 通过工程创建连接 Connection
        Connection connection = factory.newConnection();

        // 3. 创建管道 Channel
        Channel channel = connection.createChannel();

        // 4. 创建队列 Queue（简单模式不需要交换机Exchange）
        /**
         * queue: 队列名
         * durable: 是否持久化，true：重启之后，队列依然存在，false则不存在
         * exclusive: 是否独占，true：只能有一个消费者监听这个队列，一般设置为false
         * autoDelete: 是否自动删除，true：当没有消费者的时候，则自动删除这个队列
         * arguments: map类型的其他参数
         */
        channel.queueDeclare("work_queue", true, false, false, null);

        // 5. 向队列发送消息
        /**
         * exchange: 交换机的名称，简单模式下没有，所以直接设置为 ""
         * routingKey: 路由key，映射路径，如果交换机没有，则路由key和队列名保持一致
         * props: 配置参数
         * body: 消息数据
         */
        for (int i = 0 ; i < 10 ; i ++) {
            String task = "开始上班，搬砖喽~ 开启任务[" + i + "]";
            channel.basicPublish("", "work_queue", null, task.getBytes());
        }

        // 6. 释放资源
        channel.close();
        connection.close();
    }

}
