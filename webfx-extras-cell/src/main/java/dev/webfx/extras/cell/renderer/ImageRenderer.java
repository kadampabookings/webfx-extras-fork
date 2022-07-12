package dev.webfx.extras.cell.renderer;

import javafx.scene.image.ImageView;
import dev.webfx.extras.imagestore.ImageStore;
import dev.webfx.platform.util.Strings;

/**
 * @author Bruno Salmon
 */
public final class ImageRenderer implements ValueRenderer {

    public final static ImageRenderer SINGLETON = new ImageRenderer();

    private ImageRenderer() {}

    @Override
    public ImageView renderValue(Object value, ValueRenderingContext context) {
        return ImageStore.createImageView(Strings.toString(value));
    }
}
