package telran.probes.repo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SensorEmailsRepo extends MongoRepository<SensorEmailsDoc, Long> {}
