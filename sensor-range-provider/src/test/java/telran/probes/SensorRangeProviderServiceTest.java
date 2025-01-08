package telran.probes;

import static org.junit.jupiter.api.Assertions.*;

import telran.exceptions.EntityNotFoundException;
import telran.probes.dto.Range;
import telran.probes.repo.SensorRangeDoc;
import telran.probes.repo.SensorRangesRepo;
import telran.probes.service.SensorRangeProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SensorRangeProviderServiceTest
{
	@Autowired
	SensorRangeProviderService service;
	@Autowired
	SensorRangesRepo repo;
	long SENSOR_ID = 1;
	Range range = new Range(100, 200);
	SensorRangeDoc sensor = new SensorRangeDoc(SENSOR_ID, range);
	@BeforeEach
	void setUp() throws Exception
	{
		repo.save(sensor);
	}
	@Test
	void testFindSensor()
	{
		assertEquals(range, service.getSensorRange(1));
	}
	@Test
	void testNotFindSensor()
	{
		Exception exception = assertThrows(EntityNotFoundException.class, () -> {service.getSensorRange(SENSOR_ID+1);});
	    assertEquals("sensor 2 not found", exception.getMessage());
	}
}
