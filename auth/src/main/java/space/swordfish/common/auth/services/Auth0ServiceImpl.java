package space.swordfish.common.auth.services;

import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.APIException;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.net.Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import space.swordfish.common.auth.domain.TokenInput;
import space.swordfish.common.auth.domain.TokenResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.AccessDeniedException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class Auth0ServiceImpl implements Auth0Service {

    @Value("${auth0.jasypt.seed}")
    private String seed;

    @Value("${auth0.issuer}")
    private String domain;

    @Value("${auth0.management.clientId}")
    private String clientId;

    @Value("${auth0.management.clientSecret}")
    private String clientSecret;

    @Value("${auth0.management.audience}")
    private String audience;

    @Value("${auth0.management.grantType}")
    private String grantType;

    /**
     * Gets a valid user ID from auth0 based on a JWT auth token.
     *
     * @param token String JWT Token to get the user ID from
     * @return String representing the UserID.
     */
    @Override
    public String getUserIdFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);

        return jwt.getSubject();
    }

    /**
     * Gets the users name from auth0
     *
     * @param userId String valid user ID.
     * @return String name of the user
     */
    @Override
    public String getUserName(String userId) {
        if (userId.equals("unknown")) {
            return null;
        }

        User user = getUser(userId);

        return user != null ? user.getName() : null;
    }

    /**
     * Gets the users avatar/picture from auth0
     *
     * @param userId String valid user ID.
     * @return String URL of the avatar/picture
     */
    @Override
    public String getUserProfilePicture(String userId) {
        if (userId.equals("unknown")) {
            return null;
        }

        User user = getUser(userId);

        return user != null ? user.getPicture() : null;
    }

    /**
     * Valid user data should be passed in, only containing information that you want to
     * update. Works like a dirty PATCH method, this is how the auth0 SDK is written. Not
     * ideal. Again don't expose this directly but rather use as a wrapper.
     *
     * @param userId String valid user ID.
     * @param data   User data to update
     */
    @Override
    public void updateUser(String userId, User data) {
        ManagementAPI managementAPI = getManagementAPI();
        Request request = managementAPI.users().update(userId, data);

        try {
            request.execute();
        } catch (APIException exception) {
            log.error("APIException {}", exception);
        } catch (Auth0Exception exception) {
            log.error("Auth0Exception {}", exception);
        }
    }


    /**
     * Private helper method to get the User object from auth0.
     *
     * @param userId String valid user ID.
     * @return User
     */
    @Override
    public User getUser(String userId) {
        ManagementAPI managementAPI = getManagementAPI();
        if (userId.equals("unknown")) {
            return null;
        }

        Request<User> request = managementAPI.users().get(userId, null);

        try {
            return request.execute();
        } catch (APIException exception) {
            log.error("APIException {}", exception);
        } catch (Auth0Exception exception) {
            log.error("Auth0Exception {}", exception);
        }

        return null;
    }

    /**
     * Get a single object from the user meta data object. Decrypts values as they
     * come out.
     *
     * @param key String name of the key to get data from
     * @return String
     */
    @Override
    public String getEncryptedUserMetaByKey(String userId, String key) {
        User user = getUser(userId);

        String value = (String) user.getUserMetadata().get(key);

        return decrypt(value);
    }

    /**
     * Preforms basic encryption using a seed before sending data to Auth0
     *
     * @param userId Current user
     * @param data   Map of key/values to push to the user data
     */
    @Override
    public void setEncryptedUserMetaData(String userId, Map<String, Object> data) {
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            entry.setValue(encrypt((String) entry.getValue()));
        }

        com.auth0.json.mgmt.users.User auth0User = new com.auth0.json.mgmt.users.User(null);
        auth0User.setUserMetadata(data);

        updateUser(userId, auth0User);
    }

    /**
     * Return standard user meta data
     *
     * @param userId
     * @param key
     * @return
     */
    @Override
    public String getUserMetaByKey(String userId, String key) {
        User user = getUser(userId);

        return (String) user.getUserMetadata().get(key);
    }

    /**
     * Set meta data without encrypting it
     *
     * @param userId
     * @param data
     */
    @Override
    public void setUserMetaData(String userId, Map<String, Object> data) {
        com.auth0.json.mgmt.users.User auth0User = new com.auth0.json.mgmt.users.User(null);
        auth0User.setUserMetadata(data);

        updateUser(userId, auth0User);
    }

    /**
     * Create a fresh auth0 management token
     *
     * @return String management token
     */
    private String getManagementToken() {
        TokenInput tokenInput = TokenInput.builder().grantType(grantType)
                .audience(audience).clientId(clientId).clientSecret(clientSecret).build();

        ObjectMapper mapper = new ObjectMapper();

        try {
            String token = mapper.writeValueAsString(tokenInput);

            log.info(token);

            HttpResponse<String> response = Unirest.post(domain + "oauth/token")
                    .header("content-type", "application/json").body(token).asString();

            if (response.getStatus() != 200) {
                throw new AccessDeniedException(response.getBody());
            }

            return mapper.readValue(response.getBody(), TokenResponse.class)
                    .getAccessToken();

        } catch (UnirestException | IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * The actually auth0 entry point into the management API. Will create it's own token
     * as required.
     *
     * @return ManagementAPI
     */
    private ManagementAPI getManagementAPI() {
        return new ManagementAPI(domain, Objects.requireNonNull(getManagementToken()));
    }

    /**
     * Encrypts a string
     *
     * @param data String
     * @return String
     */
    private String encrypt(String data) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPasswordCharArray(seed.toCharArray());

        return textEncryptor.encrypt(data);
    }

    /**
     * Returns a decrypted string
     *
     * @param privateData String
     * @return String
     */
    private String decrypt(String privateData) {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
        textEncryptor.setPasswordCharArray(seed.toCharArray());

        return textEncryptor.decrypt(privateData);
    }

}
