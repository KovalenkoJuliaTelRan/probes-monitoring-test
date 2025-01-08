package telran.probes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.*;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.support.GenericMessage;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;

import jakarta.mail.Address;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.*;
import telran.probes.service.EmailsProviderClient;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
@Slf4j
class EmailNotifierControllerTest
{
	private static final long SENSOR_ID = 1;
	private static final double DEVIATION = -20.5;
	private static final double VALUE = 150;
	private static final String MAIL1 = "test@gmail.com";
	private static final String MAIL2 = "test@co.il";
	@Autowired
	InputDestination producer;
	@MockBean
	EmailsProviderClient providerService;
	@MockBean
	Consumer<SensorUpdateData> updateEmailsConsumer;
	@RegisterExtension
	static GreenMailExtension mailExtension = new GreenMailExtension(ServerSetupTest.SMTP).withConfiguration(GreenMailConfiguration.aConfig().withUser("user", "12345.com"));
	DeviationData deviationData = new DeviationData(SENSOR_ID, DEVIATION, VALUE, System.currentTimeMillis());
	String[] emails = { MAIL1, MAIL2, };
	private String consumerBindingName = "emailNotifierConsumer-in-0";
	@Test
	void sendingMailTest() throws Exception
	{
		when(providerService.getMails(SENSOR_ID)).thenReturn(emails);
		producer.send(new GenericMessage<DeviationData>(deviationData), consumerBindingName);
		MimeMessage[] messages = mailExtension.getReceivedMessages();
		assertEquals(2, messages.length);
		MimeMessage message = messages[0];
		Address[] recipients = message.getAllRecipients();
		assertEquals(emails.length, recipients.length);
		String[] actualMails = Arrays.stream(recipients).map(Address::toString).toArray(String[]::new);
		assertArrayEquals(emails, actualMails);
		assertTrue(message.getSubject().contains("" + SENSOR_ID));
		log.debug("content: {}", message.getContent());
	}
}
