package space.swordfish.common.auth.services;

import com.auth0.spring.security.api.authentication.JwtAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import space.swordfish.common.auth.domain.User;
import space.swordfish.common.auth0.services.Auth0Service;
import space.swordfish.common.json.services.JsonTransformService;


@Slf4j
@Component
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private JsonTransformService jsonTransformService;

    @Autowired
    private Auth0Service auth0Service;

    @Override
    public HttpEntity<String> addAuthenticationHeader() {
        String token = getCurrentToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        return new HttpEntity<String>("parameters", headers);
    }

    @Override
    public String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();
        if (authentication instanceof JwtAuthentication) {

            log.info("Current Token {}", ((JwtAuthentication) authentication).getToken());

            return ((JwtAuthentication) authentication).getToken();
        }

        return null;
    }

    @Override
    public User getCurrentUser() {
        String userId = auth0Service.getUserIdFromToken(getCurrentToken());
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        ResponseEntity<String> exchange = restTemplate.exchange("http://user-service/users/{id}", HttpMethod.GET, addAuthenticationHeader(), reference, userId);

        return jsonTransformService.read(User.class, exchange.getBody());
    }
}
