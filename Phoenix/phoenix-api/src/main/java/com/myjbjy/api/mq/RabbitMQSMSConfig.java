package com.myjbjy.api.mq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author myj
 */
@Configuration
public class RabbitMQSMSConfig {

    // 定义交换机的名称
    public static final String SMS_EXCHANGE = "sms_exchange";

    // 定义队列的名称
    public static final String SMS_QUEUE = "sms_queue";

    // 统一定义路由key
    public static final String ROUTING_KEY_SMS_SEND_LOGIN = "imooc.sms.send.login";

    // 创建交换机
    @Bean(SMS_EXCHANGE)
    public Exchange exchange() {
        return ExchangeBuilder
                .topicExchange(SMS_EXCHANGE)
                .durable(true)
                .build();
    }

    // 创建队列
    @Bean(SMS_QUEUE)
    public Queue queue() {
//        return new Queue(SMS_QUEUE);
        return QueueBuilder
                .durable(SMS_QUEUE)
                .build();
    }

    @Bean
    public Binding smsBinding(@Qualifier(SMS_EXCHANGE) Exchange exchange,
                              @Qualifier(SMS_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("imooc.sms.#")
                .noargs();
    }
}
