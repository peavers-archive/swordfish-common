package space.swordfish.common.auth.services;

public interface Auth0Service {

	String getUserId(String token);

	String getUserName(String userId);

	String getUserProfilePicture(String userId);

}
