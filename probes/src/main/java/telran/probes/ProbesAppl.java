package telran.probes;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.probes.dto.ProbeData;
import telran.probes.service.ProbesService;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class ProbesAppl
{
	final ProbesService probesService;
	@Value("${app.sensors.producer.binding.name}")
	String producerBindingName;
	public static void main(String[] args) throws InterruptedException
	{
		SpringApplication.run(ProbesAppl.class, args);
	}
	@Bean
	Supplier<ProbeData> probesSupplier()
	{
		return () ->
		{
			ProbeData probeData = probesService.getRandomProbeData();
			log.debug("probe data: {} has been sent to {}", probeData, producerBindingName);
			return probeData;
		};
	}
}
