package telran.probes.service;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import telran.exceptions.EntityAlreadyExistsException;
import telran.exceptions.EntityNotFoundException;
import telran.probes.dto.*;
import static telran.probes.messages.ErrorMessages.*;
import telran.probes.model.SensorEmailsDoc;
import telran.probes.model.SensorRangeDoc;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminConsoleServiceImpl implements AdminConsoleService
{
	final MongoTemplate mongoTemplate;
	final StreamBridge streamBridge;
	String collectionNameRanges = "sensor_ranges";
	String collectionNameMails = "sensor_emails";
	String bindingName;
	FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(false);
	@Override
	public SensorRange addSensorRange(SensorRange sensorRange)
	{
		long sensorId = sensorRange.id();
		Range range = sensorRange.range();
		try
		{
			mongoTemplate.insert(new SensorRangeDoc(sensorId, range));
		} catch (DuplicateKeyException e)
		{
			log.error("sensor with id: {} already exists in collection {}", sensorId, collectionNameRanges);
			throw new EntityAlreadyExistsException(SENSOR_ALREADY_EXISTS);
		}
		log.debug("sensor: {} has been added", sensorRange);
		return sensorRange;
	}
	@Override
	public SensorEmails addSensorEmails(SensorEmails sensorEmails)
	{
		long sensorId = sensorEmails.id();
		String[] emails = sensorEmails.emails();
		try
		{
			mongoTemplate.insert(new SensorEmailsDoc(sensorId, emails));
		} catch (DuplicateKeyException e)
		{
			log.error("sensor with id: {} already exists in collection {}", sensorId, collectionNameMails);
			throw new EntityAlreadyExistsException(SENSOR_ALREADY_EXISTS);
		}
		log.debug("sensor: {} has been added", sensorEmails);
		return sensorEmails;
	}
	@Override
	public SensorRange updateSensorRange(SensorRange sensorRange)
	{
		long sensorId = sensorRange.id();
		Range range = sensorRange.range();
		Update update = new Update().set("range", range);
		Query query = new Query(Criteria.where("sensorId").is(sensorId));
		SensorRangeDoc sensorRangeRes = mongoTemplate.findAndModify(query, update, options, SensorRangeDoc.class);
		if (sensorRangeRes == null)
		{
			log.error("sensor {} doresn't exists in the collection {}", sensorId, collectionNameRanges);
			throw new EntityNotFoundException(SENSOR_NOT_FOUND);
		}
		log.debug("new range for sensor {} is {}", sensorId, range);
		SensorUpdateData updateData = new SensorUpdateData(sensorId, range, null);
		streamBridge.send(bindingName, updateData);
		log.debug("update data {} have been sent to binding name {}", updateData, bindingName);
		return sensorRange;
	}
	@Override
	public SensorEmails updateSensorEmails(SensorEmails sensorEmails)
	{
		long sensorId = sensorEmails.id();
		String[] emails = sensorEmails.emails();
		Update update = new Update();
		update.set("emails", emails);
		Query query = new Query(Criteria.where("sensorId").is(sensorId));
		SensorEmailsDoc sensorEmailsRes = mongoTemplate.findAndModify(query, update, options, SensorEmailsDoc.class);
		if (sensorEmailsRes == null)
		{
			log.error("sensor {} doresn't exists in the collection {}", sensorId, collectionNameMails);
			throw new EntityNotFoundException(SENSOR_NOT_FOUND);
		}	
		log.debug("new remails for sensor {} is {}", sensorId, emails);
		SensorUpdateData updateData = new SensorUpdateData(sensorId, null, emails);
		streamBridge.send(bindingName, updateData);
		log.debug("update data {} have been sent to binding name {}", updateData, bindingName);
		return sensorEmails;
	}
}
