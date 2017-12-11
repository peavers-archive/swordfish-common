package space.swordfish.common.notification.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import space.swordfish.common.notification.services.NotificationService;
import space.swordfish.common.notification.services.NotificationServiceImpl;

@Configuration
public class NotificationConfig {

    @Bean
    public NotificationService notificationService() {
        return new NotificationServiceImpl();
    }

}
