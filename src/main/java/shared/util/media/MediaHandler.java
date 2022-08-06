package shared.util.media;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Base64;

public class MediaHandler {

    public String encode(String path) {
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                try {
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    return Base64.getEncoder().encodeToString(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public byte[] decode(String encoded) {
        if (encoded != null) {
            return Base64.getDecoder().decode(encoded);
        }
        return null;
    }

    public String getName() {
        byte[] bytes = new byte[24];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);
        Base64.Encoder base64Encoder = Base64.getUrlEncoder();
        return "newFile" + base64Encoder.encodeToString(bytes);
    }

}
