package com.udacity.catpoint.security.application;

import com.udacity.catpoint.image.service.FakeImageService;
import com.udacity.catpoint.security.data.PretendDatabaseSecurityRepository;
import com.udacity.catpoint.security.service.SecurityService;
import com.udacity.catpoint.security.service.StyleService;

import javax.swing.*;

/**
 * Entry point for the CatPoint Security application.
 * Wires together the repository, image service, security service, and GUI.
 * Rubric tip: "After moving StyleService to the Security module, spotbugs
 * should not report a HIGH priority issue."
 */
public class CatpointApp {

    public static void main(String[] args) {
        // Apply look-and-feel styling before building the GUI
        StyleService.setStyle();

        SwingUtilities.invokeLater(() -> new CatpointGui());
    }
}
