package com.udacity.catpoint.security.service;

import com.udacity.catpoint.security.data.AlarmStatus;
import com.udacity.catpoint.security.data.ArmingStatus;
import com.udacity.catpoint.security.data.SecurityRepository;
import com.udacity.catpoint.security.data.Sensor;

import java.util.HashSet;
import java.util.Set;

/**
 * In-memory SecurityRepository for integration tests.
 * No property files or Java Preferences API — just plain HashSet and fields.
 * Rubric (optional): "Create a FakeSecurityRepository class that works just
 * like the PretendDatabaseSecurityRepository class except without the property files."
 */
public class FakeSecurityRepository implements SecurityRepository {

    private AlarmStatus  alarmStatus  = AlarmStatus.NO_ALARM;
    private ArmingStatus armingStatus = ArmingStatus.DISARMED;
    private final Set<Sensor> sensors = new HashSet<>();

    @Override public void addSensor(Sensor s)    { sensors.add(s); }
    @Override public void removeSensor(Sensor s) { sensors.remove(s); }
    @Override public void updateSensor(Sensor s) { sensors.remove(s); sensors.add(s); }
    @Override public Set<Sensor> getSensors()    { return sensors; }

    @Override public void setAlarmStatus(AlarmStatus s)  { this.alarmStatus = s; }
    @Override public AlarmStatus getAlarmStatus()        { return alarmStatus; }

    @Override public void setArmingStatus(ArmingStatus s){ this.armingStatus = s; }
    @Override public ArmingStatus getArmingStatus()      { return armingStatus; }
}
