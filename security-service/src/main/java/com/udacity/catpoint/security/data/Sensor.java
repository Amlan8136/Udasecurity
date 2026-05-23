package com.udacity.catpoint.security.data;

import com.google.common.collect.ComparisonChain;

import java.util.Objects;
import java.util.UUID;

public class Sensor implements Comparable<Sensor> {

    private final UUID sensorId;
    private String name;
    private Boolean active;
    private SensorType sensorType;

    public Sensor(String name, SensorType sensorType) {
        this.name = name;
        this.sensorType = sensorType;
        this.sensorId = UUID.randomUUID();
        this.active = false;
    }

    public UUID getSensorId()                     { return sensorId; }
    public String getName()                       { return name; }
    public void setName(String name)              { this.name = name; }
    public Boolean getActive()                    { return active; }
    public void setActive(Boolean active)         { this.active = active; }
    public SensorType getSensorType()             { return sensorType; }
    public void setSensorType(SensorType t)       { this.sensorType = t; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sensor)) return false;
        return Objects.equals(sensorId, ((Sensor) o).sensorId);
    }

    @Override
    public int hashCode() { return Objects.hash(sensorId); }

    @Override
    public int compareTo(Sensor other) {
        return ComparisonChain.start()
                .compare(this.name, other.name)
                .compare(this.sensorId.toString(), other.sensorId.toString())
                .result();
    }
}
