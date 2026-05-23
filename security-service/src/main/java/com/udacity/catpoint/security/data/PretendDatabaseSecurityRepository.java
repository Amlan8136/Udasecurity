package com.udacity.catpoint.security.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.prefs.Preferences;

public class PretendDatabaseSecurityRepository implements SecurityRepository {

    private static final String SENSORS       = "SENSORS";
    private static final String ALARM_STATUS  = "ALARM_STATUS";
    private static final String ARMING_STATUS = "ARMING_STATUS";

    private final Preferences prefs = Preferences.userNodeForPackage(PretendDatabaseSecurityRepository.class);
    private final Gson gson = new GsonBuilder().create();

    @Override
    public void addSensor(Sensor sensor) {
        Set<Sensor> sensors = getSensors();
        sensors.add(sensor);
        saveSensors(sensors);
    }

    @Override
    public void removeSensor(Sensor sensor) {
        Set<Sensor> sensors = getSensors();
        sensors.remove(sensor);
        saveSensors(sensors);
    }

    @Override
    public void updateSensor(Sensor sensor) {
        Set<Sensor> sensors = getSensors();
        sensors.remove(sensor);
        sensors.add(sensor);
        saveSensors(sensors);
    }

    private void saveSensors(Set<Sensor> sensors) {
        prefs.put(SENSORS, gson.toJson(sensors));
    }

    @Override
    public Set<Sensor> getSensors() {
        String json = prefs.get(SENSORS, null);
        if (json == null) return new HashSet<>();
        Type type = new TypeToken<Set<Sensor>>(){}.getType();
        return gson.fromJson(json, type);
    }

    @Override
    public void setAlarmStatus(AlarmStatus status)    { prefs.put(ALARM_STATUS, status.toString()); }

    @Override
    public AlarmStatus getAlarmStatus() {
        return AlarmStatus.valueOf(prefs.get(ALARM_STATUS, AlarmStatus.NO_ALARM.toString()));
    }

    @Override
    public void setArmingStatus(ArmingStatus status)  { prefs.put(ARMING_STATUS, status.toString()); }

    @Override
    public ArmingStatus getArmingStatus() {
        return ArmingStatus.valueOf(prefs.get(ARMING_STATUS, ArmingStatus.DISARMED.toString()));
    }
}
