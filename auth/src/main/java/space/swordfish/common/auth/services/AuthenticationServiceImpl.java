package space.swordfish.common.auth.services;

import com.auth0.json.mgmt.users.User;
import com.auth0.spring.security.api.authentication.JwtAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@Component
public class AuthenticationServiceImpl implements AuthenticationService {

    @Override
    public HttpEntity<String> addAuthenticationHeader() {
        String token = getCurrentAuth0Token();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        return new HttpEntity<String>("parameters", headers);
    }

    @Override
    public String getCurrentAuth0Token() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication instanceof JwtAuthentication) {

            log.info("Current Token {}", ((JwtAuthentication) authentication).getToken());

            return ((JwtAuthentication) authentication).getToken();
        }

        return null;
    }
}
