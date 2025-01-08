package telran.probes.service;

public interface EmailsProviderClient
{
	String SERVICE_EMAIL = "service-sensors@gmail.com";
	String[] getMails(long sensorId);
}
