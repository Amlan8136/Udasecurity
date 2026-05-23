package com.udacity.catpoint.image.service;

import java.awt.image.BufferedImage;

/**
 * Interface for image analysis services.
 * Having an interface allows the SecurityService to be tested independently
 * of any specific implementation (Fake or AWS).
 * Rubric: "Create an interface that makes it easy to test our application
 * regardless of whether we're using the AwsImageService or FakeImageService."
 */
public interface ImageService {
    boolean imageContainsCat(BufferedImage image, float confidenceThreshold);
}
