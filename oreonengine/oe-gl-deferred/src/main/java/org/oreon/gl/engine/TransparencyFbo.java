package org.oreon.gl.engine;

import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.oreon.core.gl.framebuffer.GLFrameBufferObject;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.target.Attachment;

@Log4j2
public class TransparencyFbo extends GLFrameBufferObject {

  public TransparencyFbo(int width, int height) {
    super(width, height, 1, 0);
  }

  @Override
  protected Map<Attachment, GLTexture> configureAttachments(int sample) {
    return Map.of(
        //albedoAttachment
        Attachment.COLOR, new TextureImage2D(getWidth(), getHeight(), ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest),
        //alphaAttachment
        Attachment.ALPHA, new TextureImage2D(getWidth(), getHeight(), ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest),
        //lightScatteringAttachment
        Attachment.LIGHT_SCATTERING,
        new TextureImage2D(getWidth(), getHeight(), ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest),
        //depthAttachment
        Attachment.DEPTH, new TextureImage2D(getWidth(), getHeight(), ImageFormat.DEPTH32FLOAT, SamplerFilter.Nearest)
    );
  }

  @Override
  protected Logger getLog() {
    return log;
  }
}
