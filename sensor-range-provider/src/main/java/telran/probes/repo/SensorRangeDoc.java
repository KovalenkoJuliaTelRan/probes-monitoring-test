package telran.probes.repo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import telran.probes.dto.Range;

@Document(collection = "sensor_ranges")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class SensorRangeDoc
{
	@Id
	long sensorId;
	Range range;
}
