package space.swordfish.common.auth.services;

import org.springframework.http.HttpEntity;
import space.swordfish.common.auth.domain.User;

public interface AuthenticationService {

    HttpEntity<String> addAuthenticationHeader();

    String getCurrentToken();

    User getCurrentUser();

}
