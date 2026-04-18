package com.udacity.catpoint.security.service;

import com.udacity.catpoint.image.ImageService;
import com.udacity.catpoint.security.application.StatusListener;
import com.udacity.catpoint.security.data.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.awt.image.BufferedImage;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SecurityServiceTest {

    @Mock
    private SecurityRepository securityRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private StatusListener statusListener;

    private SecurityService securityService;

    @BeforeEach
    void setup() {
        securityService = new SecurityService(securityRepository, imageService);
        securityService.addStatusListener(statusListener);
    }

    /* PARAMETERIZED TEST
       Armed + sensor activation transitions */
    @ParameterizedTest
    @EnumSource(value = AlarmStatus.class, names = {"NO_ALARM", "PENDING_ALARM"})
    void armedSystem_sensorActivated_transitionsCorrectly(AlarmStatus initialStatus) {

        Sensor sensor = new Sensor("Door", SensorType.DOOR);

        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_AWAY);
        when(securityRepository.getAlarmStatus()).thenReturn(initialStatus);

        securityService.changeSensorActivationStatus(sensor, true);

        if (initialStatus == AlarmStatus.NO_ALARM) {
            verify(securityRepository).setAlarmStatus(AlarmStatus.PENDING_ALARM);
        } else {
            verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
        }
    }

    // Pending alarm + all sensors inactive → NO_ALARM
    @Test
    void pendingAlarm_allSensorsInactive_setsNoAlarm() {
        Sensor sensor = new Sensor("Motion", SensorType.MOTION);
        sensor.setActive(true);

        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }

    // Alarm active → sensor changes do nothing
    @Test
    void alarmActive_sensorChange_doesNothing() {
        Sensor sensor = new Sensor("Door", SensorType.DOOR);

        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, never()).setAlarmStatus(any());
    }

    // Active sensor activated again while pending → ALARM
    @Test
    void activeSensor_activatedAgain_pendingAlarm_setsAlarm() {
        Sensor sensor = new Sensor("Window", SensorType.WINDOW);
        sensor.setActive(true);

        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    //Inactive sensor deactivated again → no change
    @Test
    void inactiveSensor_deactivatedAgain_noAlarmChange() {
        Sensor sensor = new Sensor("Motion", SensorType.MOTION);
        sensor.setActive(false);

        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);

        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository, never()).setAlarmStatus(any());
    }

    @Test
    void sensorDeactivated_whenAlarm_setsPending() {
        Sensor sensor = new Sensor("Door", SensorType.DOOR);
        sensor.setActive(true);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.ALARM);

        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.PENDING_ALARM);
    }


    // Cat detected + ARMED_HOME → ALARM
    @Test
    void catDetected_armedHome_setsAlarm() {
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(imageService.imageContainsCat(any(BufferedImage.class), anyFloat()))
                .thenReturn(true);

        securityService.processImage(
                new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB)
        );

        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }

    // No cat + no sensors active → NO_ALARM
    @Test
    void noCatDetected_andNoActiveSensors_setsNoAlarm() {
        Sensor sensor = new Sensor("Motion", SensorType.MOTION);
        sensor.setActive(false);

        when(securityRepository.getSensors())
                .thenReturn(Set.of(sensor));

        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(false);

        securityService.processImage(
                new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB)
        );

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.NO_ALARM);
    }
    @Test
    void catDetection_notifiesListeners() {
        when(imageService.imageContainsCat(any(), anyFloat()))
                .thenReturn(true);

        securityService.processImage(
                new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB)
        );

        verify(statusListener).catDetected(true);
    }


    //Disarmed system → NO_ALAR
    @Test
    void disarmingSystem_setsNoAlarm() {
        securityService.setArmingStatus(ArmingStatus.DISARMED);

        verify(securityRepository).setAlarmStatus(AlarmStatus.NO_ALARM);
    }
    // Armed system resets all sensors to inactive
    @Test
    void armingSystem_resetsSensorsToInactive() {
        Sensor sensor = new Sensor("Door", SensorType.DOOR);
        sensor.setActive(true);

        when(securityRepository.getSensors()).thenReturn(Set.of(sensor));

        securityService.setArmingStatus(ArmingStatus.ARMED_AWAY);

        assertFalse(sensor.getActive());
    }
    //Armed-home + cat already detected → ALARM
    @Test
    void armedHome_withCatAlreadyDetected_setsAlarm() {
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        when(securityRepository.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);

        securityService.processImage(
                new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB)
        );
        securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
        verify(securityRepository).setAlarmStatus(AlarmStatus.ALARM);
    }
    @Test
    void setAlarmStatus_notifiesListeners() {
        securityService.setAlarmStatus(AlarmStatus.ALARM);
        verify(statusListener).notify(AlarmStatus.ALARM);
    }
    @Test
    void removeStatusListener_stopsNotifications() {
        securityService.removeStatusListener(statusListener);
        securityService.setAlarmStatus(AlarmStatus.ALARM);
        verify(statusListener, never()).notify(any());
    }
    @Test
    void sensorDeactivated_whenPending_setsNoAlarm() {
        Sensor sensor = new Sensor("Door", SensorType.DOOR);
        sensor.setActive(true);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.NO_ALARM);
    }
    @Test
    void sensorActivated_whileDisarmed_doesNothing() {
        Sensor sensor = new Sensor("Door", SensorType.DOOR);

        when(securityRepository.getArmingStatus())
                .thenReturn(ArmingStatus.DISARMED);

        securityService.changeSensorActivationStatus(sensor, true);

        verify(securityRepository, never()).setAlarmStatus(any());
    }
    @Test
    void sensorDeactivated_whenPendingAlarm_setsNoAlarm() {
        Sensor sensor = new Sensor("Window", SensorType.WINDOW);
        sensor.setActive(true);

        when(securityRepository.getAlarmStatus())
                .thenReturn(AlarmStatus.PENDING_ALARM);

        securityService.changeSensorActivationStatus(sensor, false);

        verify(securityRepository)
                .setAlarmStatus(AlarmStatus.NO_ALARM);
    }
    @Test
    void getAlarmStatus_callsRepository() {
        securityService.getAlarmStatus();
        verify(securityRepository).getAlarmStatus();
    }
    @Test
    void armingSystem_notifiesSensorStatusChanged() {
        Sensor sensor = new Sensor("Door", SensorType.DOOR);
        sensor.setActive(true);

        when(securityRepository.getSensors()).thenReturn(Set.of(sensor));

        securityService.setArmingStatus(ArmingStatus.ARMED_AWAY);

        verify(statusListener).sensorStatusChanged();
    }


}
