package org.oreon.gl.engine;

import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.oreon.core.gl.framebuffer.GLFrameBufferObject;
import org.oreon.core.gl.texture.GLTexture;
import org.oreon.core.gl.wrapper.texture.TextureImage2D;
import org.oreon.core.image.Image.ImageFormat;
import org.oreon.core.image.Image.SamplerFilter;
import org.oreon.core.image.Image.TextureWrapMode;
import org.oreon.core.target.Attachment;

@Log4j2
public class OffScreenFbo extends GLFrameBufferObject {

  public OffScreenFbo(int width, int height, int samples) {
    super(width, height, 1, samples);
  }

  @Override
  protected Map<Attachment, GLTexture> configureAttachments(int sample) {
    return Map.of(
        //albedoAttachment
        Attachment.COLOR,
        new TextureImage2D(getWidth(), getHeight(), sample, ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest,
            TextureWrapMode.ClampToEdge),
        //worldPositionAttachment
        Attachment.POSITION,
        new TextureImage2D(getWidth(), getHeight(), sample, ImageFormat.RGBA32FLOAT, SamplerFilter.Nearest,
            TextureWrapMode.ClampToEdge),
        //normalAttachment
        Attachment.NORMAL,
        new TextureImage2D(getWidth(), getHeight(), sample, ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest,
            TextureWrapMode.ClampToEdge),
        //specularEmissionDiffuseSsaoBloomAttachment
        Attachment.SPECULAR_EMISSION_DIFFUSE_SSAO_BLOOM,
        new TextureImage2D(getWidth(), getHeight(), sample, ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest,
            TextureWrapMode.ClampToEdge),
        //lightScatteringAttachment
        Attachment.LIGHT_SCATTERING,
        new TextureImage2D(getWidth(), getHeight(), sample, ImageFormat.RGBA16FLOAT, SamplerFilter.Nearest,
            TextureWrapMode.ClampToEdge),
        //depthAttachment
        Attachment.DEPTH,
        new TextureImage2D(getWidth(), getHeight(), sample, ImageFormat.DEPTH32FLOAT, SamplerFilter.Nearest,
            TextureWrapMode.ClampToEdge)
    );
  }

  @Override
  protected Logger getLog() {
    return log;
  }
}
