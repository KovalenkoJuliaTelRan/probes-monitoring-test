package telran.probes.controller;

import static telran.probes.messages.ErrorMessages.WRONG_SENSOR_ID;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.*;
import telran.probes.service.AdminConsoleService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AdminConsoleController
{
	final AdminConsoleService adminConsoleService;
	@Value("${app.range.provider.path}")
	String rangePath;
	@Value("${app.emails.provider.path}")
	String emailsPath;
	@PostMapping("${app.range.provider.path}")
	SensorRange addSensorRange(@RequestBody @Valid SensorRange sensorRange)
	{
		log.debug("adding received sensor range: {}", sensorRange);
		return adminConsoleService.addSensorRange(sensorRange);
	}
	@PostMapping("${app.emails.provider.path}")
	SensorEmails addSensorEmails(@RequestBody @Valid SensorEmails sensorEmails)
	{
		log.debug("adding received sensor emails: {}", sensorEmails);
		return adminConsoleService.addSensorEmails(sensorEmails);
	}
	@PutMapping("${app.range.provider.path}")
	SensorRange updateSensorRange(@RequestBody @Valid SensorRange sensorRange)
	{
		log.debug("updating received sensor range: {}", sensorRange);
		return adminConsoleService.updateSensorRange(sensorRange);
	}
	@PutMapping("${app.emails.provider.path}")
	SensorEmails updateSensorEmails(@RequestBody @Valid SensorEmails sensorEmails)
	{
		log.debug("updating received sensor emails: {}", sensorEmails);
		return adminConsoleService.updateSensorEmails(sensorEmails);
	}
}
