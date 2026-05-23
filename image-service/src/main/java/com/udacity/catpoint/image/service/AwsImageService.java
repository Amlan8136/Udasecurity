package com.udacity.catpoint.image.service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsRequest;
import software.amazon.awssdk.services.rekognition.model.DetectLabelsResponse;
import software.amazon.awssdk.services.rekognition.model.Image;
import software.amazon.awssdk.services.rekognition.model.Label;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class AwsImageService implements ImageService {

    private static final Logger log = Logger.getLogger(AwsImageService.class.getName());
    private final RekognitionClient rekognitionClient;

    public AwsImageService() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("aws.properties")) {
            if (is != null) props.load(is);
        } catch (IOException e) {
            log.warning("Could not load aws.properties: " + e.getMessage());
        }
        rekognitionClient = RekognitionClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                props.getProperty("aws.accessKeyId", ""),
                                props.getProperty("aws.secretAccessKey", ""))))
                .region(Region.US_EAST_1)
                .build();
    }

    @Override
    public boolean imageContainsCat(BufferedImage image, float confidenceThreshold) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            DetectLabelsResponse response = rekognitionClient.detectLabels(
                    DetectLabelsRequest.builder()
                            .image(Image.builder().bytes(SdkBytes.fromByteArray(baos.toByteArray())).build())
                            .minConfidence(confidenceThreshold)
                            .build());
            return response.labels().stream()
                    .map(Label::name)
                    .anyMatch(name -> name.equalsIgnoreCase("Cat"));
        } catch (Exception e) {
            log.warning("AWS Rekognition error: " + e.getMessage());
            return false;
        }
    }
}
