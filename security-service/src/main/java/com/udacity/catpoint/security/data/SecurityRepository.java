package com.udacity.catpoint.security.data;

import java.util.Set;

public interface SecurityRepository {
    void addSensor(Sensor sensor);
    void removeSensor(Sensor sensor);
    void updateSensor(Sensor sensor);
    Set<Sensor> getSensors();

    void setAlarmStatus(AlarmStatus status);
    AlarmStatus getAlarmStatus();

    void setArmingStatus(ArmingStatus status);
    ArmingStatus getArmingStatus();
}
