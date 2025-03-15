package fr.cytech.projetdevwebbackend.users;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import fr.cytech.projetdevwebbackend.errors.types.TokenError;
import fr.cytech.projetdevwebbackend.util.Either;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Provider for JWT token generation, validation, and parsing.
 * <p>
 * This component handles all JWT token operations and encapsulates
 * the security mechanisms for token creation and verification.
 *
 * @author fleefie
 * @since 2025-03-15
 */
@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long tokenValidityMillis;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    /**
     * Creates a JWT token provider with the specified secret and expiration
     * duration.
     *
     * @param jwtSecret           Base64-encoded JWT secret key from application
     *                            properties
     * @param tokenValidityMillis Token validity period in milliseconds
     */
    public JwtTokenProvider(
            @Value("${app.jwt-secret}") String jwtSecret,
            @Value("${app.jwt-expiration-milliseconds}") long tokenValidityMillis) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.tokenValidityMillis = tokenValidityMillis;
    }

    /**
     * Generates a JWT token for an authenticated user.
     *
     * @param authentication The authentication object containing user details
     * @return A signed JWT token string
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + tokenValidityMillis);

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extracts the username from a JWT token.
     *
     * @param token JWT token string
     * @return The username (subject) from the token
     * @throws JwtException If the token is invalid or expired
     */
    public String extractUsername(String token) {
        return parseToken(token).map(Claims::getSubject)
                .getRight();
    }

    /**
     * Validates a JWT token's signature and expiration.
     *
     * @param token JWT token string
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        return parseToken(token).isRight();
    }

    /**
     * Parses and validates a JWT token.
     *
     * @param token JWT token string
     * @return Either containing error or claims
     */
    public Either<TokenError, Claims> parseToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Either.right(claims);
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            return Either.left(TokenError.EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            logger.warn("Invalid JWT token: {}", e.getMessage());
            return Either.left(TokenError.MALFORMED_TOKEN);
        } catch (UnsupportedJwtException e) {
            logger.warn("Unsupported JWT token: {}", e.getMessage());
            return Either.left(TokenError.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            logger.warn("JWT claims string is empty: {}", e.getMessage());
            return Either.left(TokenError.EMPTY_CLAIMS);
        } catch (Exception e) {
            logger.error("JWT token validation error: {}", e.getMessage());
            return Either.left(TokenError.GENERIC_TOKEN_ERROR);
        }
    }

    /**
     * Retrieves the token's expiration date.
     *
     * @param token JWT token string
     * @return Either containing error or expiration date
     */
    public Either<TokenError, Date> getExpirationDate(String token) {
        return parseToken(token).map(Claims::getExpiration);
    }

    /**
     * Checks if a token has expired.
     *
     * @param token JWT token string
     * @return Either containing error or boolean indicating if token is expired
     */
    public Either<TokenError, Boolean> isTokenExpired(String token) {
        return getExpirationDate(token).map(expiration -> expiration.before(new Date()));
    }
}
