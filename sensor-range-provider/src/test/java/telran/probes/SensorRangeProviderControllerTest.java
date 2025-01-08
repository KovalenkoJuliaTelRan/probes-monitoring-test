package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import telran.probes.dto.*;
import telran.probes.service.SensorRangeProviderService;
import static telran.probes.messages.ErrorMessages.WRONG_SENSOR_ID;
import static telran.exceptions.controller.ExceptionsController.TYPE_MISMATCH_MESSAGE;;
@WebMvcTest
class SensorRangeProviderControllerTest
{
	@MockBean
	SensorRangeProviderService service;
	@Autowired
	MockMvc mock;
	@Autowired
	ObjectMapper mapper;
	private static final long SENSOR_ID = 1;
	private static final Range RANGE = new Range(100, 200);
	private static final String PATH = "/sensor/range/";
	//normal flow
	@Test
	void testGetSensorRange() throws Exception
	{
		when(service.getSensorRange(SENSOR_ID)).thenReturn(RANGE);
		String actual = mock.perform(get(PATH+SENSOR_ID)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertEquals(mapper.writeValueAsString(RANGE), actual);
	}
	//alternate flow
	@Test
	void testGetSensorRangeWrongId() throws Exception
	{
		String actual = mock.perform(get(PATH+-1)).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(WRONG_SENSOR_ID, actual);
	}
	@Test
	void testGetSensorRangeIdNotLong() throws Exception
	{
		String actual = mock.perform(get(PATH+"hello")).andExpect(status().isBadRequest()).andReturn().getResponse().getContentAsString();
		assertEquals(TYPE_MISMATCH_MESSAGE, actual);
	}
}
