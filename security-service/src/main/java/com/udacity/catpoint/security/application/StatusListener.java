package com.udacity.catpoint.security.application;

import com.udacity.catpoint.security.data.AlarmStatus;

public interface StatusListener {
    void notify(AlarmStatus status);
    void catDetected(Boolean catDetected);
    void sensorStatusChanged();
}
