package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.service.SecurityService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class DisplayPanel extends JPanel implements StatusListener {

    private final JLabel statusLabel;

    private static final Map<AlarmStatus, Color> STATUS_COLORS = Map.of(
            AlarmStatus.NO_ALARM,      new Color(0, 204, 0),
            AlarmStatus.PENDING_ALARM, new Color(255, 165, 0),
            AlarmStatus.ALARM,         new Color(255, 0, 0)
    );

    private static final Map<AlarmStatus, String> STATUS_TEXT = Map.of(
            AlarmStatus.NO_ALARM,      "Cool and Good",
            AlarmStatus.PENDING_ALARM, "I guess someone is in your home",
            AlarmStatus.ALARM,         "Awooga!"
    );

    public DisplayPanel(SecurityService securityService) {
        super(new MigLayout("fillx"));
        securityService.addStatusListener(this);

        JLabel titleLabel = new JLabel("Very Secure Home Security");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel systemStatusTitle = new JLabel("System Status: ");
        systemStatusTitle.setFont(new Font("Arial", Font.BOLD, 14));

        statusLabel = new JLabel();
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        updateStatus(securityService.getAlarmStatus());

        add(titleLabel,       "wrap");
        add(systemStatusTitle, "split 2");
        add(statusLabel,      "wrap");
    }

    private void updateStatus(AlarmStatus status) {
        statusLabel.setText(STATUS_TEXT.getOrDefault(status, "Unknown"));
        statusLabel.setBackground(STATUS_COLORS.getOrDefault(status, Color.GRAY));
        statusLabel.setForeground(Color.WHITE);
    }

    @Override public void notify(AlarmStatus status) { updateStatus(status); }
    @Override public void catDetected(Boolean cat)   {}
    @Override public void sensorStatusChanged()      {}
}