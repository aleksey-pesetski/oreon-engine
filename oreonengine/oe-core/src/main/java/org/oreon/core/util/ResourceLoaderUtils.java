package org.oreon.core.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.log4j.Log4j2;
import org.lwjgl.BufferUtils;
import org.oreon.core.context.ContextHolder;

@Log4j2
public class ResourceLoaderUtils {

  private ResourceLoaderUtils() {
  }

  public static String loadShader(String fileName) {
    try (InputStream is = ResourceLoaderUtils.class.getClassLoader().getResourceAsStream(fileName)) {
      if (is == null) {
        throw new IOException("File not found: " + fileName);
      }

      // Using BufferedInputStream for efficient reading
      try (BufferedInputStream bis = new BufferedInputStream(is)) {
        // Read all bytes at once, avoid line-by-line overhead
        return new String(bis.readAllBytes(), UTF_8);
      }
    } catch (IOException e) {
      log.error("Unable to load file [{}]!", fileName, e);
      System.exit(1);
    }

    return ""; // Fallback, though System.exit(1) will terminate the program
  }

  public static String loadShader(String fileName, String lib) {
    try (InputStream is = ResourceLoaderUtils.class.getClassLoader().getResourceAsStream("shader/" + lib)) {
      if (is == null) {
        throw new IOException("File not found: " + fileName);
      }

      // Using BufferedInputStream for efficient reading
      try (BufferedInputStream bis = new BufferedInputStream(is)) {
        // Read all bytes at once, avoid line-by-line overhead
        final String vLib = new String(bis.readAllBytes(), UTF_8)
            .replaceFirst(
                "#var_shadow_map_resolution",
                Integer.toString(ContextHolder.getContext().getConfig().getShadowMapResolution())
            );

        return loadShader(fileName).replaceFirst("#lib.glsl", vLib);
      }
    } catch (IOException e) {
      log.error("Unable to load lib file [{}]!", fileName, e);
      System.exit(1);
    }

    return "";
  }

  public static ByteBuffer loadImageToByteBuffer(String file) {
    ByteBuffer imageBuffer;
    try {
      imageBuffer = ioResourceToByteBuffer(file, 128 * 128);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    IntBuffer w = BufferUtils.createIntBuffer(1);
    IntBuffer h = BufferUtils.createIntBuffer(1);
    IntBuffer c = BufferUtils.createIntBuffer(1);

    // Use info to read image metadata without decoding the entire image.
    if (!stbi_info_from_memory(imageBuffer, w, h, c)) {
      throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
    }

//        System.out.println("Image width: " + w.get(0));
//        System.out.println("Image height: " + h.get(0));
//        System.out.println("Image components: " + c.get(0));
//        System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

    // Decode the image
    ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, c, 0);
    if (image == null) {
      throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
    }

    return image;
  }

  public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {

    ByteBuffer buffer;

    Path path = Paths.get(resource);
    if (Files.isReadable(path)) {
      try (SeekableByteChannel fc = Files.newByteChannel(path)) {
        buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
        while (fc.read(buffer) != -1) {
        }
      }
    } else {
      try (
          InputStream source = ResourceLoaderUtils.class.getClassLoader().getResourceAsStream(resource);
          ReadableByteChannel rbc = Channels.newChannel(source)
      ) {
        buffer = BufferUtils.createByteBuffer(bufferSize);

        while (true) {
          int bytes = rbc.read(buffer);
          if (bytes == -1) {
            break;
          }
          if (buffer.remaining() == 0) {
            buffer = BufferUtil.resizeBuffer(buffer, buffer.capacity() * 2);
          }
        }
      }
    }

    buffer.flip();
    return buffer;
  }

}
