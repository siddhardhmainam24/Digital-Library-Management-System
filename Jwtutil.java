package learning.outcome.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import learning.outcome.Entity.appUser;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Configuration
public class Jwtutil {
    private String jwtSecret = "mySecretKey123456789012345678901234567890123456789012345678901234567890";

    // Token expiration time (24 hours = 86400000 milliseconds)
    private int jwtExpirationMs = 86400000;

    // Creates the secret key object
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 🏭 MAIN FUNCTION: Generate token for user
    public String generateToken(appUser user) {
        // Create a map to store information in the token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());           // Store user ID
        claims.put("email", user.getEmail());         // Store email
        claims.put("role", user.getUserrole().toString()); // Store user role
        claims.put("firstName", user.getFirstName()); // Store first name
        claims.put("lastName", user.getLastName());   // Store last name

        return createToken(claims, user.getEmail());
    }

    // 🔨 Helper function: Actually create the token
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();                           // Current time
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs); // Current time + 24 hours

        return Jwts.builder()
                .setClaims(claims)              // Add user information
                .setSubject(subject)            // Set the main subject (email)
                .setIssuedAt(now)              // When was this token created
                .setExpiration(expiryDate)      // When does it expire
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign with secret key
                .compact();                     // Convert to string
    }

    // 🔍 Extract username (email) from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 📅 Extract expiration date from token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 🎯 Extract any specific piece of information from token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 📋 Extract all information from token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())  // Use our secret key
                .build()
                .parseClaimsJws(token)           // Parse the token
                .getBody();                      // Get the data
    }

    // ⏰ Check if token is expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ✅ Main validation function: Is this token valid?
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    // 📊 Get expiration time in seconds (for API response)
    public long getExpirationTime() {
        return jwtExpirationMs / 1000;
    }

    // 🆔 Extract user ID from token
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return Long.valueOf(claims.get("userId").toString());
    }

    // 👤 Extract user role from token
    public String extractUserRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role").toString();
    }

}
