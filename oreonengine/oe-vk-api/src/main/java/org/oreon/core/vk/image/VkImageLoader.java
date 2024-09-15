package org.oreon.core.vk.image;

import static org.lwjgl.stb.STBImage.STBI_rgb_alpha;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.oreon.core.util.ResourceLoaderUtils.loadImageToByteBuffer;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import lombok.extern.log4j.Log4j2;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.oreon.core.image.ImageMetaData;

@Log4j2
public class VkImageLoader {

  public static ImageMetaData getImageMetaData(String file) {
    final ByteBuffer imageBuffer = loadImageToByteBuffer(file);

    var x = BufferUtils.createIntBuffer(1);
    var y = BufferUtils.createIntBuffer(1);
    var channels = BufferUtils.createIntBuffer(1);

    var image = stbi_load_from_memory(imageBuffer, x, y, channels, 0);
    if (image == null) {
      throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
    }

    // Use info to read image metadata without decoding the entire image.
    if (!stbi_info_from_memory(imageBuffer, x, y, channels)) {
      throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
    }

    return new ImageMetaData(x.get(0), y.get(0), channels.get(0));
  }

  public static ByteBuffer decodeImage(String resource) {
    var resourceUrl = VkImageLoader.class.getClassLoader().getResource(resource);

    String absolutePath = resourceUrl.getPath().substring(1);
    if (!System.getProperty("os.name").contains("Windows")) { // TODO Language/region agnostic value for 'Windows' ?
      // stbi_load requires a file system path, NOT a classpath resource path
      absolutePath = File.separator + absolutePath;
    }

    IntBuffer x = BufferUtils.createIntBuffer(1);
    IntBuffer y = BufferUtils.createIntBuffer(1);
    IntBuffer channels = BufferUtils.createIntBuffer(1);

    ByteBuffer image = stbi_load(absolutePath, x, y, channels, STBI_rgb_alpha);
    if (image == null) {
      log.error("Could not decode image file [{}]: [{}]", absolutePath, STBImage.stbi_failure_reason());
    }

    return image;
  }
}
