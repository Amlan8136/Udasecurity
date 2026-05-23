package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.ArmingStatus;
import com.udacity.catpoint.security.service.SecurityService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ControlPanel extends JPanel implements StatusListener {

    private final SecurityService securityService;
    private final Map<ArmingStatus, JButton> buttonMap;

    public ControlPanel(SecurityService securityService) {
        super(new MigLayout("fillx, wrap 1"));
        this.securityService = securityService;
        securityService.addStatusListener(this);

        JLabel title = new JLabel("System Control");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JButton disarmedBtn  = new JButton("Disarmed");
        JButton armedHomeBtn = new JButton("Armed - At Home");
        JButton armedAwayBtn = new JButton("Armed - Away");

        buttonMap = Map.of(
                ArmingStatus.DISARMED,   disarmedBtn,
                ArmingStatus.ARMED_HOME, armedHomeBtn,
                ArmingStatus.ARMED_AWAY, armedAwayBtn
        );

        disarmedBtn.addActionListener(e  -> setArming(ArmingStatus.DISARMED));
        armedHomeBtn.addActionListener(e -> setArming(ArmingStatus.ARMED_HOME));
        armedAwayBtn.addActionListener(e -> setArming(ArmingStatus.ARMED_AWAY));

        add(title);
        add(disarmedBtn,  "split 3");
        add(armedHomeBtn);
        add(armedAwayBtn, "wrap");

        highlightCurrent(securityService.getArmingStatus());
    }

    private void setArming(ArmingStatus status) {
        securityService.setArmingStatus(status);
        highlightCurrent(status);
    }

    private void highlightCurrent(ArmingStatus status) {
        buttonMap.forEach((s, btn) ->
                btn.setBackground(s == status ? new Color(180, 160, 0) : null));
    }

    @Override public void notify(AlarmStatus status) {}
    @Override public void catDetected(Boolean cat)   {}
    @Override public void sensorStatusChanged()      { highlightCurrent(securityService.getArmingStatus()); }
}