package fr.cytech.projetdevwebbackend.users;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response object for JWT authentication containing the access token and token
 * type.
 * <p>
 * This class is typically returned from authentication endpoints when a user
 * successfully logs in. The client application can extract the token and use it
 * in subsequent requests in the Authorization header.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@Getter
@Setter
@NoArgsConstructor
public class JwtAuthResponse {
    /**
     * The JWT access token used for authentication.
     */
    private String accessToken;

    /**
     * The type of token, used in the Authorization header.
     * Default is "Bearer".
     */
    private String tokenType = BEARER_TOKEN_TYPE;

    /**
     * Constant for the Bearer token type.
     */
    public static final String BEARER_TOKEN_TYPE = "Bearer";

    /**
     * Constructor with access token only.
     * Sets the token type to the default "Bearer".
     *
     * @param accessToken JWT access token
     */
    public JwtAuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Constructor with both access token and token type.
     *
     * @param accessToken JWT access token
     * @param tokenType   Type of token (e.g., "Bearer")
     */
    public JwtAuthResponse(String accessToken, String tokenType) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
    }

    /**
     * Creates a new JWT authentication response with the default Bearer token type.
     *
     * @param accessToken JWT access token
     * @return A new JwtAuthResponse instance
     */
    public static JwtAuthResponse withToken(String accessToken) {
        return new JwtAuthResponse(accessToken);
    }

    /**
     * Returns the formatted authorization header value.
     *
     * @return String in the format "{tokenType} {accessToken}"
     */
    public String getAuthorizationHeaderValue() {
        return tokenType + " " + accessToken;
    }
}
