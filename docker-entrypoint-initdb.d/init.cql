CREATE KEYSPACE IF NOT EXISTS sparkcassandrakeyspace WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};
CREATE TABLE IF NOT EXISTS sparkcassandrakeyspace.words1 (word text,count counter,PRIMARY KEY (word));
TRUNCATE sparkcassandrakeyspace.words1;
