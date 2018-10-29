package ar.com.pabloferraris.mutants.rest;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.glassfish.jersey.internal.inject.AbstractBinder;

import ar.com.pabloferraris.mutants.persistence.PersistenceStrategy;
import ar.com.pabloferraris.mutants.persistence.rabbit.RabbitAsyncPersistenceStrategy;

public class PersistenceBinder extends AbstractBinder {

	@Override
	protected void configure() {
		String connectionString = System.getenv("RABBIT_CONNECTIONSTRING");
		if (connectionString == null) {
			connectionString = "amqp://admin:admin@localhost";
		}
		try {
			PersistenceStrategy persistence = new RabbitAsyncPersistenceStrategy(connectionString);
			bind(persistence).to(PersistenceStrategy.class);
		} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
