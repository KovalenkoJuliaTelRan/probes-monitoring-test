package telran.probes.service;

import telran.probes.dto.*;

public interface AdminConsoleService
{
	SensorRange addSensorRange(SensorRange sensorRange);
	SensorEmails addSensorEmails(SensorEmails sensorEmails);
	SensorRange updateSensorRange(SensorRange sensorRange);
	SensorEmails updateSensorEmails(SensorEmails sensorEmails);
}
