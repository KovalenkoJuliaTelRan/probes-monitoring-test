package telran.probes;

import static org.junit.jupiter.api.Assertions.*;

import telran.exceptions.EntityNotFoundException;
import telran.probes.repo.SensorEmailsDoc;
import telran.probes.repo.SensorEmailsRepo;
import telran.probes.service.SensorEmailsProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SensorEmailsProviderServiceTest
{
	@Autowired
	SensorEmailsProviderService service;
	@Autowired
	SensorEmailsRepo repo;
	private static final long SENSOR_ID = 1;
	private static final String[] emails = {"admin@gmail.com", "admin1@gmail.com"};
	private static final SensorEmailsDoc sensor = new SensorEmailsDoc(SENSOR_ID, emails);
	@BeforeEach
	void setUp() throws Exception
	{
		repo.save(sensor);
	}
	@Test
	void testFindSensor()
	{
		assertArrayEquals(emails, service.getEmails(SENSOR_ID));
	}
	@Test
	void testNotFindSensor()
	{
		Exception exception = assertThrows(EntityNotFoundException.class, () -> {service.getEmails(SENSOR_ID+1);});
	    assertEquals("sensor 2 not found", exception.getMessage());
	}
}
