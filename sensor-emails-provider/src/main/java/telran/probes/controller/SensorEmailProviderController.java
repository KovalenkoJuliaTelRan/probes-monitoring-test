package telran.probes.controller;

import static telran.probes.messages.ErrorMessages.WRONG_SENSOR_ID;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.service.SensorEmailsProviderService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SensorEmailProviderController
{
	final SensorEmailsProviderService providerService;
	@Value("${app.emails.provider.path}")
	String path;
	@GetMapping("${app.emails.provider.path}/{id}")
	String[] getEmails(@PathVariable @Min(value = 1, message = WRONG_SENSOR_ID)long id)
	{
		String[] emails = providerService.getEmails(id);
		log.debug("emails received are {}", Arrays.deepToString(emails));
		return emails;
	}
}
