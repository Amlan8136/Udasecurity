module com.udacity.catpoint.security {
    requires com.udacity.catpoint.image;
    requires com.google.common;
    requires com.google.gson;
    requires java.desktop;
    requires java.logging;
    requires java.prefs;
    requires com.miglayout.swing;
    requires org.checkerframework.checker.qual;

    opens com.udacity.catpoint.security.data to com.google.gson;
    opens com.udacity.catpoint.security.service to org.mockito;

    exports com.udacity.catpoint.security.service;
    exports com.udacity.catpoint.security.data;
    exports com.udacity.catpoint.security.application;
}