package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.Sensor;
import com.udacity.catpoint.security.data.SensorType;
import com.udacity.catpoint.security.service.SecurityService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class SensorPanel extends JPanel implements StatusListener {

    private final SecurityService securityService;
    private final JPanel sensorListPanel;

    public SensorPanel(SecurityService securityService) {
        super(new MigLayout("fillx, wrap 1"));
        this.securityService = securityService;
        securityService.addStatusListener(this);

        JLabel title = new JLabel("Sensor Management");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(10);
        JLabel typeLabel = new JLabel("Sensor Type:");
        JComboBox<SensorType> typeBox = new JComboBox<>(SensorType.values());

        JButton addBtn = new JButton("Add New Sensor");
        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (!name.isEmpty()) {
                securityService.addSensor(new Sensor(name, (SensorType) typeBox.getSelectedItem()));
                nameField.setText("");
                refreshList();
            }
        });

        add(title);
        add(nameLabel,  "split 4");
        add(nameField);
        add(typeLabel);
        add(typeBox,    "wrap");
        add(addBtn,     "wrap");

        sensorListPanel = new JPanel(new MigLayout("fillx"));
        add(sensorListPanel, "grow");
        refreshList();
    }

    private void refreshList() {
        sensorListPanel.removeAll();
        Set<Sensor> sensors = securityService.getSensors();

        for (Sensor s : sensors) {
            String state = Boolean.TRUE.equals(s.getActive()) ? "Active" : "Inactive";
            JLabel lbl = new JLabel(s.getName() + "(" + s.getSensorType() + "): " + state);

            String btnText = Boolean.TRUE.equals(s.getActive()) ? "Deactivate" : "Activate";
            JButton toggleBtn = new JButton(btnText);
            toggleBtn.addActionListener(e -> {
                securityService.changeSensorActivationStatus(s, !s.getActive());
                refreshList();
            });

            JButton removeBtn = new JButton("Remove Sensor");
            removeBtn.addActionListener(e -> {
                securityService.removeSensor(s);
                refreshList();
            });

            sensorListPanel.add(lbl,       "grow");
            sensorListPanel.add(toggleBtn, "");
            sensorListPanel.add(removeBtn, "wrap");
        }

        revalidate();
        repaint();
    }

    @Override public void notify(AlarmStatus status) { refreshList(); }
    @Override public void catDetected(Boolean cat)   {}
    @Override public void sensorStatusChanged()      { refreshList(); }
}