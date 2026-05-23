package com.udacity.catpoint.security.service;

import com.udacity.catpoint.image.service.ImageService;
import com.udacity.catpoint.security.application.StatusListener;
import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.ArmingStatus;
import com.udacity.catpoint.security.data.SecurityRepository;
import com.udacity.catpoint.security.data.Sensor;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class SecurityService {

    private final ImageService imageService;
    private final SecurityRepository securityRepository;
    private final Set<StatusListener> statusListeners = new HashSet<>();
    private boolean catDetected = false;

    public SecurityService(SecurityRepository securityRepository, ImageService imageService) {
        this.securityRepository = securityRepository;
        this.imageService = imageService;
    }

    public void addStatusListener(StatusListener listener)    { statusListeners.add(listener); }
    public void removeStatusListener(StatusListener listener) { statusListeners.remove(listener); }
    public AlarmStatus getAlarmStatus()   { return securityRepository.getAlarmStatus(); }
    public ArmingStatus getArmingStatus() { return securityRepository.getArmingStatus(); }
    public Set<Sensor> getSensors()       { return securityRepository.getSensors(); }
    public void addSensor(Sensor sensor)  { securityRepository.addSensor(sensor); }
    public void removeSensor(Sensor sensor){ securityRepository.removeSensor(sensor); }

    public void setAlarmStatus(AlarmStatus status) {
        securityRepository.setAlarmStatus(status);
        statusListeners.forEach(l -> l.notify(status));
    }

    public void setArmingStatus(ArmingStatus armingStatus) {
        if (armingStatus == ArmingStatus.DISARMED) {
            setAlarmStatus(AlarmStatus.NO_ALARM);
        } else {
            ConcurrentSkipListSet<Sensor> sensors =
                    new ConcurrentSkipListSet<>(getSensors());
            sensors.forEach(s -> changeSensorActivationStatus(s, false));

            if (catDetected && armingStatus == ArmingStatus.ARMED_HOME) {
                setAlarmStatus(AlarmStatus.ALARM);
            }
        }
        securityRepository.setArmingStatus(armingStatus);
        statusListeners.forEach(StatusListener::sensorStatusChanged);
    }

    public void changeSensorActivationStatus(Sensor sensor) {
        if (securityRepository.getAlarmStatus() == AlarmStatus.PENDING_ALARM
                && !sensor.getActive()) {
            handleSensorDeactivated();
        }
        securityRepository.updateSensor(sensor);
    }

    public void changeSensorActivationStatus(Sensor sensor, Boolean active) {
        AlarmStatus alarm = securityRepository.getAlarmStatus();

        if (alarm != AlarmStatus.ALARM) {
            if (active) {
                handleSensorActivated();
            } else if (sensor.getActive()) {
                handleSensorDeactivated();
            }
        }
        sensor.setActive(active);
        securityRepository.updateSensor(sensor);
    }

    public void processImage(BufferedImage image) {
        catDetected = imageService.imageContainsCat(image, 50.0f);

        if (catDetected && getArmingStatus() == ArmingStatus.ARMED_HOME) {
            setAlarmStatus(AlarmStatus.ALARM);
        } else if (!catDetected && allSensorsInactive()) {
            setAlarmStatus(AlarmStatus.NO_ALARM);
        }
        statusListeners.forEach(l -> l.catDetected(catDetected));
    }

    private void handleSensorActivated() {
        if (securityRepository.getArmingStatus() == ArmingStatus.DISARMED) return;
        switch (securityRepository.getAlarmStatus()) {
            case NO_ALARM      -> setAlarmStatus(AlarmStatus.PENDING_ALARM);
            case PENDING_ALARM -> setAlarmStatus(AlarmStatus.ALARM);
            default            -> { }
        }
    }

    private void handleSensorDeactivated() {
        if (securityRepository.getAlarmStatus() == AlarmStatus.PENDING_ALARM
                && allSensorsInactive()) {
            setAlarmStatus(AlarmStatus.NO_ALARM);
        }
    }

    private boolean allSensorsInactive() {
        Set<Sensor> sensors = getSensors();
        return sensors.isEmpty() || sensors.stream().noneMatch(Sensor::getActive);
    }
}