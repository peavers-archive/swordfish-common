package space.swordfish.common.auth.services;

import com.auth0.json.mgmt.users.User;
import org.springframework.http.HttpEntity;

public interface AuthenticationService {

    HttpEntity<String> addAuthenticationHeader();

    String getCurrentAuth0Token();
}
