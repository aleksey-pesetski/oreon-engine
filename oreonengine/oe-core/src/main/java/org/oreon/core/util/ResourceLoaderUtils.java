package org.oreon.core.util;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.FileSystems.getFileSystem;
import static java.nio.file.FileSystems.newFileSystem;
import static java.nio.file.Files.isRegularFile;
import static java.util.Collections.emptyMap;
import static org.apache.commons.io.FilenameUtils.getName;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.lwjgl.BufferUtils;
import org.oreon.core.context.BaseContext;

@Log4j2
public final class ResourceLoaderUtils {

  private static final String SEPARATOR = "/";

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
                Integer.toString(BaseContext.getConfig().getShadowMapResolution())
            );

        return load(fileName).replaceFirst("#lib.glsl", vLib);
      }
    } catch (IOException e) {
      log.error("Unable to load lib file [{}]!", fileName, e);
      System.exit(1);
    }

    return "";
  }

  public static ByteBuffer loadImageToByteBuffer(String file, int desired_channels) {
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
    ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, c, desired_channels);
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

  public static String buildPath(final String mainFile, final String path) {
    boolean fromJar = false;
    URL vPathUrl = ResourceLoaderUtils.class.getResource(SEPARATOR + path);

    if (vPathUrl.getProtocol().equals("jar")) {
      fromJar = true;
    }

    String tmpPath = null;
    // if jar, copy directory to temp folder
    if (fromJar) {
      try {
        final Path tempDirectory = Files.createTempDirectory("temp");
        tempDirectory.toFile().deleteOnExit();
        Path tempFile = createTempFile(tempDirectory, SEPARATOR + path + SEPARATOR + mainFile);
        tmpPath = tempFile.toString();

        //loading and creating temporary files for each which is around the main mainFile
        final FileSystem fileSystem = loadFileSystem(vPathUrl.toURI());
        final Path pathToFolder = fileSystem.getPath(SEPARATOR + path);
        try (Stream<Path> stream = Files.walk(pathToFolder, 1)) {
          log.debug("Start walking via {}", pathToFolder.getFileSystem());
          stream
              .filter(p -> isRegularFile(p) && !p.getFileName().toString().equals(mainFile))
              .forEach(p -> createTempFile(tempDirectory, SEPARATOR + path + SEPARATOR + p.getFileName()));
        } catch (IOException e) {
          log.error("Error walking the path {}: {}", pathToFolder, e.getMessage(), e);
        }
      } catch (IOException | URISyntaxException e) {
        log.error(e);
      }
    } else {
      var url = ResourceLoaderUtils.class.getClassLoader().getResource(path + "/" + mainFile);
      tmpPath = URLDecoder.decode(url.getPath(), UTF_8);

      // For Linux need to keep '/' or else the Assimp.aiImportFile(...) call below returns null!
      if (System.getProperty("os.name").contains("Windows")) {
        tmpPath = tmpPath.startsWith(SEPARATOR) ? tmpPath.substring(1) : tmpPath;
      }
    }

    return tmpPath;
  }

  private static Path createTempFile(final Path tempDirectory, final String pathToFile) {
    Path tempFile = null;
    try (final InputStream inputStream = loadResource(pathToFile)) {
      final File file = new File(tempDirectory.toAbsolutePath() + SEPARATOR + getName(pathToFile));
      file.deleteOnExit();

      tempFile = file.toPath();
      Files.copy(inputStream, tempFile);
      return tempFile;
    } catch (Exception e) {
      log.error("Can't create temp file for file by path {}.", pathToFile, e);
    }

    return tempFile;
  }

  private static InputStream loadResource(final String path) {
    return ResourceLoaderUtils.class.getResourceAsStream(path);
  }

  private static FileSystem loadFileSystem(final URI uri) {
    try {
      return getFileSystem(uri);
    } catch (FileSystemNotFoundException e) {
      log.warn("FileSystem doesn't exist for the {}. Creating a new one.", uri);
      try {
        return newFileSystem(uri, emptyMap());
      } catch (IOException ex) {
        log.error("Error at creating new FileSystem for path: {}", uri.getPath(), ex);
        throw new RuntimeException(ex);
      }
    }
  }
}
