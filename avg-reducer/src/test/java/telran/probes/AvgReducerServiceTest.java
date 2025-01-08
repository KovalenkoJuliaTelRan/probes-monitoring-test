package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.Binder;

import telran.probes.dto.ProbeData;
import telran.probes.model.ProbesList;
import telran.probes.repo.ProbesListRepo;
import telran.probes.service.AvgReducerService;

@SpringBootTest
class AvgReducerServiceTest
{
	private static final Double VALUE = 100.0;
	private ProbeData probeData = new ProbeData(1, VALUE, 0);
	@Autowired
	AvgReducerService reducerService;
	@MockBean
	ProbesListRepo probesListRepo;
	HashMap<Long, ProbesList> redisMockMap = new HashMap<>();
	@MockBean
    private Binder<?, ?, ?> binder;
	@BeforeEach
	void mockSetUp()
	{
		when(probesListRepo.findById(any(Long.class))).then(new Answer<Optional<ProbesList>>()
		{
			@Override
			public Optional<ProbesList> answer(InvocationOnMock invocation) throws Throwable
			{
				Long sensorId = invocation.getArgument(0);
				ProbesList probesList = redisMockMap.get(sensorId);
				return probesList == null ? Optional.ofNullable(null) : Optional.of(probesList);
			}
		});
		when(probesListRepo.save(any(ProbesList.class))).then(new Answer<ProbesList>()
		{
			@Override
			public ProbesList answer(InvocationOnMock invocation) throws Throwable
			{
				ProbesList probesList = invocation.getArgument(0);
				redisMockMap.put(probesList.getSensorId(), probesList);
				return probesList;
			}
		});
	}
	@Test
	void test()
	{
		Double res = reducerService.getAvgValue(probeData);
		assertNull(res);
		res = reducerService.getAvgValue(probeData);
		assertNotNull(res);
		assertEquals(VALUE, res);
		res = reducerService.getAvgValue(probeData);
		assertNull(res);
		res = reducerService.getAvgValue(probeData);
		assertNotNull(res);
		assertEquals(VALUE, res);
	}
}
