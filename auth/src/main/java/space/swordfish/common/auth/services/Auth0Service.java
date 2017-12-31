package space.swordfish.common.auth.services;

import com.auth0.json.mgmt.users.User;

public interface Auth0Service {

    String getUserId(String token);

    String getUserName(String userId);

    String getUserProfilePicture(String userId);

    User getUser(String userId);

    void updateUser(String userId, User data);

}
