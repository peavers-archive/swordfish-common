package space.swordfish.common.auth.services;

import org.springframework.http.HttpEntity;

public interface AuthenticationService {

	HttpEntity<String> addAuthenticationHeader();

	String getCurrentToken();

}
