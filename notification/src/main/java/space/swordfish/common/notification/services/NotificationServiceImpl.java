package space.swordfish.common.notification.services;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import space.swordfish.common.notification.domain.Notification;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private JsonTransformService jsonTransformService;

    @Value("${queues.notificationEvents}")
    private String queue;

    @Override
    public void send(String channel, String event, String payload) {
        Notification notification = new Notification();
        notification.setChannel(channel);
        notification.setEvent(event);
        notification.setMessage(payload);

        Message<String> message = MessageBuilder
                .withPayload(jsonTransformService.write(notification)).build();

        queueMessagingTemplate.send(queue, message);
    }
}
