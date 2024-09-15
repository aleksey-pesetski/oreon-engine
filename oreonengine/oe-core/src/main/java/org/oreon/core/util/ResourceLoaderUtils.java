package org.oreon.core.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.log4j.Log4j2;
import org.lwjgl.BufferUtils;
import org.oreon.core.context.ContextHolder;

@Log4j2
public final class ResourceLoaderUtils {

  private ResourceLoaderUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static String load(String fileName) {
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

        return load(fileName).replaceFirst("#lib.glsl", vLib);
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

    return imageBuffer;
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
          var source = ResourceLoaderUtils.class.getClassLoader().getResourceAsStream(resource);
          var byteChannel = Channels.newChannel(source)
      ) {
        if (source == null) {
          throw new IOException("Resource '" + resource + "' not found");
        }

        buffer = BufferUtils.createByteBuffer(bufferSize);
        while (true) {
          int bytes = byteChannel.read(buffer);
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
