package com.avro.crm.avro;

import static com.avro.crm.avro.ThrowingConsumer.throwingConsumerWrapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.BaseFieldTypeBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class AvroAdapter {
	
    /** Model Mapper Used to Create Avro Based Objects */
    private final Mapper mapper;

    @Autowired
    public AvroAdapter(Mapper mapper) {this.mapper = mapper;}

    public <T, AvroType extends GenericContainer> Optional<ByteArrayInputStream> createAvroByteArrayInputStream(
            List<T> payload, Class<AvroType> avroClazz) {

        if (payload == null || payload.isEmpty()) {
            return Optional.empty();
        }

        List<AvroType> avroPayloadObjects = payload.stream()
                .map(record -> mapper.map(record, avroClazz))
                .collect(Collectors.toList());

        try {

            /* Create Byte Array Output Stream */
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            /* Setup Avro Data Writer */
            DatumWriter<AvroType> userDatumWriter = new SpecificDatumWriter<>(avroClazz);
            DataFileWriter<AvroType> dataFileWriter = new DataFileWriter<>(userDatumWriter);
            dataFileWriter.create(avroPayloadObjects.get(0).getSchema(), outputStream);

            /* Write Avro File Objects */
            avroPayloadObjects.forEach(throwingConsumerWrapper(dataFileWriter::append));
            dataFileWriter.close();

            return Optional.of(new ByteArrayInputStream(outputStream.toByteArray()));

        } catch (IOException e) {
            log.warn("Unable to create Avro Byte Array", e);
            return Optional.empty();
        }

    }
    
    /**
     * @author longphan
     * @param payload
     * @param schemaName
     * @return Optional<ByteArrayInputStream>
     * @throws IOException 
     */
    public <T> Optional<ByteArrayInputStream> createAvroByteArrayInputStream(LinkedHashMap<String, Object> objectSchema,
			List<LinkedHashMap<String, Object>> payload, String schemaName){
  		//Create Byte Array Output Stream 
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final Schema schema = createSchema(objectSchema, "Crm" + schemaName);

        //Mapping data to Record
        List<GenericRecord> records = payload.stream().map(item -> buildingData(item, schema)).collect(Collectors.toList());
    
        // Setup Avro Data Writer 
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);
        try (DataFileWriter<GenericRecord> fileWriter = new DataFileWriter<>(datumWriter)) {
			/* Write Avro File Objects */
			fileWriter.create(schema, outputStream);
			records.stream().forEach(throwingConsumerWrapper(fileWriter::append));
        }catch(IOException e) {
        	  log.warn("Unable to create Avro Byte Array", e);
              return Optional.empty();
        }
        return Optional.of(new ByteArrayInputStream(outputStream.toByteArray()));
    }
    
    /**
     * Create Schema
     * @param values
     * @param schemaName
     * @return
     */
    private Schema createSchema(LinkedHashMap<String, Object> record, String schemaName) {
    	return createField(SchemaBuilder.record(schemaName).fields(), record);
    }
    /**
     * Create Schema fields
     * @param fields
     * @param record
     * @return
     */
    private Schema createField(FieldAssembler<Schema> fields, LinkedHashMap<String, Object> record) {
		record.entrySet().stream()
				.forEach(item -> createType(fields.name(item.getKey().toLowerCase()).type().nullable(),
						item.getValue().toString()));
    	return fields.endRecord();
    }
   
    /**
     * Create field type
     * @author longphan
     * @param type
     * @param field
     */
    private void createType(BaseFieldTypeBuilder<Schema> field, String type) {
    	switch (type) {
		case "int":
			field.intType().noDefault();
			break;
		case "boolean":
			field.booleanType().noDefault();
			break;
		/* Return Double Object */
		case "double":
		case "currency":
		case "percent":
			field.doubleType().noDefault();
			break;
		/* Return String Object */
		case "id":
		case "reference":
		case "picklist":
		case "multipicklist":
		case "combobox":
		case "textarea":
		case "phone":
		case "url":
		case "date":
		case "datetime":
		case "email":
		case "anytype":
		case "string":
			field.stringType().noDefault();
			break;
		/* Return Object which is not reading */
		case "_":
			log.warn("Unknown Data Type: {}", type);
			break;
		case "address":
		case "encryptedstring":
			log.warn("Unknown Data Type: {}", type);
			break;
		default:
			break;
		}
    }
	/**
	 * mapping data to Generic record
	 * @param dimension
	 * @param outputSchema
	 * @return
	 */
	private GenericRecord buildingData(LinkedHashMap<String, Object> data, Schema schema) {
		GenericRecord record = new GenericData.Record(schema);
		data.entrySet().stream().filter(f -> schema.getField(f.getKey().toLowerCase()) != null)
				.forEach(item -> record.put(item.getKey().toLowerCase(), item.getValue()));
		return record;
	}
}
