package com.udacity.catpoint.security.service;

import com.udacity.catpoint.image.service.ImageService;
import com.udacity.catpoint.security.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.when;

/**
 * Integration tests using FakeSecurityRepository (real in-memory state,
 * no property files). ImageService is still mocked for control.
 *
 * Rubric (optional): "Create a second test class called
 * SecurityServiceIntegrationTest.java and write methods that test our
 * requirements as integration tests."
 */
@ExtendWith(MockitoExtension.class)
public class SecurityServiceIntegrationTest {

    private SecurityService securityService;
    private FakeSecurityRepository repository;

    @Mock private ImageService imageService;

    @BeforeEach
    void setUp() {
        repository = new FakeSecurityRepository();
        securityService = new SecurityService(repository, imageService);
    }

    @Test
    void twoSensorsActivated_thenOneDeactivated_alarmRemainsActive() {
        repository.setArmingStatus(ArmingStatus.ARMED_HOME);
        Sensor door   = new Sensor("Door",   SensorType.DOOR);
        Sensor window = new Sensor("Window", SensorType.WINDOW);
        repository.addSensor(door);
        repository.addSensor(window);

        securityService.changeSensorActivationStatus(door,   true);   // NO_ALARM → PENDING
        assertEquals(AlarmStatus.PENDING_ALARM, repository.getAlarmStatus());

        securityService.changeSensorActivationStatus(window, true);   // PENDING  → ALARM
        assertEquals(AlarmStatus.ALARM, repository.getAlarmStatus());

        securityService.changeSensorActivationStatus(door,   false);  // ALARM stays ALARM (Req 4)
        assertEquals(AlarmStatus.ALARM, repository.getAlarmStatus());
    }

    @Test
    void catDetected_thenNoCat_alarmStatusReturnsToNoAlarm() {
        repository.setArmingStatus(ArmingStatus.ARMED_HOME);

        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        securityService.processImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
        assertEquals(AlarmStatus.ALARM, repository.getAlarmStatus());

        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);
        securityService.processImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
        assertEquals(AlarmStatus.NO_ALARM, repository.getAlarmStatus());
    }

    @Test
    void systemDisarmed_catDetected_noAlarmTriggered() {
        repository.setArmingStatus(ArmingStatus.DISARMED);

        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        securityService.processImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));

        assertEquals(AlarmStatus.NO_ALARM, repository.getAlarmStatus());
    }

    @Test
    void catDetectedWhileDisarmed_thenArmedHome_alarmTriggered() {
        repository.setArmingStatus(ArmingStatus.DISARMED);

        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        securityService.processImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));

        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);  // Req 11

        assertEquals(AlarmStatus.ALARM, repository.getAlarmStatus());
    }

    @Test
    void activeSensorsPresent_systemArmed_allSensorsResetToInactive() {
        repository.setArmingStatus(ArmingStatus.DISARMED);
        Sensor s1 = new Sensor("S1", SensorType.DOOR);
        Sensor s2 = new Sensor("S2", SensorType.WINDOW);
        s1.setActive(true);
        s2.setActive(true);
        repository.addSensor(s1);
        repository.addSensor(s2);

        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);  // Req 10

        repository.getSensors().forEach(s ->
            assertEquals(false, s.getActive(), s.getName() + " should be inactive after arming"));
    }

    @Test
    void catDetectedWithActiveSensor_noCatScannedAfter_alarmRemainsActive() {
        repository.setArmingStatus(ArmingStatus.ARMED_HOME);
        Sensor door = new Sensor("Door", SensorType.DOOR);
        repository.addSensor(door);

        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        securityService.processImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
        assertEquals(AlarmStatus.ALARM, repository.getAlarmStatus());

        securityService.changeSensorActivationStatus(door, true);  // sensor active

        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);
        securityService.processImage(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));

        // sensor still active → should NOT go to NO_ALARM
        assertEquals(AlarmStatus.ALARM, repository.getAlarmStatus());
    }
}
