package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.*;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.client.RestTemplate;

import telran.probes.dto.*;
import telran.probes.service.RangeProviderClient;
import static telran.probes.messages.ErrorMessages.*;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AnalyzerServiceTest
{
	private static final long SENSOR_ID = 123;
	private static final double MIN_VALUE = 100;
	private static final double MAX_VALUE = 200;
	private static final Range RANGE = new Range(MIN_VALUE, MAX_VALUE);
	private static final String URL = "http://localhost:8080/sensor/range/";
	private static final long SENSOR_ID_NOT_FOUND = 124;
	private static final Range RANGE_DEFAULT = new Range(RangeProviderClient.MIN_DEFAULT_VALUE, RangeProviderClient.MAX_DEFAULT_VALUE);
	private static final long SENSOR_ID_UNAVAILABLE = 170;
	private static final Range RANGE_UPDATED = new Range(MIN_VALUE + 10, MAX_VALUE + 10);
	@Value("${app.analyzer.update.range.binding.name}")
	String updateBindingName;
	@Autowired
	InputDestination producer;
	@Autowired
	RangeProviderClient providerService;
	@MockBean
	RestTemplate restTemplate;
	@Test
	@Order(1)
	void normalFlowNoCache()
	{
		when(restTemplate.exchange(getUrl(SENSOR_ID), HttpMethod.GET, null, Range.class)).thenReturn(new ResponseEntity<>(RANGE, HttpStatus.OK));
		assertEquals(RANGE, providerService.getRange(SENSOR_ID));
	}
	private String getUrl(long sensorId)
	{
		return URL + sensorId;
	}
	@Test
	@Order(2)
	void normalFlowWithCache()
	{
		verify(restTemplate, never()).exchange(getUrl(SENSOR_ID), HttpMethod.GET, null, Range.class);
		assertEquals(RANGE, providerService.getRange(SENSOR_ID));
	}

	@Test
	@Order(3)
	void sensorNotFoundTest()
	{
		when(restTemplate.exchange(getUrl(SENSOR_ID_NOT_FOUND), HttpMethod.GET, null, String.class)).thenReturn(new ResponseEntity<>(SENSOR_NOT_FOUND, HttpStatus.NOT_FOUND));
		assertEquals(RANGE_DEFAULT, providerService.getRange(SENSOR_ID_NOT_FOUND));
	}

	@Test
	@Order(4)
	void defaultRangeNotInCache()
	{
		when(restTemplate.exchange(getUrl(SENSOR_ID_NOT_FOUND), HttpMethod.GET, null, Range.class))
				.thenReturn(new ResponseEntity<>(RANGE, HttpStatus.OK));
		assertEquals(RANGE, providerService.getRange(SENSOR_ID_NOT_FOUND));
	}

	@SuppressWarnings("unchecked")
	@Test
	void remoteWebServiceAnavailable()
	{
		when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(), any(Class.class))).thenThrow(new RuntimeException("Service is unavailable"));
		assertEquals(RANGE_DEFAULT, providerService.getRange(SENSOR_ID_UNAVAILABLE));
	}

	@Test
	void updateRangeSensorInMap() throws InterruptedException
	{
		producer.send(new GenericMessage<SensorUpdateData>(new SensorUpdateData(SENSOR_ID, RANGE_UPDATED, null)),
				updateBindingName);
		Thread.sleep(100);
		assertEquals(RANGE_UPDATED, providerService.getRange(SENSOR_ID));
	}

}
