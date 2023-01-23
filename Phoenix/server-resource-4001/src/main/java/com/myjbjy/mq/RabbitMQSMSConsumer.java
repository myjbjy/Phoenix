package com.myjbjy.mq;

import com.myjbjy.pojo.mq.SMSContentQO;
import com.myjbjy.utils.GsonUtils;
import com.myjbjy.utils.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.myjbjy.api.mq.RabbitMQSMSConfig;
import org.springframework.amqp.core.Message;

import javax.annotation.Resource;

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
    @RabbitListener(queues = {RabbitMQSMSConfig.SMS_QUEUE})
    public void watchQueue(String payload, Message message) {
        log.info("payload = " + payload);

        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        log.info("routingKey = " + routingKey);

        String msg = payload;
        log.info("msg = " + msg);

        if (routingKey.equalsIgnoreCase(RabbitMQSMSConfig.ROUTING_KEY_SMS_SEND_LOGIN)) {
//            // 此处为短信发送的消息消费处理
            SMSContentQO contentQO = GsonUtils.stringToBean(msg, SMSContentQO.class);
            smsUtils.sendSMS(contentQO.getMobile(), contentQO.getContent());
        }
    }
}
