package com.udacity.catpoint.security.service;

import com.udacity.catpoint.image.service.ImageService;
import com.udacity.catpoint.security.application.StatusListener;
import com.udacity.catpoint.security.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SecurityServiceTest {

    private SecurityService securityService;
    private Sensor sensor;

    @Mock private ImageService       imageService;
    @Mock private SecurityRepository securityRepository;
    @Mock private StatusListener     statusListener;

    private static final BufferedImage TEST_IMAGE =
            new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);

    private Sensor newSensor() {
        return new Sensor(UUID.randomUUID().toString(), SensorType.DOOR);
    }

    private Set<Sensor> buildSensors(int count, boolean active) {
        Set<Sensor> set = new HashSet<>();
        for (int i = 0; i < count; i++) {
            Sensor s = new Sensor(UUID.randomUUID().toString(), SensorType.DOOR);
            s.setActive(active);
            set.add(s);
        }
        return set;
    }

    @BeforeEach
    void setUp() {
        securityService = new SecurityService(securityRepository, imageService);
        sensor = newSensor();
        securityService.addStatusListener(statusListener);
    }

    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void sensorActivated_armedAndNoAlarm_alarmStatusSetToPending(ArmingStatus status) {
        when(securityRepository.getArmingStatus()).thenReturn(status);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }

    @Test
    void sensorActivated_systemArmedAndStatusPending_alarmStatusSetToAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void sensorInactive_alarmPendingAndAllSensorsInactive_alarmStatusSetToNoAlarm() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        when(securityRepository.getSensors()).thenReturn(new HashSet<>());
        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    void sensorInactive_alarmPendingButOtherSensorsStillActive_alarmStatusNotCleared() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        when(securityRepository.getSensors()).thenReturn(buildSensors(1, true));
        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor);
        verify(securityRepository, never()).setAlarmStatus(any());
    }

    @Test
    void sensorDeactivated_noAlarmState_noAlarmChange() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor);
        verify(securityRepository, never()).setAlarmStatus(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void sensorStateChanged_alarmActive_alarmStateNotAffected(boolean newState) {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);
        securityService.changeSensorActivationStatus(sensor, newState);
        verify(securityRepository, never()).setAlarmStatus(any());
    }

    @Test
    void sensorDeactivated_alarmActive_alarmStatusRemainsAlarm() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);
        securityService.changeSensorActivationStatus(sensor, false);
        verify(securityRepository, never()).setAlarmStatus(AlarmStatus.PENDING_ALARM);
        verify(securityRepository, never()).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    void sensorActivatedWhileAlreadyActive_alarmPending_alarmStatusSetToAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        sensor.setActive(true);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    @ParameterizedTest
    @EnumSource(AlarmStatus.class)
    void sensorDeactivatedWhileAlreadyInactive_anyAlarmStatus_noAlarmChange(AlarmStatus status) {
        when(securityRepository.getAlarmStatus()).thenReturn(status);
        sensor.setActive(false);
        securityService.changeSensorActivationStatus(sensor, false);
        verify(securityRepository, never()).setAlarmStatus(any());
    }

    @Test
    void sensorDeactivated_whileActive_pendingAlarm_alarmStatusSetToNoAlarm() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        when(securityRepository.getSensors()).thenReturn(new HashSet<>());
        sensor.setActive(true);
        securityService.changeSensorActivationStatus(sensor, false);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    void imageAnalyzed_catDetectedAndSystemArmedHome_alarmStatusSetToAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        securityService.processImage(TEST_IMAGE);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void imageAnalyzed_catDetectedAndSystemArmedAway_alarmStatusNotSetToAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_AWAY);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        securityService.processImage(TEST_IMAGE);
        verify(securityRepository, never()).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void imageAnalyzed_noCatAndAllSensorsInactive_alarmStatusSetToNoAlarm() {
        when(securityRepository.getSensors()).thenReturn(buildSensors(3, false));
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);
        securityService.processImage(TEST_IMAGE);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    void imageAnalyzed_noCatButSensorsActive_alarmStatusNotChanged() {
        when(securityRepository.getSensors()).thenReturn(buildSensors(2, true));
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);
        securityService.processImage(TEST_IMAGE);
        verify(securityRepository, never()).setAlarmStatus(any());
    }

    @Test
    void armingStatusSet_systemDisarmed_alarmStatusSetToNoAlarm() {
        securityService.setArmingStatus(ArmingStatus.DISARMED);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void armingStatusSet_systemArmed_allSensorsResetToInactive(ArmingStatus status) {
        Set<Sensor> sensors = buildSensors(3, true);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        when(securityRepository.getSensors()).thenReturn(sensors);
        securityService.setArmingStatus(status);
        sensors.forEach(s -> assertFalse(s.getActive()));
    }

    @ParameterizedTest
    @EnumSource(value = ArmingStatus.class, names = {"ARMED_HOME", "ARMED_AWAY"})
    void armingStatusSet_systemArmed_repositoryUpdatedForEachSensor(ArmingStatus status) {
        Set<Sensor> sensors = buildSensors(3, true);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        when(securityRepository.getSensors()).thenReturn(sensors);
        securityService.setArmingStatus(status);
        verify(securityRepository, times(sensors.size())).updateSensor(any(Sensor.class));
    }

    @Test
    void armingStatusSetToArmedHome_catPreviouslyDetectedWhileDisarmed_alarmStatusSetToAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        securityService.processImage(TEST_IMAGE);

        when(securityRepository.getSensors()).thenReturn(new HashSet<>());
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);

        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void armingStatusSetToArmedAway_catPreviouslyDetected_alarmStatusNotSetToAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        securityService.processImage(TEST_IMAGE);

        when(securityRepository.getSensors()).thenReturn(new HashSet<>());
        securityService.setArmingStatus(ArmingStatus.ARMED_AWAY);

        verify(securityRepository, never()).setAlarmStatus(AlarmStatus.ALARM);
    }

    @Test
    void sensorActivated_systemDisarmed_noAlarmChange() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        securityService.changeSensorActivationStatus(sensor, true);
        verify(securityRepository, never()).setAlarmStatus(any());
    }

    @Test
    void addAndRemoveStatusListener_noExceptionThrown() {
        securityService.addStatusListener(statusListener);
        securityService.removeStatusListener(statusListener);
    }

    @Test
    void addAndRemoveSensor_noExceptionThrown() {
        securityService.addSensor(sensor);
        securityService.removeSensor(sensor);
    }

    @Test
    void imageAnalyzed_noCatAndNoSensors_alarmStatusSetToNoAlarm() {
        when(securityRepository.getSensors()).thenReturn(new HashSet<>());
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(false);
        securityService.processImage(TEST_IMAGE);
        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    @Test
    void setAlarmStatus_withFakeRepository_repositoryStateUpdatedCorrectly() {
        FakeSecurityRepository fakeRepo = new FakeSecurityRepository();
        SecurityService service = new SecurityService(fakeRepo, imageService);
        service.setAlarmStatus(AlarmStatus.ALARM);
        assert fakeRepo.getAlarmStatus() == AlarmStatus.ALARM;
    }

    @Test
    void sensorActivated_armedAndAlarmAlreadyActive_alarmStatusNotChanged() {
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, never()).setAlarmStatus(any());
        verify(securityRepository).updateSensor(sensor);
    }

    @Test
    void sensorActivated_systemDisarmed_alarmStatusNotChanged() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, never()).setAlarmStatus(any());
        verify(securityRepository).updateSensor(sensor);
    }
}