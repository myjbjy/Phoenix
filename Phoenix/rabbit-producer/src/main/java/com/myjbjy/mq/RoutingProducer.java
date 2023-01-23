package com.myjbjy.mq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 构建路由（定向）模式的生产者，发送消息
 */
public class RoutingProducer {

    public static void main(String[] args) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.1.122");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("imooc");
        factory.setPassword("imooc");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 创建交换机 Exchange
        /**
         * exchange: 交换机的名称
         * type: 交换机的类型
         *  FANOUT("fanout"): 广播模式，发布订阅，把消息发送给所有的绑定的队列
         *  DIRECT("direct"): 定向投递模式，把消息发送给指定的“routing key”的队列
         *  TOPIC("topic"): 通配符模式，把消息发送给符合的“routing pattern”的队列
         *  HEADERS("headers"): 使用率不多，参数匹配
         * durable: 是否持久化
         * autoDelete: 是否自动删除
         * internal: 内部意思，true：表示当前exchange是rabbitmq内部使用的，用户创建的队列不会消费该类型交换机下的消息，所以我们一般使用false即可
         * arguments: map类型的参数
         */
        String routing_exchange = "routing_exchange";
        channel.exchangeDeclare(routing_exchange,
                BuiltinExchangeType.DIRECT,
                true, false, false, null);

        // 定义两个队列
        String routing_queue_order = "routing_queue_order";
        String routing_queue_pay = "routing_queue_pay";
        channel.queueDeclare(routing_queue_order, true, false, false, null);
        channel.queueDeclare(routing_queue_pay, true, false, false, null);

        // 绑定交换机和队列
        channel.queueBind(routing_queue_order, routing_exchange, "order_create");
        channel.queueBind(routing_queue_order, routing_exchange, "order_delete");
        channel.queueBind(routing_queue_order, routing_exchange, "order_update");
        channel.queueBind(routing_queue_pay, routing_exchange, "order_pay");

        String msg1 = "创建订单A";
        String msg2 = "创建订单B";
        String msg3 = "删除订单C";
        String msg4 = "修改订单D";
        String msg5 = "支付订单E";
        String msg6 = "支付订单F";
        channel.basicPublish(routing_exchange, "order_create", null, msg1.getBytes());
        channel.basicPublish(routing_exchange, "order_create", null, msg2.getBytes());
        channel.basicPublish(routing_exchange, "order_delete", null, msg3.getBytes());
        channel.basicPublish(routing_exchange, "order_update", null, msg4.getBytes());
        channel.basicPublish(routing_exchange, "order_pay", null, msg5.getBytes());
        channel.basicPublish(routing_exchange, "order_pay", null, msg6.getBytes());

        channel.close();
        connection.close();
    }

}
