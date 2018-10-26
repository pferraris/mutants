package ar.com.pabloferraris.mutants.rest;

import org.glassfish.jersey.internal.inject.AbstractBinder;

import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.rabbit.RabbitAsyncPersistenceStrategy;

public class PersistenceBinder extends AbstractBinder {

	@Override
	protected void configure() {
		try {
			String connectionString = System.getenv("RABBIT_CONNECTIONSTRING");
			PersistenceStrategy persistence = new RabbitAsyncPersistenceStrategy(connectionString);
			bind(persistence).to(PersistenceStrategy.class);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
