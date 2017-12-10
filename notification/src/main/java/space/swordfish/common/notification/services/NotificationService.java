package space.swordfish.common.notification.services;

public interface NotificationService {

	void send(String channel, String event, String payload);

}
