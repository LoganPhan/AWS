Accent Task Config Service
=

## Task Config Service

The Task Config Service sits on top of a PostgresSQL database with a responsibility of exposing the ability to read and write configurations. 
The Task Config Service is the only service that will be accessible via the Internet and therefore will need to support user Authentication.
The Orchestrator services communicate with the task config services to retrieve task configs and keep track of task runs.


The task config service is the only service that will be interfacing, and therefore an API Key is used to restrict access. 

•	ACCENT_EES_DB_USER – contain the DB account

•	ACCENT_EES_DB_PASS – contains the DB account password

•	ACCENT_EES_DB_URL – database connection string (i.e. jdbc:postgresql://localhost:5432/accent_ees)

•	ACCENT_EES_TCS_API_KEY – the API Key to use for access


SQL Scripts:
```
create database accent_ees;
//RECONNECT TO accent_ees then run the following:

CREATE USER eeapp WITH PASSWORD 'eeapp';

GRANT ALL PRIVILEGES ON DATABASE accent_ees TO eeapp;

create schema task_config;

GRANT ALL ON schema task_config TO eeapp;

GRANT ALL ON ALL TABLES IN SCHEMA task_config TO eeapp;

GRANT ALL ON ALL SEQUENCES IN SCHEMA task_config TO eeapp;

GRANT ALL ON ALL FUNCTIONS IN SCHEMA task_config TO eeapp;
```

**Start spring boot and allow liquidbase to build schema then run the below.**

You may need to adjust the insert for servers, import_option_filters, and/or aws_config


```
   INSERT INTO task_config.clients (name) VALUES ('accent-technologies.com');
   
   INSERT INTO task_config.import_options (client_id, include_appointments, include_attachments, import_time_from)
   VALUES (1, TRUE, FALSE, '2013-01-01 00:00:00');
   
   INSERT INTO task_config.import_folders (import_options_id, client_id, folder_path)
   VALUES (1, 1, '/inbox');
   INSERT INTO task_config.import_folders (import_options_id, client_id, folder_path)
   VALUES (1, 1, '/sent items');
   INSERT INTO task_config.import_folders (import_options_id, client_id, folder_path)
   VALUES (1, 1, '/deleted items');
   INSERT INTO task_config.import_folders (import_options_id, client_id, folder_path)
   VALUES (1, 1, 'calendar');
   
   INSERT INTO task_config.import_option_filters (id, import_options_id, client_id, filter_type, filter_contains) VALUES (1, 1, 1, 'INCLUDE', 'wsantas@yahoo.com');
   
   INSERT INTO task_config.servers (id, client_id, name, server_type, server_url, impersonator_address, impersonator_password, sugar_id, sugar_secret) VALUES (2, null, null, null, null, null, null, null, null, null);
   INSERT INTO task_config.servers (id, client_id, name, server_type, server_url, impersonator_address, impersonator_password, sugar_id, sugar_secret) VALUES (1, null, null, null, 'https://dev-exchange.accent-technologies.com/ews/exchange.asmx', 'exchservice@accenttest.local', 'S0m3DummyPa$$', null, null, 1000);
   
   INSERT INTO task_config.aws_config (id, client_id, name, region, access_key, secret_key, type, bucket_name, bucket_path, kinesis_stream_name, config_api_key) VALUES (1, null, null, null, null, null, null, null, null, null, null);
   INSERT INTO task_config.aws_config (id, client_id, name, region, access_key, secret_key, type, bucket_name, bucket_path, kinesis_stream_name, config_api_key) VALUES (2, null, 'Exchange-Extractor', null, null, null, null, 'test', null, null, 'RKCNpxfRIu8TWUCKu7c7V53pIGmYmicm3o4mdMcL');
   
   INSERT INTO task_config.tasks (id, name, client_id, status, server_id, aws_config_id, import_option_id, task_type)
   VALUES (1, 'accent-sales-batch', 1, 'STOPPED', 1, 2, 1, 'EMAIL_BATCH');
   INSERT INTO task_config.tasks (id, name, client_id, status, server_id, aws_config_id, import_option_id, task_type)
   VALUES (2, 'accent-sales', 1, 'STOPPED', 1, 1, 1, 'EMAIL_STREAM');
   INSERT INTO task_config.tasks (id, name, client_id, status, server_id, aws_config_id, import_option_id, task_type)
   VALUES (3, 'accent-sugarcrm', 1, 'STOPPED', 2, 2, NULL, 'CRM');
   
   INSERT INTO task_config.subscriptions (id, client_id, account, task_id, allow_internal) VALUES (1, 1, 'exchtest7@accenttest.local', 2, false);
   INSERT INTO task_config.subscriptions (id, client_id, account, task_id, allow_internal) VALUES (2, 1, 'exchtest7@accenttest.local', 1, false);
   
```

###Build
Maven: ```mvn clean install```

###Deploy
Please make sure the database has been created, the application started once to create the ddl, and the insert ran. 

You can run the spring boot application directly. Make sure you set each server on its own port.

```java -jar target/accent-ees-task-config-service-2.0.9.jar -Dserver.port=8081 --ACCENT_EES_DB_USER="eeapp" --ACCENT_EES_DB_PASS="eeapp" --ACCENT_EES_DB_URL="jdbc:postgresql://localhost:5432/accent_ees" --ACCENT_EES_TCS_API_KEY="TEST123"```

You can alternatively use the Docker images. 




