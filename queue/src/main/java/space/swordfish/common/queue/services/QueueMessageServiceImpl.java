package space.swordfish.common.queue.services;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class QueueMessageServiceImpl implements QueueMessageService {

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    @Autowired
    private AmazonSQSAsync amazonSQSAsync;

    @Override
    public void send(String queue, String payload) {
        amazonSQSAsync.createQueueAsync(queue,
                new AsyncHandler<CreateQueueRequest, CreateQueueResult>() {
                    @Override
                    public void onError(Exception exception) {

                    }

                    @Override
                    public void onSuccess(CreateQueueRequest request,
                                          CreateQueueResult createQueueResult) {
                        queueMessagingTemplate.send(queue,
                                MessageBuilder.withPayload(payload).build());
                    }
                });
    }
}
