package shared.util.media;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class ImageHandler {

    public String encode(String path) {
        if (path != null) {
            File image = new File(path);
            if (image.exists()) {
                try {
                    byte[] bytes = Files.readAllBytes(image.toPath());
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

    public Image getImage(String encoded) {
        byte[] data = this.decode(encoded);
        if (data != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            try {
                BufferedImage image = ImageIO.read(bis);
                return convertToFxImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }
        return new ImageView(wr).getImage();
    }
}
