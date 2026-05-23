package com.udacity.catpoint.security.service;

import javax.swing.*;
import java.util.logging.Logger;

public class StyleService {

    private static final Logger log = Logger.getLogger(StyleService.class.getName());

    private StyleService() {}

    public static void setStyle() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            log.warning("Could not set look and feel: " + e.getMessage());
        }
    }
}
