package telran.probes.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Document(collection = "sensor_emails")
@AllArgsConstructor
@Getter
public class SensorEmailsDoc
{
	@Id
	long sensorId;
	String[] emails;
}
