package com.myjbjy.mq;

import com.myjbjy.pojo.mq.SMSContentQO;
import com.myjbjy.utils.GsonUtils;
import com.myjbjy.utils.SMSUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.myjbjy.api.mq.RabbitMQSMSConfig;
import org.springframework.amqp.core.Message;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 短信监听消费者
 * @author myj
 */
@Slf4j
@Component
public class RabbitMQSMSConsumer {

    @Resource
    private SMSUtils smsUtils;

    /**
     * 监听队列，并且处理消息
     * @param payload
     * @param message
     */
//    @RabbitListener(queues = {RabbitMQSMSConfig.SMS_QUEUE})
//    public void watchQueue(String payload, Message message) {
//        log.info("payload = " + payload);
//
//        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
//        log.info("routingKey = " + routingKey);
//
//        String msg = payload;
//        log.info("msg = " + msg);
//
//        if (routingKey.equalsIgnoreCase(RabbitMQSMSConfig.ROUTING_KEY_SMS_SEND_LOGIN)) {
////            // 此处为短信发送的消息消费处理
//            SMSContentQO contentQO = GsonUtils.stringToBean(msg, SMSContentQO.class);
//            smsUtils.sendSMS(contentQO.getMobile(), contentQO.getContent());
//        }
//    }

    /**
     *
     * @param message
     * @param channel
     * @throws Exception
     */
    @RabbitListener(queues = {RabbitMQSMSConfig.SMS_QUEUE})
    public void watchQueue(Message message, Channel channel) throws Exception {
        try {
            String routingKey = message.getMessageProperties().getReceivedRoutingKey();
            log.info("routingKey = " + routingKey);

            int a = 1/0;

            String msg = new String(message.getBody());
            log.info("msg = " + msg);

            /**
             * deliveryTag: 消息投递的标签
             * multiple: 批量确认所有消费者获得的消息
             */
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),
                    true);
        } catch (Exception e) {
            e.printStackTrace();
            /**
             * requeue: true：重回队列 false：丢弃消息
             */
            channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                    true,
                    false);
        }

    }
}
