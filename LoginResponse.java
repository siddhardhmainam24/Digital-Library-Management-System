package learning.outcome.DTO;

public class LoginResponse {
    private String token;           // ← The JWT token (our "wristband")
    private String tokenType = "Bearer";  // ← Standard way to say "this is a Bearer token"
    private UserResponse user;      // ← User's basic info (name, email, etc.)
    private long expiresIn;        // ← How long the token is valid (in seconds)

    public LoginResponse() {}

    public LoginResponse(String token, UserResponse user, long expiresIn) {
        this.token = token;
        this.user = user;
        this.expiresIn = expiresIn;
    }

    // Getters and setters...
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public UserResponse getUser() { return user; }
    public void setUser(UserResponse user) { this.user = user; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
}
