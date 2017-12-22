package space.swordfish.common.notification.domain;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;

import java.util.UUID;

@Data
@Type("notifications")
public class Notification {

    @Id
    String id = UUID.randomUUID().toString();
    String channel;
    String event;
    String message;

}
