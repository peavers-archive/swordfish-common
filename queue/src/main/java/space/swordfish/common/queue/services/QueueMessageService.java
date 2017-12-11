package space.swordfish.common.queue.services;

public interface QueueMessageService {

	void send(String queue, String payload);
}
