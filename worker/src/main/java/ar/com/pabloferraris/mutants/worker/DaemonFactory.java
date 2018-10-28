package ar.com.pabloferraris.mutants.worker;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import ar.com.pabloferraris.mutants.persistence.elastic.ElasticSearchPersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.memcached.MemcachedStatsUpdater;
import ar.com.pabloferraris.mutants.persistence.rabbit.RabbitDetectionResultConsumer;

public final class DaemonFactory {

	public Daemon create() throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
		String rabbitConnectionString = System.getenv("RABBIT_CONNECTIONSTRING");
		String elasticConnectionString = System.getenv("ELASTIC_CONNECTIONSTRING");
		String memcachedConnectionString = System.getenv("MEMCACHED_CONNECTIONSTRING");
		if (rabbitConnectionString == null) {
			rabbitConnectionString = "amqp://localhost";
		}
		if (elasticConnectionString == null) {
			elasticConnectionString = "http://localhost";
		}
		if (memcachedConnectionString == null) {
			memcachedConnectionString = "localhost";
		}

		Daemon daemon = new Daemon();

		daemon.addWorker(new RabbitDetectionResultConsumer(rabbitConnectionString, "Persistence"),
				new ElasticSearchPersistenceStrategy(elasticConnectionString));

		daemon.addWorker(new RabbitDetectionResultConsumer(rabbitConnectionString, "Stats"),
				new MemcachedStatsUpdater(memcachedConnectionString));
		
		return daemon;
	}
	
}
