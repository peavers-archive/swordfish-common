package space.swordfish.common.notification.domain;

import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
import lombok.Data;

@Data
@Type("notifications")
public class Notification {

	@Id
	String id;
	String channel;
	String event;
	String message;

}
