package com.udacity.catpoint.image.service;

import java.awt.image.BufferedImage;
import java.util.Random;

public class FakeImageService implements ImageService {

    private final Random random = new Random();

    @Override
    public boolean imageContainsCat(BufferedImage image, float confidenceThreshold) {
        return random.nextBoolean();
    }
}
