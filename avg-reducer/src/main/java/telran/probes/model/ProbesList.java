package telran.probes.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RedisHash
@RequiredArgsConstructor
@Getter
public class ProbesList
{
	@Id
	@NonNull
	Long sensorId;
	List<Double> values = new ArrayList<>();
}
