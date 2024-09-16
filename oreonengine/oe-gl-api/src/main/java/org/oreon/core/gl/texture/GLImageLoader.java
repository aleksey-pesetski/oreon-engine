package org.oreon.core.gl.texture;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.oreon.core.util.ResourceLoaderUtils.loadImageToByteBuffer;

import java.nio.ByteBuffer;
import lombok.extern.log4j.Log4j2;
import org.lwjgl.BufferUtils;
import org.oreon.core.image.ImageMetaData;

@Log4j2
public class GLImageLoader {

  public static ImageMetaData loadImage(String file, int handle) {
    final ByteBuffer imageBuffer = loadImageToByteBuffer(file);

    var w = BufferUtils.createIntBuffer(1);
    var h = BufferUtils.createIntBuffer(1);
    var c = BufferUtils.createIntBuffer(1);

    var image = stbi_load_from_memory(imageBuffer, w, h, c, 0);
    if (image == null) {
      throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
    }

    int width = w.get(0);
    int height = h.get(0);
    int comp = c.get(0);

    glBindTexture(GL_TEXTURE_2D, handle);

    if (comp == 3) {
      if ((width & 3) != 0) {
        glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
      }
      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, image);
    } else {
      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
    }

    stbi_image_free(image);

    return new ImageMetaData(width, height, comp);
  }
}
