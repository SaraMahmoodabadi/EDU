package server.network;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;

public class Token {
    private final SecureRandom secureRandom;
    private final Base64.Encoder base64Encoder;
    private final ArrayList<String> tokens;

    public Token() {
        this.secureRandom = new SecureRandom();
        this.base64Encoder = Base64.getUrlEncoder();
        this.tokens = new ArrayList<>();
    }

    public String generateToken() {
        byte[] bytes = new byte[24];
        secureRandom.nextBytes(bytes);
        String token;
        do {
            token = base64Encoder.encodeToString(bytes);
            this.tokens.add(token);
        } while (this.tokens.contains(token));
        return token;
    }

    public void removeToken(String token) {
        this.tokens.remove(token);
    }
}
