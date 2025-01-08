package telran.probes.service;

import telran.probes.dto.Range;

public interface SensorRangeProviderService
{
	Range getSensorRange(long sensorId);
}
