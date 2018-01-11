package space.swordfish.common.auth0.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenInput {

	@JsonProperty("grant_type")
	String grantType;

	@JsonProperty("client_id")
	String clientId;

	@JsonProperty("client_secret")
	String clientSecret;

	@JsonProperty("audience")
	String audience;
}
