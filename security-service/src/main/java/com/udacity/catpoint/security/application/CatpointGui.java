package com.udacity.catpoint.security.application;

import com.udacity.catpoint.image.service.FakeImageService;
import com.udacity.catpoint.security.data.PretendDatabaseSecurityRepository;
import com.udacity.catpoint.security.service.SecurityService;
import com.udacity.catpoint.security.service.StyleService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class CatpointGui extends JFrame {

    public CatpointGui() {
        SecurityService securityService = new SecurityService(
                new PretendDatabaseSecurityRepository(),
                new FakeImageService());

        setTitle("Very Secure App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new MigLayout());

        JPanel mainPanel = new JPanel(new MigLayout("wrap 1", "[grow, fill]"));

        mainPanel.add(new DisplayPanel(securityService));
        mainPanel.add(new ImagePanel(securityService));
        mainPanel.add(new ControlPanel(securityService));
        mainPanel.add(new SensorPanel(securityService));

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        StyleService.setStyle();
        SwingUtilities.invokeLater(CatpointGui::new);
    }
}