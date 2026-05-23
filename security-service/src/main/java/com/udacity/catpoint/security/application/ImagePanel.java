package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.service.SecurityService;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImagePanel extends JPanel implements StatusListener {

    private final SecurityService securityService;
    private BufferedImage currentImage;
    private final JLabel cameraLabel;
    private final JLabel imageLabel;

    private static final int W = 300, H = 225;

    public ImagePanel(SecurityService securityService) {
        super(new MigLayout("fillx, wrap 1"));
        this.securityService = securityService;
        securityService.addStatusListener(this);

        cameraLabel = new JLabel("Camera Feed");
        cameraLabel.setFont(new Font("Arial", Font.BOLD, 20));

        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(W, H));
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        imageLabel.setBackground(Color.BLACK);
        imageLabel.setOpaque(true);

        JButton refreshBtn = new JButton("Refresh Camera");
        refreshBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setCurrentDirectory(new File("."));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    currentImage = ImageIO.read(fc.getSelectedFile());
                    Image scaled = currentImage.getScaledInstance(W, H, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage());
                }
            }
        });

        JButton scanBtn = new JButton("Scan Picture");
        scanBtn.addActionListener(e -> {
            if (currentImage == null) {
                JOptionPane.showMessageDialog(this, "Please choose an image first.");
                return;
            }
            securityService.processImage(currentImage);
        });

        add(cameraLabel);
        add(imageLabel);
        add(refreshBtn, "split 2");
        add(scanBtn);
    }

    @Override
    public void notify(AlarmStatus status) {}

    @Override
    public void catDetected(Boolean cat) {
        if (Boolean.TRUE.equals(cat)) {
            cameraLabel.setText("DANGER - CAT DETECTED");
            cameraLabel.setForeground(Color.BLACK);
        } else {
            cameraLabel.setText("Camera Feed - No Cats Detected");
            cameraLabel.setForeground(Color.BLACK);
        }
    }

    @Override
    public void sensorStatusChanged() {}
}