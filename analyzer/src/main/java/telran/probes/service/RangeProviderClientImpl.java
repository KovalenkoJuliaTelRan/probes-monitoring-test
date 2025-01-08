package telran.probes.service;

import java.util.HashMap;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.*;

@Configuration
@Service
@Slf4j
public class RangeProviderClientImpl implements RangeProviderClient
{
	RestTemplate restTemplate = new RestTemplate();
	HashMap<Long, Range> cache = new HashMap<>();
	@Value("${app.range.provider.host:localhost}")
	String host;
	@Value("${app.range.provider.port:8080}")
	int port;
	@Value("${app.range.provider.path:}")
	String path;
	@Override
	public Range getRange(long sensorId)
	{
		Range res = cache.get(sensorId);
		if (res == null)
		{
			log.debug("range for sensor with id {}  doesn't exist in cache", sensorId);
			res = serviceRequest(sensorId);
		} else
		{
			log.debug("range {} from cache", res);
		}
		return res;
	}
	private Range serviceRequest(long sensorId)
	{
		Range range = null;
		try
		{
			ResponseEntity<?> responseEntity = restTemplate.exchange(getUrl(sensorId), HttpMethod.GET, null, Range.class);
			if (responseEntity.getStatusCode().is4xxClientError())
				throw new Exception(responseEntity.getBody().toString());
			range = (Range) responseEntity.getBody();
			log.debug("range value {}", range);
			cache.put(sensorId, range);
		} catch (Exception e)
		{
			log.error("Error at service request: {}", e.getMessage());
			range = new Range(MIN_DEFAULT_VALUE, MAX_DEFAULT_VALUE);
			log.warn("default range value: {}", range);
		}
		return range;
	}
	private String getUrl(long sensorId)
	{
		String url = String.format("http://%s:%d%s%d", host, port, path, sensorId);
		log.debug("url created is {}", url);
		return url;
	}
	@Bean
	Consumer<SensorUpdateData> updateRangeConsumer()
	{
		return updateData -> 
		{
			long sensorId = updateData.id();
			Range range = updateData.range();
			if (cache.containsKey(sensorId) && range != null)
				cache.put(sensorId, range);
		};
	}
}