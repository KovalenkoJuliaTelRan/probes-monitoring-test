package telran.probes;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.DeviationData;
import telran.probes.service.EmailsProviderClient;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class EmailNotifierAppl
{
	@Value("${app.mail.notifier.subject:deviation of sensor }")
	private String subject;
	final JavaMailSender mailSender;
	final EmailsProviderClient providerService;
	public static void main(String[] args)
	{
		SpringApplication.run(EmailNotifierAppl.class, args);
	}
	@Bean
	Consumer<DeviationData> emailNotifierConsumer()
	{
		return deviation ->
		{
			log.debug("received deviation data: {}", deviation);
			long sensorId = deviation.id();
			String[] emails = providerService.getMails(sensorId);
			log.debug("received mail addresses are:{}", Arrays.toString(emails));
			SimpleMailMessage smm = new SimpleMailMessage();
			smm.setTo(emails);
			smm.setText(getText(deviation));
			smm.setSubject(getSubject(sensorId));
			mailSender.send(smm);
			log.debug("mail has been sent");
		};
	}
	private String getSubject(long sensorId)
	{
		String res = subject + sensorId;
		log.debug("subject: {} ", res);
		return res;
	}
	private String getText(DeviationData deviationData)
	{
		String text = String.format("sensor %d has value %f with deviation %f at %s", deviationData.id(), deviationData.value(), deviationData.deviation(), Instant.ofEpochMilli(deviationData.timestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime());
		return text;
	}
}
