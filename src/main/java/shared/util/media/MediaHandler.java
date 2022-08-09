package shared.util.media;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
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
        return "newFile" + LocalDateTime.now();
    }

}
