package telran.probes;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.*;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class ProbesTest
{
	@Autowired
	OutputDestination consumer;
	@Test
	void test() throws InterruptedException
	{
		String bindingName = "probesSupplier-out-0";
		for (int i = 0; i < 8; i++)
		{
			assertNotNull(consumer.receive(1500, bindingName));
		}
	}
}
