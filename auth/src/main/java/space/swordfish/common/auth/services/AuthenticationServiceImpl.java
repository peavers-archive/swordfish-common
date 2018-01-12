package space.swordfish.common.auth.services;

import com.auth0.spring.security.api.authentication.JwtAuthentication;
import com.github.jasminb.jsonapi.JSONAPIDocument;
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

import java.util.List;


@Slf4j
@Component
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Auth0Service auth0Service;

    private space.swordfish.common.auth.services.JsonTransformService jsonTransformService;

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

    @Override
    public Iterable<User> getLocalUsers() {
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        ResponseEntity<String> exchange = restTemplate.exchange("http://user-service/users/", HttpMethod.GET, addAuthenticationHeader(), reference);

        JSONAPIDocument<List<User>> listJSONAPIDocument = jsonTransformService.readList(User.class, exchange.getBody());

        return listJSONAPIDocument.get();
    }

    @Override
    public User getLocalUserById(String id) {
        ParameterizedTypeReference<String> reference = new ParameterizedTypeReference<String>() {
        };

        ResponseEntity<String> exchange = restTemplate.exchange("http://user-service/users/{id}", HttpMethod.GET, addAuthenticationHeader(), reference, id);

        return jsonTransformService.read(User.class, exchange.getBody());
    }
}
