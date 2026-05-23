module com.udacity.catpoint.image {
    requires software.amazon.awssdk.auth;
    requires software.amazon.awssdk.core;
    requires software.amazon.awssdk.regions;
    requires software.amazon.awssdk.services.rekognition;

    requires com.google.gson;

    requires java.desktop;
    requires java.logging;

    exports com.udacity.catpoint.image.service;
}

