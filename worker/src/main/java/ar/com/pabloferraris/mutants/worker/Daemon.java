package ar.com.pabloferraris.mutants.worker;

import java.io.IOException;

import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.elastic.ElasticSearchPersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.memcached.MemcachedStatsUpdater;
import ar.com.pabloferraris.mutants.persistence.rabbit.RabbitDetectionResultsConsumer;

public class Daemon {
	
	public static void main(String[] args) throws Exception {
		String rabbitConnectionString = System.getenv("RABBIT_CONNECTIONSTRING");
		String elasticConnectionString = System.getenv("ELASTIC_CONNECTIONSTRING");
		String memcachedConnectionString = System.getenv("MEMCACHED_CONNECTIONSTRING");
		
		final RabbitDetectionResultsConsumer resultsPersistence = new RabbitDetectionResultsConsumer(rabbitConnectionString, "Persistence");
		final PersistenceStrategy persistence = new ElasticSearchPersistenceStrategy(elasticConnectionString);
		resultsPersistence.Start(result -> {
			try {
				persistence.add(result);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		});

		final RabbitDetectionResultsConsumer statsUpdater = new RabbitDetectionResultsConsumer(rabbitConnectionString, "Stats");
		final PersistenceStrategy stats = new MemcachedStatsUpdater(memcachedConnectionString);
		statsUpdater.Start(result -> {
			try {
				stats.add(result);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		});
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			resultsPersistence.Stop();
			statsUpdater.Stop();
			try {
				persistence.close();
				stats.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}));
		while (resultsPersistence.isActive() && statsUpdater.isActive()) {
			Thread.sleep(100);
		}
	}
}
