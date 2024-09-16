package org.oreon.common.water;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.oreon.core.math.Vec2f;
import org.oreon.core.math.Vec3f;

@Log4j2
public class WaterConfig {

  private static final String FFT_RESOLUTION = "fft.resolution";
  private static final String FFT_L = "fft.L";
  private static final String FFT_AMPLITUDE = "fft.amplitude";
  private static final String WIND_X = "wind.x";
  private static final String WIND_Y = "wind.y";
  private static final String WIND_SPEED = "wind.speed";
  private static final String ALIGNMENT = "alignment";
  private static final String FFT_CAPILLARWAVES_SUPPRESSION = "fft.capillarwavesSuppression";
  private static final String DISPLACEMENT_SCALE = "displacementScale";
  private static final String CHOPPINESS = "choppiness";
  private static final String DISTORTION_DELTA = "distortion_delta";
  private static final String WAVEMOTION = "wavemotion";
  private static final String UV_SCALE = "uvScale";
  private static final String TESSELLATION_FACTOR = "tessellationFactor";
  private static final String TESSELLATION_SLOPE = "tessellationSlope";
  private static final String TESSELLATION_SHIFT = "tessellationShift";
  private static final String SPECULAR_FACTOR = "specular.factor";
  private static final String SPECULAR_AMPLIFIER = "specular.amplifier";
  private static final String EMISSION_FACTOR = "emission.factor";
  private static final String K_REFLECTION = "kReflection";
  private static final String K_REFRACTION = "kRefraction";
  private static final String NORMAL_STRENGTH = "normalStrength";
  private static final String HIGH_DETAIL_RANGE = "highDetailRange";
  private static final String T_DELTA = "t_delta";
  private static final String CHOPPY = "choppy";
  private static final String FRESNEL_FACTOR = "fresnel.factor";
  private static final String REFLECTION_BLENDFACTOR = "reflection.blendfactor";
  private static final String WATER_BASECOLOR_X = "water.basecolor.x";
  private static final String WATER_BASECOLOR_Y = "water.basecolor.y";
  private static final String WATER_BASECOLOR_Z = "water.basecolor.z";
  private static final String CAPILLAR_STRENGTH = "capillar.strength";
  private static final String CAPILLAR_DOWNSAMPLING = "capillar.downsampling";
  private static final String DUDV_DOWNSAMPLING = "dudv.downsampling";
  private static final String UNDERWATER_BLURFACTOR = "underwater.blurfactor";
  private static final String DIFFUSE_ENABLE = "diffuse.enable";

  private final PropertiesConfiguration config;

  private WaterConfig(final String file) {
    try {
      config = readPropertiesConfiguration(file);
    } catch (ConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  public static WaterConfig load(final String file) throws RuntimeException {
    return new WaterConfig(file);
  }

  public int getN() {
    return config.getInt(FFT_RESOLUTION);
  }

  public int getL() {
    return config.getInt(FFT_L);
  }

  public float getAmplitude() {
    return config.getFloat(FFT_AMPLITUDE);
  }

  public Vec2f getWindDirection() {
    return new Vec2f(
        config.getFloat(WIND_X),
        config.getFloat(WIND_Y)).normalize();
  }

  public float getWindSpeed() {
    return config.getFloat(WIND_SPEED);
  }

  public float getAlignment() {
    return config.getFloat(ALIGNMENT);
  }

  public float getCapillarWavesSupression() {
    return config.getFloat(FFT_CAPILLARWAVES_SUPPRESSION);
  }

  public float getDisplacementScale() {
    return config.getFloat(DISPLACEMENT_SCALE);
  }

  public float getChoppiness() {
    return config.getFloat(CHOPPINESS);
  }

  public int getTessellationFactor() {
    return config.getInt(TESSELLATION_FACTOR);
  }

  public float getTessellationShift() {
    return config.getFloat(TESSELLATION_SHIFT);
  }

  public float getTessellationSlope() {
    return config.getFloat(TESSELLATION_SLOPE);
  }

  public int getHighDetailRange() {
    return config.getInt(HIGH_DETAIL_RANGE);
  }

  public int getUvScale() {
    return config.getInt(UV_SCALE);
  }

  public float getSpecularFactor() {
    return config.getFloat(SPECULAR_FACTOR);
  }

  public float getSpecularAmplifier() {
    return config.getFloat(SPECULAR_AMPLIFIER);
  }

  public boolean isDiffuse() {
    return config.getInt(DIFFUSE_ENABLE) != 0;
  }

  public float getEmission() {
    return config.getFloat(EMISSION_FACTOR);
  }

  public float getKReflection() {
    return config.getFloat(K_REFLECTION);
  }

  public float getKRefraction() {
    return config.getFloat(K_REFRACTION);
  }

  public float getDistortion() {
    return config.getFloat(DISTORTION_DELTA);
  }

  public float getFresnelFactor() {
    return config.getFloat(FRESNEL_FACTOR);
  }

  public float getWaveMotion() {
    return config.getFloat(WAVEMOTION);
  }

  public float getNormalStrength() {
    return config.getFloat(NORMAL_STRENGTH);
  }

  public float getT_delta() {
    return config.getFloat(T_DELTA);
  }

  public boolean isChoppy() {
    return config.getBoolean(CHOPPY);
  }

  public Vec3f getBaseColor() {
    return new Vec3f(
        config.getFloat(WATER_BASECOLOR_X),
        config.getFloat(WATER_BASECOLOR_Y),
        config.getFloat(WATER_BASECOLOR_Z));
  }

  public float getReflectionBlendFactor() {
    return config.getFloat(REFLECTION_BLENDFACTOR);
  }

  public float getCapillarStrength() {
    return config.getFloat(CAPILLAR_STRENGTH);
  }

  public float getCapillarDownsampling() {
    return config.getFloat(CAPILLAR_DOWNSAMPLING);
  }

  public float getDudvDownsampling() {
    return config.getFloat(DUDV_DOWNSAMPLING);
  }

  public float getUnderwaterBlur() {
    return config.getFloat(UNDERWATER_BLURFACTOR);
  }

  private PropertiesConfiguration readPropertiesConfiguration(final String file) throws ConfigurationException {
    return new Configurations().properties(file);
  }
}
