package telran.probes.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SensorRangesRepo extends MongoRepository<SensorRangeDoc, Long> {}
